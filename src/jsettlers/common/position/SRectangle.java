package jsettlers.common.position;

import java.io.Serializable;

public class SRectangle implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 3854066932718449211L;

	public final short xMin;
	public final short yMin;
	public final short xMax;
	public final short yMax;

	public SRectangle(short xMin, short yMin, short xMax, short yMax) {
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
	}

	public int getWidth() {
		return xMax - xMin + 1;
	}

	public int getHeight() {
		return yMax - yMin + 1;
	}

	public boolean contains(ShortPoint2D pos) {
		return xMin <= pos.x && pos.x <= xMax && yMin <= pos.y && pos.y <= yMax;
	}

	@Override
	public String toString() {
		return "xMin: " + xMin + " yMin: " + yMin + "  xMax: " + xMax + " yMax: " + yMax;
	}
}
