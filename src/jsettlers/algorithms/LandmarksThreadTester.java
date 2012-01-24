package jsettlers.algorithms;

import go.graphics.sound.SoundPlayer;
import go.graphics.swing.AreaContainer;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;

import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.JOGLPanel;
import jsettlers.graphics.JoglLibraryPathInitializer;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.SelectAction;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.logic.algorithms.landmarks.ILandmarksThreadGrid;
import jsettlers.logic.algorithms.landmarks.LandmarksCorrectingThread;
import jsettlers.main.swing.ResourceProvider;

public class LandmarksThreadTester {
	static { // sets the native library path for the system dependent jogl libs
		JoglLibraryPathInitializer.initLibraryPath();

		ImageProvider provider = ImageProvider.getInstance();
		provider.addLookupPath(new File("/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/GFX"));
		provider.addLookupPath(new File("D:/Games/Siedler3/GFX"));
		provider.addLookupPath(new File("C:/Program Files/siedler 3/GFX"));
	}

	protected static final int WIDTH = 20;
	protected static final int HEIGHT = 20;
	private static Map map;
	private static LandmarksCorrectingThread thread;

	public static void main(String args[]) {

		JOGLPanel content = new JOGLPanel(new SoundPlayer() {
			
			@Override
			public void playSound(int sound, float lvolume, float rvolume) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public int load(short[] loadSound) {
				// TODO Auto-generated method stub
				return 0;
			}
		});

		ResourceManager.setProvider(new ResourceProvider());

		map = new Map();

		MapInterfaceConnector connector = content.showGameMap(map, null);
		connector.addListener(new IMapInterfaceListener() {

			@Override
			public void action(Action action) {
				if (action.getActionType() == EActionType.SELECT_POINT) {
					System.out.println("clicked: " + ((SelectAction) action).getPosition());
				}
			}
		});

		JFrame jsettlersWnd = new JFrame("landmarksthreadtester");
		AreaContainer panel = new AreaContainer(content.getArea());
		panel.setPreferredSize(new Dimension(640, 480));
		jsettlersWnd.add(panel);
		panel.requestFocusInWindow();

		jsettlersWnd.pack();
		jsettlersWnd.setSize(1200, 800);
		jsettlersWnd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jsettlersWnd.setVisible(true);
		jsettlersWnd.setLocationRelativeTo(null);

		thread = new LandmarksCorrectingThread(map);

		test1();
		test2();
	}

	private static void test2() {
		map.setBlocked(8, 11, true);
		map.setBlocked(8, 13, true);

		setPartition(7, 11, 1);
		setPartition(7, 10, 1);
		setPartition(8, 10, 1);
		setPartition(9, 11, 1);
		setPartition(9, 12, 1);
		setPartition(8, 12, 1);

		setPartition(7, 12, 1);
	}

	private static void test1() {
		for (short x = 3; x < 6; x++) {
			for (short y = 5; y < 7; y++) {
				map.setBlocked(x, y, true);
			}
		}

		setPartition(2, 4, 1);
		setPartition(2, 5, 1);
		setPartition(2, 6, 1);

		setPartition(6, 5, 1);
		setPartition(6, 6, 1);
		setPartition(6, 7, 1);

		setPartition(3, 4, 1);
		setPartition(4, 4, 1);
		setPartition(5, 4, 1);

		setPartition(3, 7, 1);
		setPartition(4, 7, 1);
		setPartition(5, 7, 1);
	}

	private static void setPartition(int x, int y, int partition) {
		map.setPartitionAndPlayerAt((short) x, (short) y, (short) partition);
		ISPosition2D pos = new ShortPoint2D(x, y);
		thread.addLandmarkedPosition(pos);
	}

	// private static void printMap(Map map) {
	// for (short y = HEIGHT - 1; y >= 0; y--) {
	// printSpaces(y * 10);
	// for (short x = 0; x < WIDTH; x++) {
	// System.out.print("      (" + x + "|" + y + ")");
	// if (map.isBlocked(x, y)) {
	// System.out.print("b");
	// } else {
	// System.out.print(" ");
	// }
	// System.out.print("|" + map.getPartitionAt(x, y) + "      ");
	// }
	// System.out.println();
	// }
	// }

	// private static void printSpaces(int spaces) {
	// for (int i = 0; i < spaces; i++) {
	// System.out.print(" ");
	// }
	// }

	private static class Map implements ILandmarksThreadGrid, IGraphicsGrid {
		short[][] partitions = new short[WIDTH][HEIGHT];
		boolean[][] blocked = new boolean[WIDTH][HEIGHT];

		@Override
		public void setPartitionAndPlayerAt(short x, short y, short partition) {
			this.partitions[x][y] = partition;
		}

		@Override
		public boolean isInBounds(short x, short y) {
			return 0 <= x && x < WIDTH && 0 <= y && y < HEIGHT;
		}

		@Override
		public boolean isBlocked(short x, short y) {
			return blocked[x][y];
		}

		@Override
		public short getPartitionAt(short x, short y) {
			return partitions[x][y];
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
		public Color getDebugColorAt(int x, int y) {
			return new Color(isBlocked((short) x, (short) y) ? 1 : 0, 0, getPartitionAt((short) x, (short) y) / 2f, 1);
		}

		@Override
		public boolean isBorder(int x, int y) {
			return false;
		}

		@Override
		public byte getPlayerAt(int x, int y) {
			return 0;
		}

		@Override
		public byte getVisibleStatus(int x, int y) {
			return CommonConstants.FOG_OF_WAR_VISIBLE;
		}

		@Override
		public boolean isFogOfWarVisible(int x, int y) {
			return true;
		}

		@Override
		public void setBackgroundListener(IGraphicsBackgroundListener backgroundListener) {
		}

	}
}
