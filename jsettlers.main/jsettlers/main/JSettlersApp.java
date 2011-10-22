package jsettlers.main;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import jsettlers.graphics.JOGLPanel;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.input.GuiInterface;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.player.ActivePlayer;
import jsettlers.logic.timer.Timer100Milli;
import network.INetworkManager;
import network.NetworkManager;
import network.NullNetworkManager;
import random.RandomSingleton;

public abstract class JSettlersApp implements Runnable {

	private static final int[] PRELOAD_FILES = new int[] {
	        2, 0, 1, 3, 10, 11, 12, 13
	};

	private static final byte PLAYERS = 3;

	private final String networkmode;

	private final String host;

	protected JSettlersApp() {
		this("single", "");
	}

	protected JSettlersApp(String networkmode, String host) {
		this.networkmode = networkmode;
		this.host = host;
	}

	public void addImagePath(File file) {
		ImageProvider provider = ImageProvider.getInstance();
		provider.addLookupPath(file);
    }
	
	public void run() {
		INetworkManager manager = null;
        try {
	        manager = startNetworkManager(networkmode, host);
        } catch (UnknownHostException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }

		ActivePlayer.instantiate((byte) 0);

		JOGLPanel content = new JOGLPanel();
		startGui(content);

		ProgressConnector progress = content.showProgress();

		// Schedule image loading while the other stuff is waiting
		ImageProvider provider = ImageProvider.getInstance();
		provider.addLookupPath(new File(
		        "/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/GFX"));
		provider.addLookupPath(new File("D:/Games/Siedler3/GFX"));
		provider.addLookupPath(new File("C:/Program Files/siedler 3/GFX"));
		for (int i : PRELOAD_FILES) {
			provider.preload(i);
		}
		RandomSingleton.load(2132134L);

		Timer100Milli.start();

		progress.setProgressState(EProgressState.LOADING_MAP);

		MainGrid grid = MainGrid.create("test", PLAYERS, RandomSingleton.get());

		progress.setProgressState(EProgressState.LOADING_IMAGES);
		for (int i : PRELOAD_FILES) {
			provider.waitForPreload(i);
		}

		MapInterfaceConnector connector =
		        content.showHexMap(grid.getGraphicsGrid(), ActivePlayer.get()
		                .getStatistics());
		new GuiInterface(connector, manager, grid.getGuiInputGrid());

		manager.startGameTimer();
	}

	protected abstract void startGui(JOGLPanel content);

	private INetworkManager startNetworkManager(String networkmode, String host)
	        throws UnknownHostException, IOException {
		INetworkManager manager;
		if (networkmode.equalsIgnoreCase("single")) {
			manager = new NullNetworkManager();
		} else {
			if (host.equalsIgnoreCase("host")) {
				manager = new NetworkManager(60);
			} else {
				manager = new NetworkManager("localhost", 60);
			}
		}

		manager.waitForInit();
		return manager;
	}
}
