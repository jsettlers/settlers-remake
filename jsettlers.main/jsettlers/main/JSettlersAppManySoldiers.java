package jsettlers.main;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;

import javax.swing.JFrame;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.JOGLPanel;
import jsettlers.graphics.JoglLibraryPathInitializer;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
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

public class JSettlersAppManySoldiers {
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

		RandomSingleton.load(10001L);

		Timer100Milli.start();

		GameManager.start(PLAYERS);
		HexGrid.create(Constants.WIDTH, Constants.HEIGHT, true);
		PathfinderWrapper.startPathfinder(HexGrid.get());

		ConstructMarksCalculator.startCalculator(HexGrid.get(), (byte) 0);

		new JSettlersAppManySoldiers(manager);

		LandmarksCorrectingThread.startThread(HexGrid.get());

		initTests();

		Thread.sleep(3000);

		manager.startGameTimer();
	}

	private static void initTests() {

		GameManager.requestMaterial(new MaterialJobPart(EMaterialType.PICK, new ShortPoint2D((short) 60, (short) (Constants.HEIGHT - 30)), (byte) 1));

		Random random = new Random(1234);
		EMovableType[] values = new EMovableType[] { EMovableType.BOWMAN_L3, EMovableType.SWORDSMAN_L3, EMovableType.PIKEMAN_L3,
				EMovableType.SWORDSMAN_L3 };
		for (int i = 0; i < 10000; i++) {
			setMovable(18 + (i % 200) * 2, 150 + (i / 200) * 2, values[random.nextInt(values.length)], random.nextInt(4));
		}
	}

	private static void setMovable(int x, int y, EMovableType type, int player) {
		ShortPoint2D pos = new ShortPoint2D((short) x, (short) (Constants.HEIGHT - y));
		if (!HexGrid.get().isBlocked(pos.getX(), pos.getY()))
			HexGrid.get().placeNewMovable(pos, new Movable(pos, type, (byte) player));
	}

	private JSettlersAppManySoldiers(INetworkManager manager) {
		JFrame jsettlersWnd = new JFrame("jsettlers");

		JOGLPanel panel = new JOGLPanel();
		jsettlersWnd.add(panel.getJOGLJPanel());
		panel.getJOGLJPanel().requestFocusInWindow();

		jsettlersWnd.pack();
		jsettlersWnd.setSize(1200, 800);
		jsettlersWnd.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ImageProvider provider = ImageProvider.getInstance();
		provider.addLookupPath(new File("/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/GFX"));
		provider.addLookupPath(new File("D:/Games/Siedler3/GFX"));
		provider.addLookupPath(new File("C:/Program Files/siedler 3/GFX"));

		ActivePlayer.instantiate((byte) 0);

		MapInterfaceConnector connector = panel.showHexMap(HexGrid.get(), ActivePlayer.get().getStatistics());
		new GuiInterface(connector, manager);

		jsettlersWnd.setVisible(true);
	}

}
