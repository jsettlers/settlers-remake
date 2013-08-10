package jsettlers.mapcreator.main;

import java.io.File;

import jsettlers.logic.map.save.MapLoader;
import jsettlers.main.JSettlersGame;

public class PlayProcess {
	public static void main(String[] args) {
		try {
			final File file = new File(args[0]);
			
			JSettlersGame game = new JSettlersGame(new MapLoader(
					file), 123456L, (byte) 0);
			game.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}