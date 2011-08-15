package jsettlers.common.position;

import java.io.Serializable;

public class SRectangle implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 3854066932718449211L;

	private final short xMin;
	private final short yMin;
	private final short xMax;
	private final short yMax;

	public SRectangle(short xMin, short yMin, short xMax, short yMax) {
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
	}

	public short getXMin() {
		return xMin;
	}

	public short getYMin() {
		return yMin;
	}

	public short getXMax() {
		return xMax;
	}

	public short getYMax() {
		return yMax;
	}

	public int getWidth() {
		return xMax - xMin;
	}

	public int getHeight() {
		return yMax - yMin;
	}

}
