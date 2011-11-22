package jsettlers.logic.map.newGrid;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import jsettlers.common.resources.ResourceManager;
import synchronic.timer.NetworkTimer;

public class GameSerializer {
	private static final String QUICK_SAVE_FILE = "save/quicksave";
	private static final String NORMAL_EXTENSION = ".sav";
	private static final String GZIP_EXTENSION = ".sav.gz";
	private static final boolean SAVE_USE_GZIP = false;

	public GameSerializer() {
	}

	public void save(MainGrid grid) throws FileNotFoundException, IOException {
		OutputStream file;
		if (SAVE_USE_GZIP) {
			OutputStream unzipped = ResourceManager.writeFile(QUICK_SAVE_FILE + GZIP_EXTENSION);
			file = new GZIPOutputStream(unzipped);
		} else {
			file = ResourceManager.writeFile(QUICK_SAVE_FILE + NORMAL_EXTENSION);
		}

		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(file));

		NetworkTimer.get().setPausing(true);
		try {
			Thread.sleep(30); // FIXME @Andreas serializer should wait until threads did their work!
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		oos.writeInt(NetworkTimer.getGameTime());
		oos.writeObject(grid);

		oos.flush();
		oos.close();

		NetworkTimer.get().setPausing(false);
	}

	public MainGrid load() throws IOException, ClassNotFoundException {
		InputStream inStream;
		try {
			inStream = ResourceManager.getFile(QUICK_SAVE_FILE + NORMAL_EXTENSION);
		} catch (IOException e) {
			InputStream gzipped = ResourceManager.getFile(QUICK_SAVE_FILE + GZIP_EXTENSION);
			inStream = new GZIPInputStream(gzipped);
		}
		
		ObjectInputStream ois = new ObjectInputStream(inStream);
		NetworkTimer.get().setPausing(true);

		NetworkTimer.setGameTime(ois.readInt());
		MainGrid grid = (MainGrid) ois.readObject();

		NetworkTimer.get().setPausing(false);
		return grid;
	}
	
}
