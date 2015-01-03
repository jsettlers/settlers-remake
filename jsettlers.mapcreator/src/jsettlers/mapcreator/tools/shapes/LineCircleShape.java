package jsettlers.mapcreator.tools.shapes;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.localization.EditorLabels;

public class LineCircleShape extends ShapeType {

	private static final ShapeProperty RADIUS_PROPERTY = new ShapeProperty(EditorLabels.getLabel("radius"), 0, 100);

	public LineCircleShape() {
		addProperty(RADIUS_PROPERTY);
	}

	@Override
	public void setAffectedStatus(byte[][] fields, ShortPoint2D start,
			ShortPoint2D end) {
		CircleLine line = new CircleLine(start, end);

		for (int x = 0; x < fields.length; x++) {
			for (int y = 0; y < fields[x].length; y++) {
				double distance = line.getDistanceOf(x, y);
				fields[x][y] = getFieldRating(x, y, distance);
			}
		}
	}

	protected byte getFieldRating(int x, int y, double distance) {
		return (distance <= getProperty(RADIUS_PROPERTY) ? Byte.MAX_VALUE : 0);
	}

	@Override
	public int getSize() {
		return getProperty(RADIUS_PROPERTY);
	}

	public int getRadius() {
		return getProperty(RADIUS_PROPERTY);
	}

	@Override
	public String getName() {
		return EditorLabels.getLabel("circle_line");
	}
}
