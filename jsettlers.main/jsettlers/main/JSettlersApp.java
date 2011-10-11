package jsettlers.main;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JFrame;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.JOGLPanel;
import jsettlers.graphics.JoglLibraryPathInitializer;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.input.GuiInterface;
import jsettlers.logic.algorithms.construction.ConstructMarksCalculator;
import jsettlers.logic.algorithms.landmarks.LandmarksCorrectingThread;
import jsettlers.logic.algorithms.path.wrapper.PathfinderWrapper;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.management.GameManager;
import jsettlers.logic.management.MaterialJobPart;
import jsettlers.logic.map.hex.HexGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.player.ActivePlayer;
import jsettlers.logic.timer.Timer100Milli;
import network.INetworkManager;
import network.NetworkManager;
import network.NullNetworkManager;
import random.RandomSingleton;

public class JSettlersApp {

	private static final int[] PRELOAD_FILES = new int[] { 2, 0, 1, 3, 10, 11, 12, 13 };

	private static final byte PLAYERS = 3;

	static { // sets the native library path for the system dependent jogl libs
		JoglLibraryPathInitializer.initLibraryPath();
	}

	/**
	 * @param args
	 *            input arguments
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws InterruptedException, UnknownHostException, IOException {

		INetworkManager manager;
		if (args.length == 0 || args[0].equalsIgnoreCase("single")) {
			manager = new NullNetworkManager();
		} else {
			if (args[0].equalsIgnoreCase("host")) {
				manager = new NetworkManager(60);
			} else {
				manager = new NetworkManager("localhost", 60);
			}
		}

		manager.waitForInit();

		ActivePlayer.instantiate((byte) 0);

		String map = null;
		if (args.length == 2) {
			map = args[1];
		}

		new JSettlersApp(manager, map);
	}

	private static void initTests() {
		for (int x = 20; x < 60; x += 2) {
			setMovable(x, 5, EMovableType.BEARER, 1);
		}

		GameManager.requestMaterial(new MaterialJobPart(EMaterialType.PICK, new ShortPoint2D((short) 60, (short) (Constants.HEIGHT - 30)), (byte) 1));

		for (int i = 0; i < 40; i++) {
			for (int h = 0; h < 40; h++)
				setMovable(100 + i, 120 + h, EMovableType.PIONEER, 1);
		}
	}

	private static void setMovable(int x, int y, EMovableType type, int player) {
		ShortPoint2D pos = new ShortPoint2D((short) x, (short) (Constants.HEIGHT - y));
		HexGrid.get().placeNewMovable(pos, new Movable(HexGrid.get(), HexGrid.get().getTile(pos), type, (byte) player));
	}

	private JSettlersApp(INetworkManager manager, String map) {

		JFrame jsettlersWnd = new JFrame("jsettlers");

		JOGLPanel panel = new JOGLPanel();
		jsettlersWnd.add(panel.getJOGLJPanel());
		panel.getJOGLJPanel().requestFocusInWindow();

		jsettlersWnd.pack();
		jsettlersWnd.setSize(1200, 800);
		jsettlersWnd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jsettlersWnd.setVisible(true);
		jsettlersWnd.setLocationRelativeTo(null);

		ProgressConnector progress = panel.showProgress();

		// Schedule image loading while the other stuff is waiting
		ImageProvider provider = ImageProvider.getInstance();
		provider.addLookupPath(new File("/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/GFX"));
		provider.addLookupPath(new File("D:/Games/Siedler3/GFX"));
		provider.addLookupPath(new File("C:/Program Files/siedler 3/GFX"));
		for (int i : PRELOAD_FILES) {
			provider.preload(i);
		}
		RandomSingleton.load(2132134L);

		Timer100Milli.start();

		GameManager.start(PLAYERS);

		progress.setProgressState(EProgressState.LOADING_MAP);
		if (map != null && map.equalsIgnoreCase("noRandom")) {
			HexGrid.create((short) 400, (short) 400);
		} else {
			HexGrid.createRandom("test", PLAYERS, RandomSingleton.get());

		}

		PathfinderWrapper.startPathfinder(HexGrid.get());

		ConstructMarksCalculator.startCalculator(HexGrid.get(), (byte) 0);

		LandmarksCorrectingThread.startThread(HexGrid.get());

		progress.setProgressState(EProgressState.LOADING_IMAGES);
		for (int i : PRELOAD_FILES) {
			provider.waitForPreload(i);
		}

		MapInterfaceConnector connector = panel.showHexMap(HexGrid.get(), ActivePlayer.get().getStatistics());
		new GuiInterface(connector, manager);

		// initTests();

		manager.startGameTimer();
	}
}
