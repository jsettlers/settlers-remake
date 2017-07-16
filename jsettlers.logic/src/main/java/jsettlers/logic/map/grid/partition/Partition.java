/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.logic.map.grid.partition;

import java.io.Serializable;

import jsettlers.logic.map.grid.partition.data.PartitionDataSupplier;
import jsettlers.logic.map.grid.partition.manager.PartitionManager;
import jsettlers.logic.map.grid.partition.manager.materials.offers.IOffersCountListener;

/**
 * This class holds the metadata of a partition.
 *
 * @author Andreas Eberle
 *
 */
public final class Partition extends PartitionManager implements Serializable {
	private static final long serialVersionUID = -2087692347209993840L;

	final short partitionId;
	final byte  playerId;

	private int counter = 0;
	private int xSum    = 0;
	private int ySum    = 0;

	public Partition(short partitionId, byte playerId, IOffersCountListener countListener) {
		super(countListener);
		this.partitionId = partitionId;
		this.playerId = playerId;
	}

	public Partition(short partitionId, byte playerId, int size) {
		this(partitionId, playerId, null);
		this.counter = size;
	}

	void decrement(int x, int y) {
		counter--;
		xSum -= x;
		ySum -= y;
	}

	public void increment(int x, int y) {
		counter++;
		xSum += x;
		ySum += y;
	}

	public void mergeInto(Partition newPartition) {
		super.mergeInto(newPartition);
		newPartition.counter += this.counter;
		newPartition.xSum += xSum;
		newPartition.ySum += ySum;

		counter = 0;
		xSum = 0;
		ySum = 0;
	}

	public void removePositionTo(final int x, final int y, final Partition newPartitionObject) {
		if (this == newPartitionObject) {
			System.err.println("ERROR: newManager can not be the same as this manager. At (" + x + "|" + y + ")");
		}

		this.decrement(x, y);
		newPartitionObject.increment(x, y);
		super.removePositionTo(x, y, newPartitionObject, newPartitionObject.playerId == this.playerId);

		if (isEmpty()) { super.stopManager(); }
	}

	public boolean isEmpty() {
		return counter <= 0;
	}

	public byte getPlayerId() {
		return playerId;
	}

	public int getNumberOfElements() {
		return counter;
	}

	public PartitionDataSupplier getPartitionData() {
		return new PartitionDataSupplier(playerId, partitionId, getPartitionSettings(), getMaterialCounts());
	}
}
