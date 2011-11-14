package jsettlers.logic.map.newGrid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import jsettlers.common.resources.ResourceManager;
import synchronic.timer.NetworkTimer;

public class GameSerializer {
	private static final String QUICK_SAVE_FILE = "save/quicksave.sav";

	public GameSerializer() {
	}

	public void save(MainGrid grid) throws FileNotFoundException, IOException {
		ZipOutputStream zipOutStream = new ZipOutputStream(ResourceManager.writeFile(QUICK_SAVE_FILE));
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
		ZipInputStream zipInStream = new ZipInputStream(ResourceManager.getFile(QUICK_SAVE_FILE));
		zipInStream.getNextEntry();

		ObjectInputStream ois = new ObjectInputStream(zipInStream);
		NetworkTimer.get().setPausing(true);

		NetworkTimer.setGameTime(ois.readInt());
		MainGrid grid = (MainGrid) ois.readObject();

		NetworkTimer.get().setPausing(false);
		return grid;
	}
}
