package jsettlers.newmain.datatypes;

/**
 * Class to contain a reference to an object.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public class ObjectContainer<T> {
	private T value;

	public ObjectContainer() {
	}

	public ObjectContainer(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T object) {
		this.value = object;
	}
}
