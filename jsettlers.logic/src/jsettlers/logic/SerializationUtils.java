package jsettlers.logic;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;

public class SerializationUtils {

	public static <T> void writeSparseArray(ObjectOutputStream oos, T[] data) throws IOException {
		oos.writeInt(data.length);

		for (int index = 0; index < data.length; index++) {
			T object = data[index];
			if (object != null) {
				oos.writeInt(index);
				oos.writeObject(object);
			}
		}
		oos.writeInt(-1);
		oos.flush();
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] readSparseArray(ObjectInputStream ois, Class<T> arrayElementType) throws IOException, ClassNotFoundException {
		int length = ois.readInt();
		T[] data = (T[]) Array.newInstance(arrayElementType, length);

		int index = ois.readInt();
		while (index >= 0) {
			data[index] = (T) ois.readObject();
			index = ois.readInt();
		}
		return data;
	}
}
