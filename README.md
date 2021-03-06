# InvUnload

### Buy me covfefe ❤️
```
BTC     bc1qjqzkrfupcrgtzpeu0pmut24vq8tfzs9rqe6458
ETH     0x799b3b5520525CDd95d1D5C7Ba1a2Ee6037B1bFE
ADA     addr1q8mz3z7cw4jz9dacvpz6dpw2c6xke6nv8vk6rfnt7mkaat8vgnu796g5vrarn4pjgpdqkare9zryx645e25wcae8636q97typg
XRP     r3Bpcyp8zVNkaDzpppdRTuSXSvxAUJXAVj
LTC     ltc1qpja2nr6x9nz3q3ya3ec6ec5hxvm8dz52urn39z
BCH     1NAteBJF7wKw8BdzLJ6YE65ja1ZAHf68jf
DOGE    DL4VNBx6EGuPcgnJrfgxok9tTvcbVGKx3R
XMR     89S6qYdMJyZZ8ddKtFqTzGhuDZxJkNcmL9L6KzTSw7AeQos1uku2GBhBaHLUYtgv4TQRRQuNF4FixAu6geKC2r25NyWZj2Q
DASH    XtffD9gZFDKWWpabMyAi8sF9EeDREH5dUy
DCR     DsSAqeDekTCvbd84GkAofHyutrFrFDX1EnD
ZEC     t1P336iRRMM6Yu2wTzXJmjm6p4RgNAQkgsM
STRAX   SVfFcCZtQ8yMSMxc2K8xzFr4psHpGpnKNT 
```

Automatically puts your stuff into the right chests

You don't have time to open every chest in your storage room to tidy up your inventory?
Fear no more! You have no more excuses for having chests full of random garbage!

<p align="center">
  <img src="https://api.jeff-media.de/invunload/spigotmc/img/invunload128.png"/>
</p>

InvUnload does two brilliant things:

When you enter /unload, it checks if there are chests nearby. For each chest, the player's inventory (except hotbar) will be searched for matching items. If there are any, they will be put into the chest.

When you enter /dump, it will put all items from the player's inventory (except hotbar) into nearby chests. If possible, they will be put into chests already containing matching items.

## Build

You will need maven to build InvUnload. You will also have to download these dependencies:

- PlotSquared 4 ([LINK](https://www.spigotmc.org/resources/plotsquared-v4-v5-out-now.1177/))
- Spartan API ([LINK](https://vagdedes.com/spartan/api/SpartanAPI.jar)).

Just create a folder called "lib" inside the maven project and put those two .jar-files inside. No you can run ``mvn install``
