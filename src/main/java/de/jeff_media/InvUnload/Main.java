package de.jeff_media.InvUnload;

import java.io.File;
import java.util.*;

import de.jeff_media.ChestSortAPI.ChestSort;
import de.jeff_media.ChestSortAPI.ChestSortAPI;
import de.jeff_media.InvUnload.Hooks.CoreProtectHook;
import de.jeff_media.InvUnload.Hooks.InventoryPagesHook;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.jeff_media.InvUnload.Hooks.ChestSortHook;
import de.jeff_media.InvUnload.Hooks.PlotSquaredHook;
import de.jeff_media.PluginUpdateChecker.PluginUpdateChecker;

public class Main extends JavaPlugin implements Listener {

	@Nullable
	public ChestSortAPI chestSortAPI;
    CoreProtectHook coreProtectHook;

    String mcVersion; // 1.13.2 = 1_13_R2
						// 1.14.4 = 1_14_R1
						// 1.8.0 = 1_8_R1
	int mcMinorVersion; // 14 for 1.14, 13 for 1.13, ...

	private int currentConfigVersion = 26;

	protected Messages messages;
	protected BlockUtils blockUtils;

	protected int defaultChestRadius = 10;
	protected int maxChestRadius = 20;

	public boolean usingMatchingConfig = true;

	protected PluginUpdateChecker updateChecker;
	protected ChestSortHook chestSortHook;
	protected PlotSquaredHook plotSquaredHook;
	protected InventoryPagesHook inventoryPagesHook;
	protected Visualizer visualizer;
	protected GroupUtils groupUtils;

	CommandUnload commandUnload;
	CommandUnloadinfo commandUnloadInfo;
	CommandSearchitem commandSearchitem;
	CommandBlacklist commandBlacklist;
	MaterialTabCompleter materialTabCompleter;

	HashMap<UUID,PlayerSetting> playerSettings;

	private int updateCheckInterval = 86400;

	@Override
	public void onDisable() {
		saveAllPlayerSettings();
	}

	public void onEnable() {

		Metrics metrics = new Metrics(this,3156);

		String tmpVersion = getServer().getClass().getPackage().getName();
		mcVersion = tmpVersion.substring(tmpVersion.lastIndexOf('.') + 1);
		tmpVersion = mcVersion.substring(mcVersion.indexOf("_") + 1);
		mcMinorVersion = Integer.parseInt(tmpVersion.substring(0, tmpVersion.indexOf("_")));

		reloadCompleteConfig(false);

		ChestSort chestSort = (ChestSort) getServer().getPluginManager().getPlugin("ChestSort");
		if (getConfig().getBoolean("use-chestsort") == false ||chestSort == null) {
			//getLogger().warning("Warning: ChestSort is not installed.");
		} else {
			chestSortAPI = chestSort.getAPI();
			getLogger().info("Succesfully hooked into ChestSort");
		}
		
		chestSortHook = new ChestSortHook(this);
		plotSquaredHook = new PlotSquaredHook(this);
		coreProtectHook = new CoreProtectHook(this);
		inventoryPagesHook = new InventoryPagesHook(this);
		
		registerCommands();
	}

	private void createConfig() {
		
		saveResource("groups.example.yml", true);

		// This saves the config.yml included in the .jar file, but it will not
		// overwrite an existing config.yml
		this.saveDefaultConfig();
		reloadConfig();
		//System.out.println("DEBUG: Current config version: "+getConfig().getInt("config-version",0));
		if (getConfig().getInt("config-version", 0) != currentConfigVersion) {
			//System.out.println("DEBUG: Current config version: "+getConfig().getInt("config-version",0));

			showOldConfigWarning();
			ConfigUpdater configUpdater = new ConfigUpdater(this);
			configUpdater.updateConfig();
			configUpdater = null;
			usingMatchingConfig = true;
			// createConfig();
		}

		setDefaultConfigValues();

	}

