package jsettlers.mapcreator.tools;

import jsettlers.common.position.ISPosition2D;

public class LineCircleShape implements ShapeType {

	private int radius = 10;

	@Override
	public void setAffectedStatus(byte[][] fields, ISPosition2D start, ISPosition2D end) {
		CircleLine line = new CircleLine(start, end);

		for (int x = 0; x < fields.length; x++) {
			for (int y = 0; y < fields[x].length; y++) {
				double distance = line.getDistanceOf(x, y);
				fields[x][y] = getFieldRating(x, y, distance);
			}
		}
	}

	@SuppressWarnings("unused")
	protected byte getFieldRating(int x, int y, double distance) {
		return (distance <= radius ? Byte.MAX_VALUE : 0);
	}

	@Override
	public int getSize() {
		return radius;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

}
