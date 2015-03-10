package jsettlers.logic.map.newGrid.landscape;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.timer.IScheduledTimerable;
import jsettlers.logic.timer.RescheduleTimer;

/**
 * Resets the positions flattened by movables to grass after a while.
 * 
 * @author Andreas Eberle
 * 
 */
final class FlattenedResetter implements IScheduledTimerable, Serializable {
	private static final long serialVersionUID = -7786860099434140327L;
	private static final int SCHEDULE_INTERVAL = 1500;

	private transient LinkedList<ShortPoint2D> positions = new LinkedList<ShortPoint2D>();
	private final IFlattenedResettable grid;

	FlattenedResetter(IFlattenedResettable grid) {
		this.grid = grid;
		positions = new LinkedList<ShortPoint2D>();
		RescheduleTimer.add(this, SCHEDULE_INTERVAL);
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();

		for (ShortPoint2D curr : positions) {
			oos.writeObject(curr);
		}
		oos.writeObject(null);
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();

		positions = new LinkedList<ShortPoint2D>();

		ShortPoint2D curr = (ShortPoint2D) ois.readObject();
		while (curr != null) {
			positions.add(curr);
			curr = (ShortPoint2D) ois.readObject();
		}
	}

	public void addPosition(int x, int y) {
		positions.add(new ShortPoint2D(x, y));
	}

	public void removeArea(FreeMapArea area) {
		Iterator<ShortPoint2D> iter = positions.iterator();
		while (iter.hasNext()) {
			if (area.contains(iter.next())) {
				iter.remove();
			}
		}
	}

	@Override
	public int timerEvent() {
		for (Iterator<ShortPoint2D> iter = positions.iterator(); iter.hasNext();) {
			ShortPoint2D currPos = iter.next();
			if (grid.countFlattenedDown(currPos.x, currPos.y)) {
				iter.remove();
			}
		}

		return SCHEDULE_INTERVAL;
	}

	@Override
	public void kill() {
		// nothing to do here
	}
}
