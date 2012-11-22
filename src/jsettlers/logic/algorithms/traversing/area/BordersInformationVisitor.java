package jsettlers.logic.algorithms.traversing.area;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.traversing.ITraversingVisitor;

public final class BordersInformationVisitor implements ITraversingVisitor {

	final int[] xMin;
	final int[] xMax;

	int lastX;
	int lastY;

	int minY;
	int maxY;

	public BordersInformationVisitor(ShortPoint2D startPos, int height) {
		lastX = startPos.x;
		lastY = startPos.y;

		minY = maxY = startPos.y;

		xMin = new int[height];
		for (int i = 0; i < height; i++) {
			xMin[i] = Integer.MAX_VALUE;
		}
		xMax = new int[height];
	}

	@Override
	public boolean visit(int x, int y) {
		int dy = y - lastY;

		if (dy != 0) {
			xMin[y] = Math.min(x, xMin[y]);
			xMin[lastY] = Math.min(lastX, xMin[lastY]);
			xMax[y] = Math.max(x, xMax[y]);
			xMax[lastY] = Math.max(lastX, xMax[lastY]);
		}

		if (y < minY) {
			minY = y;
		} else if (y > maxY) {
			maxY = y;
		}

		lastY = y;
		lastX = x;

		return true;
	}
}
