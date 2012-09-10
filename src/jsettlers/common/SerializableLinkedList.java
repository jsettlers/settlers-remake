package jsettlers.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.LinkedList;

/**
 * This is a linked list that serializes itself in a better way then the standard does.
 * 
 * @author Andreas Eberle
 */
public class SerializableLinkedList<T> extends LinkedList<T> {
	private static final long serialVersionUID = 9095918450777901533L;

	public SerializableLinkedList() {
		super();
	}

	public SerializableLinkedList(Collection<T> c) {
		super(c);
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeInt(super.size());
		for (T curr : this) {
			oos.writeObject(curr);
		}
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		int size = ois.readInt();
		for (int i = 0; i < size; i++) {
			@SuppressWarnings("unchecked")
			T curr = (T) ois.readObject();
			super.add(curr);
		}
	}

}
