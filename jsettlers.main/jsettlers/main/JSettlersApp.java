package jsettlers.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jsettlers.graphics.JOGLPanel;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.input.GuiInterface;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.newGrid.MainGridSerializer;
import jsettlers.logic.timer.Timer100Milli;
import network.INetworkManager;
import network.NetworkManager;
import network.NullNetworkManager;
import random.RandomSingleton;

public abstract class JSettlersApp implements Runnable {

	private static final int[] PRELOAD_FILES = new int[] { 2, 0, 1, 3, 10, 11, 12, 13 };

	private static final byte PLAYERS = 3;

	private final String networkmode;
	private final String host;

	private final String randomMap;

	protected JSettlersApp() {
		this("single", "", "test");
	}

	/**
	 * 
	 * @param networkmode
	 *            possible values are:<br>
	 *            <dl>
	 *            <dt>"single"</dt>
	 *            <dd>leading to a single player game</dd>
	 *            <dt>"host"</dt>
	 *            <dd>opening a host for a multiplayer game</dd>
	 *            <dt>"client"</dt>
	 *            <dd>opening a connection to the given host.</dd>
	 *            </dl>
	 * @param host
	 *            host to connect to.
	 * @param randomMap
	 *            name of the random map or <br>
	 *            null if the debugging map should be loaded.
	 */
	protected JSettlersApp(String networkmode, String host, String randomMap) {
		this.networkmode = networkmode;
		this.host = host;
		this.randomMap = randomMap;
	}

	public void addImagePath(File file) {
		ImageProvider provider = ImageProvider.getInstance();
		provider.addLookupPath(file);
	}

	@Override
	public void run() {
		INetworkManager manager = null;

		manager = startNetworkManager(networkmode, host);

		JOGLPanel content = new JOGLPanel();
		startGui(content);

		ProgressConnector progress = content.showProgress();

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

		progress.setProgressState(EProgressState.LOADING_MAP);

		MainGrid grid;

		grid = createMainGrid();

		progress.setProgressState(EProgressState.LOADING_IMAGES);
		for (int i : PRELOAD_FILES) {
			provider.waitForPreload(i);
		}

		MapInterfaceConnector connector = content.showHexMap(grid.getGraphicsGrid(), null);
		new GuiInterface(connector, manager, grid.getGuiInputGrid());

		manager.startGameTimer();
	}

	private MainGrid createMainGrid() {
		MainGrid grid;
		if (randomMap != null && !randomMap.isEmpty()) {
			grid = MainGrid.create("test", PLAYERS, RandomSingleton.get());
		} else {
			MainGridSerializer serializer = new MainGridSerializer();
			try {
				grid = serializer.load();
			} catch (Exception e) {
				e.printStackTrace();
				grid = null;
			}

			if (grid == null) {
				grid = MainGrid.createForDebugging();
				try {
					serializer.save(grid);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return grid;
	}

	protected abstract void startGui(JOGLPanel content);

	private INetworkManager startNetworkManager(String networkmode, String host) {
		INetworkManager manager;
		if (networkmode.equalsIgnoreCase("single")) {
			manager = new NullNetworkManager();
		} else {
			if (networkmode.equalsIgnoreCase("host")) {
				manager = new NetworkManager(6666);
			} else {
				manager = new NetworkManager(host, 6666);
			}
		}

		manager.waitForInit();
		return manager;
	}
}
