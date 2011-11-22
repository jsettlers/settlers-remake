package jsettlers.logic.map.newGrid;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import jsettlers.common.resources.ResourceManager;
import synchronic.timer.NetworkTimer;

public class GameSerializer {
	private static final String QUICK_SAVE_FILE = "save/quicksave.sav";

	public GameSerializer() {
	}

	public void save(MainGrid grid) throws FileNotFoundException, IOException {
		
		GZIPOutputStream zipOutStream = new GZIPOutputStream(ResourceManager.writeFile(QUICK_SAVE_FILE));

		ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(zipOutStream));

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

	public MainGrid load() throws FileNotFoundException, IOException, ClassNotFoundException {
		GZIPInputStream zipInStream = new GZIPInputStream(ResourceManager.getFile(QUICK_SAVE_FILE));

		ObjectInputStream ois = new ObjectInputStream(zipInStream);
		NetworkTimer.get().setPausing(true);

		NetworkTimer.setGameTime(ois.readInt());
		MainGrid grid = (MainGrid) ois.readObject();

		NetworkTimer.get().setPausing(false);
		return grid;
	}
	
	
}
