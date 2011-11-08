package jsettlers.logic.map.newGrid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainGridSerializer {
	private final String saveFile = "save/test.sav";

	public MainGridSerializer() {
	}

	public void save(MainGrid grid) throws FileNotFoundException, IOException {
		new File(saveFile).delete();
		new File(saveFile).getParentFile().mkdirs();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));

		oos.writeObject(grid);

		oos.flush();
		oos.close();
	}

	public MainGrid load() throws FileNotFoundException, IOException, ClassNotFoundException {
		if (new File(saveFile).exists()) {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile));

			return (MainGrid) ois.readObject();
		} else {
			return null;
		}
	}
}