	private void setDefaultConfigValues() {
		// If you use an old config file with missing options, the following default
		// values will be used instead
		// for every missing option.
		getConfig().addDefault("max-chest-radius", 20);
		maxChestRadius = getConfig().getInt("max-chest-radius");
		
		getConfig().addDefault("default-chest-radius", 10);
		defaultChestRadius = getConfig().getInt("default-chest-radius");

		getConfig().addDefault("unload-before-dumping", true);
		
		getConfig().addDefault("check-interval", 4);
		updateCheckInterval = (int) (getConfig().getDouble("check-interval")*60*60);
		
		getConfig().addDefault("use-chestsort", true);
		
		getConfig().addDefault("use-playerinteractevent", true);
		getConfig().addDefault("use-coreprotect",true);
		getConfig().addDefault("use-plotsquared", true);
		getConfig().addDefault("plotsquared-allow-when-trusted", true);
		getConfig().addDefault("plotsquared-allow-outside-plots", true);
		
		getConfig().addDefault("spawn-particles", true); 
		getConfig().addDefault("particle-type", "SPELL_WITCH");
		getConfig().addDefault("particle-count", 100);
		
		getConfig().addDefault("always-show-summary", true);
		
		getConfig().addDefault("laser-animation", true);
		getConfig().addDefault("laser-default-duration", 5);
		getConfig().addDefault("laser-max-distance", 30);
		getConfig().addDefault("laser-max-distance", 50);
		getConfig().addDefault("laser-moves-with-player", false);

		getConfig().addDefault("strict-tabcomplete",true);
		
		if(!EnumUtils.particleExists(getConfig().getString("particle-type"))) {
			getLogger().warning("Specified particle type \"" + getConfig().getString("particle-type") + "\" does not exist! Please check your config.yml");
			getConfig().set("error-particles",true);
		}
		if(!EnumUtils.soundExists(getConfig().getString("sound-effect"))) {
			getLogger().warning("Specified sound effect \"" + getConfig().getString("sound-effect") + "\" does not exist! Please check your config.yml");
			getConfig().set("error-sound", true);
		}
	
	}

	private void showOldConfigWarning() {
		getLogger().warning("==============================================");
		getLogger().warning("You were using an old config file. InvUnload");
		getLogger().warning("has updated the file to the newest version.");
		getLogger().warning("Your changes have been kept.");
		getLogger().warning("==============================================");
	}

	private void registerCommands() {
		commandUnload = new CommandUnload(this);
		commandUnloadInfo = new CommandUnloadinfo(this);
		commandSearchitem = new CommandSearchitem(this);
		commandBlacklist = new CommandBlacklist(this);
		materialTabCompleter= new MaterialTabCompleter(this);
		getCommand("unload").setExecutor(commandUnload);
		getCommand("dump").setExecutor(commandUnload);
		getCommand("unloadinfo").setExecutor(commandUnloadInfo);
		getCommand("searchitem").setExecutor(commandSearchitem);
		getCommand("searchitem").setTabCompleter(materialTabCompleter);
		getCommand("blacklist").setExecutor(commandBlacklist);
		getCommand("blacklist").setTabCompleter(commandBlacklist);
	}
	
	private void initUpdateChecker() {
		// Check for updates (async, of course)
		// When set to true, we check for updates right now, and every X hours (see
		// updateCheckInterval)
		if (getConfig().getString("check-for-updates", "true").equalsIgnoreCase("true")) {
		updateChecker.check(updateCheckInterval);

		} // When set to on-startup, we check right now (delay 0)
		else if (getConfig().getString("check-for-updates", "true").equalsIgnoreCase("on-startup")) {
			updateChecker.check();
		}
	}

	PlayerSetting getPlayerSetting(Player p) {
		if(playerSettings.containsKey(p.getUniqueId())) {
			return playerSettings.get(p.getUniqueId());
		}

		PlayerSetting setting;
		if(getPlayerFile(p.getUniqueId()).exists()) {
			setting = new PlayerSetting(getPlayerFile(p.getUniqueId()));
		} else {
			setting = new PlayerSetting();
		}

		playerSettings.put(p.getUniqueId(),setting);

		return setting;
	}

	File getPlayerFile(UUID uuid) {
		return new File(getDataFolder()+File.separator+"playerdata"+File.separator+uuid.toString()+".yml");
	}

	public void reloadCompleteConfig(boolean reload) {
		reloadConfig();
		createConfig();
		new File(getDataFolder()+File.separator+"playerdata").mkdirs();
		if(reload) {
			if (updateChecker != null) {
				updateChecker.stop();
			}
			saveAllPlayerSettings();
		}
		messages = new Messages(this);
		updateChecker = new PluginUpdateChecker(this,"https://api.jeff-media.de/invunload/invunload-latest-version.txt","https://www.spigotmc.org/resources/1-12-1-15-invunload.60095/","https://github.com/JEFF-Media-GbR/Spigot-InvUnloadPlus/blob/master/CHANGELOG.md","https://chestsort.de/donate");
		initUpdateChecker();
		blockUtils = new BlockUtils(this);
		visualizer = new Visualizer(this);
		File groupsFile = new File(this.getDataFolder()+File.separator+"groups.yml");
		groupUtils = new GroupUtils(this,groupsFile);
		getServer().getPluginManager().registerEvents(new PlayerListener(this),this);
		playerSettings = new HashMap<>();
	}

	private void saveAllPlayerSettings() {
		for(Map.Entry<UUID,PlayerSetting> entry : playerSettings.entrySet()) {
			entry.getValue().save(getPlayerFile(entry.getKey()),this);
		}
	}

}
