package jsettlers.mapcreator.tools.shapes;

import jsettlers.common.position.ShortPoint2D;

public class LineCircleShape implements ShapeType {

	private int radius = 10;

	@Override
	public void setAffectedStatus(byte[][] fields, ShortPoint2D start, ShortPoint2D end) {
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

	@Override
	public String getName() {
	    return "circle line";
	}
}
