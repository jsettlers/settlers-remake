package jsettlers.main;

import java.io.File;

import network.NullNetworkManager;

import random.RandomSingleton;
import jsettlers.graphics.JOGLPanel;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.graphics.startscreen.IStartScreenConnector;
import jsettlers.graphics.startscreen.IStartScreenConnector.IGameSettings;
import jsettlers.input.GuiInterface;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.timer.Timer100Milli;

/**
 * This is the new main game class
 * 
 * @author michael
 */
public class ManagedJSettlers {

	private JOGLPanel content;

	public synchronized void start(IGuiStarter starter) {
		content = new JOGLPanel();
		starter.startGui(content);

		// Schedule image loading while the other stuff is waiting
		ImageProvider provider = ImageProvider.getInstance();
		provider.addLookupPath(new File(
		        "/home/michael/.wine/drive_c/BlueByte/S3AmazonenDemo/GFX"));
		provider.addLookupPath(new File("D:/Games/Siedler3/GFX"));
		provider.addLookupPath(new File("C:/Program Files/siedler 3/GFX"));
		RandomSingleton.load(2132134L);

		showMainScreen();
	}

	private void showMainScreen() {
		content.showStartScreen(new StartConnector());
	}

	private class StartConnector implements IStartScreenConnector {

		private final IMapItem[] MAPS = new IMapItem[] {
			new MapItem()
		};

		@Override
		public IMapItem[] getMaps() {
			return MAPS;
		}

		private class MapItem implements IMapItem {
			@Override
			public String getName() {
				return "test";
			}

			@Override
			public int getMinPlayers() {
				return 1;
			}

			@Override
			public int getMaxPlayers() {
				return 5;
			}
		}

		@Override
		public ILoadableGame[] getLoadableGames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IRecoverableGame[] getRecoverableGames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public INetworkGame[] getNetworkGames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setNetworkServer(String host) {
			// TODO Auto-generated method stub

		}

		@Override
		public void startNewGame(IGameSettings game) {
			startGame(game);
		}

		@Override
		public void loadGame(ILoadableGame load) {
			// TODO Auto-generated method stub

		}

		@Override
		public void recoverNetworkGame(IRecoverableGame game) {
			// TODO Auto-generated method stub

		}

		@Override
		public void joinNetworkGame(INetworkGame game) {
			// TODO Auto-generated method stub

		}

		@Override
        public void addNetworkGameListener(INetworkGameListener gameListener) {
	        // TODO Auto-generated method stub
	        
        }

		@Override
        public void removeNetworkGameListener(INetworkGameListener gameListener) {
	        // TODO Auto-generated method stub
	        
        }

		@Override
        public void startGameServer(IGameSettings game, String name) {
	        // TODO Auto-generated method stub
	        
        }

		@Override
        public void exitGame() {
	        // TODO Auto-generated method stub
	        
        }

	}

	synchronized void startGame(IGameSettings game) {
		NullNetworkManager manager = new NullNetworkManager();
		ProgressConnector progress = content.showProgress();
		Timer100Milli.start();

		progress.setProgressState(EProgressState.LOADING_MAP);

		MainGrid grid = createGameGrid(game);

		progress.setProgressState(EProgressState.LOADING_IMAGES);

		MapInterfaceConnector connector =
		        content.showHexMap(grid.getGraphicsGrid(), null);
		new GuiInterface(connector, manager, grid.getGuiInputGrid());

		manager.startGameTimer();
	}

	private MainGrid createGameGrid(IGameSettings game) {
		return MainGrid.create(game.getMap().getName(),
		        (byte) game.getPlayerCount(), RandomSingleton.get());
	}

	public interface IGuiStarter {
		void startGui(JOGLPanel content);
	}

}
