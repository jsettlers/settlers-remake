package jsettlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jsettlers.common.map.MapLoadException;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.save.MapLoader;
import random.RandomSingleton;

public final class TestUtils {
	private TestUtils() {
	}

	public static MainGrid getMap() throws MapLoadException {
		RandomSingleton.load(123456L);
		MapLoader loader = new MapLoader(new File("../jsettlers.common/resources/maps/bigmap.map"));
		return loader.getMainGrid();
	}

	public static <T> T serializeAndDeserialize(T list) throws IOException,
			ClassNotFoundException {
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(byteOutStream);

		oos.writeObject(list);
		oos.close();

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteOutStream.toByteArray()));

		@SuppressWarnings("unchecked")
		T readList = (T) ois.readObject();
		ois.close();

		return readList;
	}
}
