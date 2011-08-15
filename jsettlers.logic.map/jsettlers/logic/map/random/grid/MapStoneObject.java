package jsettlers.logic.map.random.grid;

public class MapStoneObject implements MapObject {

	private final int capacity;

	private MapStoneObject(int capacity) {
		this.capacity = capacity;
	}

	public int getCapacity() {
		return capacity;
	}

	public static MapStoneObject getInstance(int capacity) {
		return new MapStoneObject(capacity);
	}
}
