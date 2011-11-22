package jsettlers.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.LinkedList;

/**
 * This is a linked list that can be serialized as array.
 * 
 * @author michael
 */
public class SerializableLinkedList<T> extends LinkedList<T> {
	/**
     * 
     */
    private static final long serialVersionUID = 9095918450777901533L;

	public SerializableLinkedList() {
		super();
	}

	public SerializableLinkedList(Collection<T> c) {
		super(c);
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeObject(toArray());
	}

	private void readObject(ObjectInputStream ois)
	        throws ClassNotFoundException, IOException {
		@SuppressWarnings("unchecked")
        T[] arr = (T[]) ois.readObject();
		for (T a : arr) {
			add(a);
		}
	}

}
