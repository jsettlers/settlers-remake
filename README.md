# JSettlers   [![Build Status](https://travis-ci.org/jsettlers/settlers-remake.svg?branch=master)](https://travis-ci.org/jsettlers/settlers-remake)

This project intends to create a remake of the famous strategy game "The Settlers 3" published by Blue Byte in 1998. The project is developed in Java and runs on PC (Windows/Linux), Mac and Android. More information can be found on the project's website at [www.settlers-android-clone.com](http://www.settlers-android-clone.com)

### Warning: Alpha Status
The game is currently in an **alpha** status! Therefore bugs, frequent changes making saved games invalid and server abortions need to be expected. Nevertheless we will try to minimize trouble.

### Found a Bug? Report it!
If you experience troubles / find a bug, help us fix it. The JSettlers game creates log files of your games that are essential for debugging. Therefore always include the following information in a bug report:
1. What OS and JRE are you using? If it is Android, please state your Android version.
2. Revision of your build:
  1. In the PC/Mac version this can be found in the head of the window as "JSettlers - commit: XXXXXXX".
  2. In the Android version, the info is displayed on the start screen in the lower right corner as "build: XXXXXXX".
3. In the game's folder, there is a subfolder ```resources/logs/``` containing a folder of log files for every game you played. Please package the folder belonging to your game where you experienced the bug (identifiable by date and map name). This folder contains the following files:
  1. *_out.log: This is the console / debugging output of the game.
  2. *_replay.log: This file contains all game relevant actions you and other players did in the game (e.g. place a building, send soldiers somewhere). With this game, we can recalculate your game and debug it to find the exact source of your trouble.
4. Specify the game time when you first experienced the bug (the game time can be found in the upper right corner while playing).
5. If you were playing a custom map, please attach the map. Without it, replaying the game won't be possible.
6. Describe what problem you experienced so that we can easily understand it.

Many thanks in advance for helping to improve this game!


## Playing JSettlers

In order to play the game, you need to have the "GFX" and "SND" folders of the original version of "The Settlers 3" as obtained by installing the original "The Settlers 3" game (DEMO version also works).

Furthermore, you need an up to date installation of [Java 1.7 or 1.8](http://java.com/). The Java Runtime Environment (JRE) is needed to run JSettlers, as it is written in the programming language Java.

After that, follow the detailed installation instructions for you platform.

### Windows, Linux, Mac OS
1. Install "The Settlers III" or a demo version ([Settlers III Amazons Demo](http://www.siedler-maps.de/downloads.php?action=download&downloadid=41)) of it. Don't worry, if it is not running on your OS, we only need the graphics and sound files. In order to get them, you can also unzip the Amazons Demo exe file (yes: unzip the .exe) and copy the folders `Gfx` and `Snd` into an empty folder on your computer.
2. Download the newest stable [release of JSettlers*.zip / JSettlers*.tar.bz2](https://github.com/jsettlers/settlers-remake/releases) (this also includes the MapEditor).
3. Unpack the downloaded archive to wherever you want JSettlers' installation to be.
5. Run the "JSettlers.jar" file.
  1. On the first start, the game will ask you for the folder where you've installed / unziped (see step 1) the original Settlers III. Please select the respective folder and continue.
  2. Have fun and enjoy the game!
6. Please have a look at the [manual](https://github.com/jsettlers/settlers-remake/wiki/JSettlers-Manual). The current state of the game lacks some controls known from the original, but also contains new ways to do things, which you shouldn't miss.

#### Arch Linux
1. Install [jsettlers-git](https://aur.archlinux.org/packages/jsettlers-git/) from the AUR.
2. Optionally: Install [settlers3-demo-data](https://aur.archlinux.org/packages/settlers3-demo-data/) if you don't own an original The Settlers III and select the following folder when the game asks you: /usr/share/jsettlers/s3
3. You can start the game from the system-menu or with the commands "jsettlers" and "jsettlers-mapcreator".
4. See instructions above

#### Configuration Flags
As described before, the game's UI is still lacking a lot of features. That's why we have to offer some configurations via an options file. You can find a default `options.prp` file aside the `JSettlers.jar` file after you unpacked the archive.

When opening the file, you will see several options that can be enabled by uncommenting them (remove the # at the beginning of the respective line). This is also described in the file.

**Possible configurations include:**
- all-ai: Let all players be played by the AI. You will be able to watch all AI players and to "assist" them during the game.
- fixed-ai-type=YYYYY: Option to specify an AI type that shall be used for all AI players. The default behavior is to use a the weakest AI type for the first player and increase the difficulty for every player. Possible values: ROMAN_VERY_EASY, ROMAN_EASY, ROMAN_HARD, ROMAN_VERY_HARD
- disable-ai: If this flag is enabled, no AI players will be present in single player games.
- locale: If you want to test a different localization than your systems default, it can be specify with this option. The value should look like: en_en.

**Command line flags**
All the options above can also be specified as command line options. For this, you need to prepend every one of them with a double dash. Therefore the option `fixed-ai-type=YYYYY` should be specified as `--fixed-ai-type=YYYYY`.


### Android
1. Enable installation of Apps from "Unknown Sources".
2. Copy the "GFX" and the "SND" folders of your original "The Settlers 3" installation into a folder called "JSettlers" on your device. The "JSettlers" folder must be located in the root directory of your internal storage (alongside folders like "Download" or "DCIM") or your external storage (e.g. memory card).
3. Download the newest stable JSettlers-Android_v*.apk onto your Android device.
4. Install JSettlers by running the downloaded file.

## Build instructions and developer's guide
The [build instructions](https://github.com/jsettlers/settlers-remake/wiki/Compiling-using-gradle) and the [developer's guide](https://github.com/jsettlers/settlers-remake/wiki/Developer's%20Guide) can be found in our wiki. Alternatively, you can follow also the [Docker based build instructions](./docs/build-using-docker.md) for building the desktop variant and the dedicated server.

## Getting in Touch
Besides the possibility to report bugs on Github you can also join our [JSettlers Discord](https://discord.gg/2hVV4u6). Here you can discuss on development questions and find other players to meet with.
