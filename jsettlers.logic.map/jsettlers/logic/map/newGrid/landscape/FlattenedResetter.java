package jsettlers.logic.map.newGrid.landscape;

import java.util.Iterator;
import java.util.LinkedList;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.timer.ITimerable;
import jsettlers.logic.timer.Timer100Milli;

class FlattenedResetter implements ITimerable {
	/**
	 * number of ticks of the timer to be ignored before the next reset run will be done.
	 */
	private static final byte EXECUTION_DELAY = 15;

	private final LinkedList<ShortPoint2D> positions = new LinkedList<ShortPoint2D>();
	private byte delayCtr = 0;
	private final IFlattenedResettable grid;

	FlattenedResetter(IFlattenedResettable grid) {
		this.grid = grid;
		Timer100Milli.add(this);
	}

	public void addPosition(short x, short y) {
		positions.add(new ShortPoint2D(x, y));
	}

	@Override
	public void timerEvent() {
		delayCtr++;
		if (delayCtr >= EXECUTION_DELAY) {
			delayCtr = 0;

			for (Iterator<ShortPoint2D> iter = positions.iterator(); iter.hasNext();) {
				ShortPoint2D currPos = iter.next();
				if (grid.countFlattenedDown(currPos.getX(), currPos.getY())) {
					iter.remove();
				}
			}
		}
	}

	@Override
	public void kill() {
		// ignore here
	}

}
