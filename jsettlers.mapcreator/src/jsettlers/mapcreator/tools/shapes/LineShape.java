package jsettlers.mapcreator.tools.shapes;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.localization.EditorLabels;

public class LineShape extends ShapeType {

	@Override
	public void setAffectedStatus(byte[][] fields, ShortPoint2D start, ShortPoint2D end) {
		ShortPoint2D current = start;
		if (shouldDrawAt(current)) {
			setFieldToMax(fields, current);
		}
		while (!current.equals(end)) {
			EDirection d = EDirection.getApproxDirection(current, end);
			current = d.getNextHexPoint(current);
			if (shouldDrawAt(current)) {
				setFieldToMax(fields, current);
			}
		}
	}

	protected boolean shouldDrawAt(ShortPoint2D current) {
		return true;
	}

	private static void setFieldToMax(byte[][] fields, ShortPoint2D current) {
		short x = current.x;
		short y = current.y;
		if (x < fields.length && x >= 0 && y >= 0 && y < fields[x].length) {
			fields[x][y] = Byte.MAX_VALUE;
		}
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public String getName() {
		return EditorLabels.getLabel("line");
	}

}
