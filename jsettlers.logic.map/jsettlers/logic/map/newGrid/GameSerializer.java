package jsettlers.logic.map.newGrid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import synchronic.timer.NetworkTimer;

public class GameSerializer {
	private final String saveFile = "save/test.sav";

	public GameSerializer() {
	}

	public void save(MainGrid grid) throws FileNotFoundException, IOException {
		new File(saveFile).delete();
		new File(saveFile).getParentFile().mkdirs();

		ZipOutputStream zipOutStream = new ZipOutputStream(new FileOutputStream(saveFile));
		zipOutStream.putNextEntry(new ZipEntry("savefile"));

		ObjectOutputStream oos = new ObjectOutputStream(zipOutStream);

		NetworkTimer.get().setPausing(true);
		try {
			Thread.sleep(30); // FIXME @Andreas serializer should wait until threads did their work!
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		oos.writeInt(NetworkTimer.getGameTime());
		oos.writeObject(grid);

		oos.flush();

		zipOutStream.closeEntry();

		oos.close();

		NetworkTimer.get().setPausing(false);
	}

	public MainGrid load() throws FileNotFoundException, IOException, ClassNotFoundException {
		if (new File(saveFile).exists()) {
			ZipInputStream zipInStream = new ZipInputStream(new FileInputStream(saveFile));
			zipInStream.getNextEntry();

			ObjectInputStream ois = new ObjectInputStream(zipInStream);
			NetworkTimer.get().setPausing(true);

			NetworkTimer.setGameTime(ois.readInt());
			MainGrid grid = (MainGrid) ois.readObject();

			NetworkTimer.get().setPausing(false);
			return grid;
		} else {
			return null;
		}
	}
}
