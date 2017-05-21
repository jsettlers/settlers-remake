/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.map.grid.landscape;

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

	private transient LinkedList<ShortPoint2D> positions = new LinkedList<>();
	private final IFlattenedResettable grid;

	FlattenedResetter(IFlattenedResettable grid) {
		this.grid = grid;
		positions = new LinkedList<>();
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

		positions = new LinkedList<>();

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
