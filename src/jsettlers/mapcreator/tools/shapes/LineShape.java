package jsettlers.mapcreator.tools.shapes;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;

public class LineShape implements ShapeType {

	@Override
	public void setAffectedStatus(byte[][] fields, ISPosition2D start,
	        ISPosition2D end) {
		ISPosition2D current = start;
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

	protected boolean shouldDrawAt(ISPosition2D current) {
		return true;
	}

	private static void setFieldToMax(byte[][] fields, ISPosition2D current) {
		short x = current.getX();
		short y = current.getY();
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
		return "Line";
	}

}
