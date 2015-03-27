# JSettlers

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
5. Describe what problem you experienced so that we can easily understand it.

Many thanks in advance for helping to improve this game!


## Playing JSettlers

In order to play the game, you need to have the "GFX" and "SND" folders of the original version of "The Settlers 3" as obtained by installing the original "The Settlers 3" game (DEMO version also works).

After that, follow the detailed installation instructions for you platform.

### Windows, Linux, Mac OS
1. Install "The Settlers III" or a demo version of it. Don't worry, if it is not running on your OS, we only need the graphics and sound files.
2. Download the newest stable release of JSettlers*.zip / JSettlers*.tar.bz2 (this also includes the MapEditor).
3. Unpack the downloaded archive to wherever you want JSettlers' installation to be.
4. Open the unpacked file named "config.prp" with a text editor. Update the line starting with 
```
settlers-folder=. 
```
to 
```
settlers-folder=<SETTLERS-3-INSTALLATION-PATH>
```
where ```<SETTLERS-3-INSTALLATION-PATH>``` is the path to your original "The Settlers 3" installation. (Meaning a folder containing the original's "GFX" and "SND" folders.) **Important Note**: Use slashes as path delimiters. 
5. Run the "JSettlers.jar" file and enjoy the game.

### Android
1. Enable installation of Apps from "Unknown Sources".
2. Copy the "GFX" and the "SND" folders of your original "The Settlers 3" installation into a folder called "JSettlers" on your device. The "JSettlers" folder must be located in the root directory of your internal storage (alongside folders like "Download" or "DCIM") or your external storage (e.g. memory card).
3. Download the newest stable JSettlers-Android_v*.apk onto your Android device.
4. Install JSettlers by running the downloaded file.

## Developing on this project

This project can be built using eclipse. 
To work on it, clone this repository and then import all projects as existing projects into your workspace. Dependencies are added automatically.

## Project structure.

_go.graphics_

The base graphics abstraction interface. Contains all code used on all plattforms

_go.graphics.android_

The Android graphics implementation

_go.graphics.nativegl_

A native OpenGL implementation for Linux. No external libraries are needed here. Still needs some work (e.g. has no text support so far)

_go.graphics.swing_

The default Linux/Windows drawing library. It uses Swing to create a GL drawing window.

_jsettlers.algorithms_

Alfgorithms used by this project. This includes pathfinding, fog of war, marker generation and more.

_jsettlers.buildingcreator_

A building editor. We use this in to help implement new buildings. Not included in a jsettlers release.

_jsettlers.common_

This project contains all the base abstractions needed by most other modules. This includes the map interfaces, building specifications, image tables and some utilities.

_jsettlers.graphics_

This is the main graphics project. It contains everything needed to draw the in game map.

_jsettlers.graphics.androidui_

This is the GUI overlay used on Android.

_jsettlers.graphics.swing_

This is the GUI overlay used on PC. This does not really require swing any more, it also works with native gl.

_jsettlers.logic_

The core logic. This manages the game grid and all game state.

_jsettlers.main.android_

Our Android launch code.

_jsettlers.main.swing_

Our Linux/Windows launch code.

_jsettlers.mapcreator_ 

A standalone mapcreator app that lets you design your own maps.

_jsettlers.tests_

Test cases. Not included in a game build.
