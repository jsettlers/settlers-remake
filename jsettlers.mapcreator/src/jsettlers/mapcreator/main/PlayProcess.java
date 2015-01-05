package jsettlers.mapcreator.main;

import java.io.File;

import jsettlers.logic.map.save.DirectoryMapLister;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.main.JSettlersGame;

public class PlayProcess {
	public static void main(String[] args) {
		try {
			final File file = new File(args[0]);

			JSettlersGame game = new JSettlersGame(MapLoader.getLoaderForFile(new DirectoryMapLister.ListedMapFile(file, false)), 123456L, (byte) 0,
					null);
			game.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}