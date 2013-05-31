package jsettlers.mapcreator.main;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;

import jsettlers.common.map.IMapData;
import jsettlers.common.map.IMapDataProvider;
import jsettlers.common.map.MapLoadException;
import jsettlers.main.JSettlersGame;
import jsettlers.main.MapDataMapCreator;
import jsettlers.main.swing.SwingManagedJSettlers;
import jsettlers.mapcreator.data.MapData;

public class PlayProcess {
	public static void main(String[] args) {
		try {
			final File file = new File(args[0]);

			IMapDataProvider mapDataProvider = new IMapDataProvider() {
				@Override
				public IMapData getData() throws MapLoadException {
					try {
						FileInputStream in = new FileInputStream(file);
						return MapData.deserialize(in);
					} catch (Throwable e) {
						throw new MapLoadException(e);
					}
				}
			};

			@SuppressWarnings("deprecation")
			JSettlersGame game = new JSettlersGame(SwingManagedJSettlers.getGui(Collections.<String> emptyList()), new MapDataMapCreator(
					mapDataProvider), 123456L, (byte) 0);
			game.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}