package jsettlers.mapcreator.tools;

import jsettlers.common.position.ISPosition2D;

/**
 * Only draws a little point at the start position.
 * @author michael
 *
 */
public class PointShape implements ShapeType {

	@Override
    public void setAffectedStatus(byte[][] fields, ISPosition2D start,
            ISPosition2D end) {
	    short x = start.getX();
		if (x >= 0 && x < fields.length) {
	    	short y = start.getY();
			if (y >= 0 && y < fields[x].length) {
		    	fields[x][y] = Byte.MAX_VALUE;
		    }
	    }
    }

	@Override
    public int getSize() {
	    return 1;
    }

}
