package jsettlers.mapcreator.tools.shapes;

import jsettlers.common.position.ShortPoint2D;

/**
 * This shape lets space between its points.
 * @author michael
 *
 */
public class GridShape extends LineShape {
	@Override
	protected boolean shouldDrawAt(ShortPoint2D current) {
	    return current.getX() % 2 == 0 && current.getY() % 2 == 1;
	}
	
	@Override
	public String getName() {
	    return "filtered";
	}
}
