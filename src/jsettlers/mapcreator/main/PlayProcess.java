package jsettlers.mapcreator.main;

import java.io.File;
import java.io.FileInputStream;

import jsettlers.main.swing.SwingManagedJSettlers;
import jsettlers.mapcreator.data.MapData;

public class PlayProcess {
	public static void main(String[] args) {
		try {
			File file = new File(args[0]);
			FileInputStream in = new FileInputStream(file);

			MapData data = MapData.deserialize(in);
			SwingManagedJSettlers.startMap(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}