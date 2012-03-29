package jsettlers.mapcreator.tools.buffers;

import java.util.Iterator;

import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.position.ShortPoint2D;

public class ByteMapArea implements IMapArea {
	/**
     * 
     */
	private static final long serialVersionUID = 882939657993150266L;

	private final byte[][] status;

	public ByteMapArea(byte[][] status) {
		this.status = status;
	}

	@Override
	public boolean contains(ShortPoint2D position) {
		short y = position.getY();
		short x = position.getX();
		return x >= 0 && y >= 0 && x < status.length && y < status[x].length && status[x][y] > Byte.MAX_VALUE / 2;
	}

	@Override
	public Iterator<ShortPoint2D> iterator() {
		return new It();
	}

	private class It implements Iterator<ShortPoint2D> {
		private int x = 0;
		private int y = 0;

		private It() {
			searchNext();
		}

		private void searchNext() {
			do {
				x++;
				if (x >= status.length) {
					x = 0;
					y++;
				}
			} while (y < status[x].length && status[x][y] <= Byte.MAX_VALUE / 2);
		}

		@Override
		public boolean hasNext() {
			return y < status[x].length;
		}

		@Override
		public ShortPoint2D next() {
			if (!hasNext()) {
				throw new IllegalStateException();
			}
			ShortPoint2D point2d = new ShortPoint2D(x, y);
			searchNext();
			return point2d;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
}
