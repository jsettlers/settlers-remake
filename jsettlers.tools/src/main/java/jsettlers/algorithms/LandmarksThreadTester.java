/*******************************************************************************
 * Copyright (c) 2015 - 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.algorithms;

import java.io.IOException;

import jsettlers.TestToolUtils;
import jsettlers.algorithms.landmarks.EnclosedBlockedAreaFinderAlgorithm;
import jsettlers.algorithms.landmarks.IEnclosedBlockedAreaFinderGrid;
import jsettlers.algorithms.traversing.area.IAreaVisitor;
import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.EDebugColorModes;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.movable.IMovable;
import jsettlers.graphics.action.PointAction;
import jsettlers.main.swing.lookandfeel.JSettlersLookAndFeelExecption;
import jsettlers.main.swing.resources.SwingResourceLoader;

public class LandmarksThreadTester {
	private static final int WIDTH = 20;
	private static final int HEIGHT = 20;
	private static Map map;

	public static void main(String args[]) throws JSettlersLookAndFeelExecption, IOException, SwingResourceLoader.ResourceSetupException {
		map = new Map();

		IMapInterfaceConnector connector = TestToolUtils.openTestWindow(map);
		connector.addListener(action -> {
			if (action.getActionType() == EActionType.SELECT_POINT) {
				System.out.println("clicked: " + ((PointAction) action).getPosition());
			}
		});

		test1();
		test2();
	}

	private static void test2() {
		map.setBlocked(8, 11, true);
		map.setBlocked(8, 13, true);

		setPlayer(7, 11, 1);
		setPlayer(7, 10, 1);
		setPlayer(8, 10, 1);
		setPlayer(9, 11, 1);
		setPlayer(9, 12, 1);
		setPlayer(8, 12, 1);

		setPlayer(7, 12, 1);
	}

	private static void test1() {
		for (short x = 3; x < 6; x++) {
			for (short y = 5; y < 7; y++) {
				map.setBlocked(x, y, true);
			}
		}

		setPlayer(2, 4, 1);
		setPlayer(2, 5, 1);
		setPlayer(2, 6, 1);

		setPlayer(6, 5, 1);
		setPlayer(6, 6, 1);
		setPlayer(6, 7, 1);

		setPlayer(3, 4, 1);
		setPlayer(4, 4, 1);
		setPlayer(5, 4, 1);

		setPlayer(3, 7, 1);
		setPlayer(4, 7, 1);
		setPlayer(5, 7, 1);
	}

	private static void setPlayer(int x, int y, int partition) {
		map.setPlayerAt((short) x, (short) y, (byte) partition);
		EnclosedBlockedAreaFinderAlgorithm.checkLandmark(map, x, y);
	}

	// private static void printMap(Map map) {
	// for (short y = HEIGHT - 1; y >= 0; y--) {
	// printSpaces(y * 10);
	// for (short x = 0; x < WIDTH; x++) {
	// System.out.print(" (" + x + "|" + y + ")");
	// if (map.isPioneerBlockedAndWithoutTowerProtection(x, y)) {
	// System.out.print("b");
	// } else {
	// System.out.print(" ");
	// }
	// System.out.print("|" + map.getPlayerIdAt(x, y) + " ");
	// }
	// System.out.println();
	// }
	// }

	// private static void printSpaces(int spaces) {
	// for (int i = 0; i < spaces; i++) {
	// System.out.print(" ");
	// }
	// }

	private static class Map implements IEnclosedBlockedAreaFinderGrid, IGraphicsGrid {
		byte[][] players = new byte[WIDTH][HEIGHT];
		boolean[][] blocked = new boolean[WIDTH][HEIGHT];

		public void setPlayerAt(int x, int y, byte player) {
			this.players[x][y] = player;
		}

		@Override
		public boolean isInBounds(int x, int y) {
			return 0 <= x && x < WIDTH && 0 <= y && y < HEIGHT;
		}

		@Override
		public boolean isPioneerBlockedAndWithoutTowerProtection(int x, int y) {
			return blocked[x][y];
		}

		@Override
		public byte getPlayerIdAt(int x, int y) {
			return players[x][y];
		}

		void setBlocked(int x, int y, boolean blocked) {
			this.blocked[x][y] = blocked;
		}

		@Override
		public short getHeight() {
			return HEIGHT;
		}

		@Override
		public short getWidth() {
			return WIDTH;
		}

		@Override
		public boolean isOfPlayerOrBlocked(int x, int y, byte playerId) {
			return players[x][y] == playerId || blocked[x][y];
		}

		@Override
		public IAreaVisitor getDestroyBuildingOrTakeOverVisitor(byte newPlayer) {
			return (x, y) -> {
				if (blocked[x][y]) {
					players[x][y] = newPlayer;
				}
				return true;
			};
		}

		@Override
		public IMovable getMovableAt(int x, int y) {
			return null;
		}

		@Override
		public IMapObject getMapObjectsAt(int x, int y) {
			return null;
		}

		@Override
		public byte getHeightAt(int x, int y) {
			return 0;
		}

		@Override
		public ELandscapeType getLandscapeTypeAt(int x, int y) {
			return ELandscapeType.GRASS;
		}

		@Override
		public int getDebugColorAt(int x, int y, EDebugColorModes debugColorMode) {
			return Color.getARGB(isPioneerBlockedAndWithoutTowerProtection((short) x, (short) y) ? 1 : 0, 0,
					getPlayerIdAt((short) x, (short) y) / 2f, 1);
		}

		@Override
		public boolean isBorder(int x, int y) {
			return false;
		}

		@Override
		public int nextDrawableX(int x, int y, int maxX) {
			return x + 1;
		}

		@Override
		public byte getVisibleStatus(int x, int y) {
			return CommonConstants.FOG_OF_WAR_VISIBLE;
		}

		@Override
		public void setBackgroundListener(IGraphicsBackgroundListener backgroundListener) {
		}

		@Override
		public IPartitionData getPartitionData(int x, int y) {
			return null;
		}

		@Override
		public boolean isBuilding(int x, int y) {
			return false;
		}
	}
}
