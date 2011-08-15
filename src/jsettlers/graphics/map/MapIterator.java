package jsettlers.graphics.map;

import java.util.Iterator;

import jsettlers.common.map.IHexMap;
import jsettlers.common.map.IHexTile;
import jsettlers.common.position.ShortPoint2D;

/**
 * This is an iterator over an rectangulat area of the map.
 * <p>
 * It goes from back to front.
 * <p>
 * To let the area be a rectangle, every second line the y borders are increased
 * by 1.
 * 
 * 
 * @author michael
 */
public class MapIterator implements Iterator<IHexTile> {
	private short x = -1;
	private short y = 0;

	private final IHexMap map;
	private final short topleftx;
	private final short toplefty;
	private final short width;
	private final short height;

	/**
	 * 
	 * @param map
	 * @param topleftx May be negative.
	 * @param toplefty May be negative.
	 * @param width The width
	 * @param height
	 */
	public MapIterator(IHexMap map, short topleftx, short toplefty,
	        short width, short height) {
		this.map = map;
		this.topleftx = topleftx;
		this.toplefty = toplefty;
		this.width = width;
		this.height = height;
		
		increaseXY();
	}

	@Override
	public boolean hasNext() {
		return this.y < this.height;
	}

	@Override
	public IHexTile next() {
		IHexTile next;
		if (!hasNext()) {
			next = null;
		} else {
			next = this.map.getTile(new ShortPoint2D((short) (this.x + this.y/2 + this.topleftx), (short) (this.y + this.toplefty)));
		}
		
		increaseXY();
		return next;
	}

	private void increaseXY() {
	    boolean valid = false;
		while (!valid && this.y < this.height) {
	    	this.x++;
	    	if (this.x > this.width) {
	    		this.x = 0;
	    		this.y++;
	    	}
			
			int tilex = this.x + this.y/2 + this.topleftx;
			int tiley = this.y + this.toplefty;
			valid = tilex >= 0 && tilex < this.map.getWidth() && tiley >= 0 && tiley < this.map.getHeight();
	    }
    }

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
