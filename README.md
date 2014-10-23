# settlers

This is a Settlers 3 clone written in JAVA. It runs on PC (Windows/Linux) and Android.

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
