package jsettlers.graphics.map.draw;

import java.util.Comparator;

/**
 * Created by Rudolf Polzer on 13.07.17.
 */

public class FloatIntObject implements Comparator<FloatIntObject> {
	private float f;
	private int i;

	FloatIntObject(float f, int i) {
		this.f = f;
		this.i = i;
	}

	public float getFloat() {
		return this.f;
	}

	public int getInt() {
		return this.i;
	}

	public int compare(FloatIntObject o1, FloatIntObject o2) { // compare the float part
		if (o1.getFloat() > o2.getFloat()) {
			return 1;
		} else if (o1.getFloat() == o2.getFloat()) {
			return 0;
		} else {
			return -1;
		}
	}
}
