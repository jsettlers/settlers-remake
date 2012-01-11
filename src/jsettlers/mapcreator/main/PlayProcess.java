package jsettlers.mapcreator.main;

import java.io.File;
import java.io.FileInputStream;

import jsettlers.common.map.IMapData;
import jsettlers.common.map.IMapDataProvider;
import jsettlers.common.map.MapLoadException;
import jsettlers.main.swing.SwingManagedJSettlers;
import jsettlers.mapcreator.data.MapData;

public class PlayProcess {
	public static void main(String[] args) {
		try {
			final File file = new File(args[0]);

			SwingManagedJSettlers.startMap(new IMapDataProvider() {
				@Override
				public IMapData getData() throws MapLoadException {
					try  {
						FileInputStream in = new FileInputStream(file);
						return MapData.deserialize(in);
					} catch (Throwable e) {
						throw new MapLoadException(e);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}