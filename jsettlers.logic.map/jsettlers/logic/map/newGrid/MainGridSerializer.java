package jsettlers.logic.map.newGrid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jsettlers.logic.map.newGrid.blocked.BlockedGrid;
import jsettlers.logic.map.newGrid.landscape.LandscapeGrid;
import jsettlers.logic.map.newGrid.objects.ObjectsGrid;

public class MainGridSerializer {
	private final MainGrid grid;
	private final String saveFile = "save/test.sav";

	public MainGridSerializer(MainGrid grid) {
		this.grid = grid;
	}

	public void save() throws FileNotFoundException, IOException {
		new File(saveFile).delete();
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile));

		oos.write(grid.width);
		oos.write(grid.height);

		oos.writeObject(grid.landscapeGrid);
		oos.writeObject(grid.blockedGrid);
		oos.writeObject(grid.objectsGrid);

		oos.flush();
		oos.close();
	}

	public MainGrid load() throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile));

		short width = ois.readShort();
		short height = ois.readShort();

		LandscapeGrid landscapeGrid = (LandscapeGrid) ois.readObject();
		BlockedGrid blockedGrid = (BlockedGrid) ois.readObject();
		ObjectsGrid objectsGrid = (ObjectsGrid) ois.readObject();

		ois.close();

		return new MainGrid(width, height, landscapeGrid, blockedGrid, objectsGrid);
	}
}
