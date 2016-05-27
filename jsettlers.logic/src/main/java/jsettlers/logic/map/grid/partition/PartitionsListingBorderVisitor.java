/*******************************************************************************
 * Copyright (c) 2015, 2016
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

import java.util.LinkedList;

import jsettlers.algorithms.partitions.IBlockingProvider;
import jsettlers.algorithms.traversing.borders.IBorderVisitor;
import jsettlers.common.position.ShortPoint2D;

/**
 * This class implements the {@link IBorderVisitor} and is used to detect all the partitions that are on the traversed positions.<br>
 * NOTE: This class is especially used for detecting necessary merges and divides when a position has changed player.
 * 
 * @author Andreas Eberle
 * 
 */
final class PartitionsListingBorderVisitor implements IBorderVisitor {

	private final PartitionsGrid grid;
	private final IBlockingProvider blockingProvider;
	private final LinkedList<BorderPartitionInfo> partitionsList = new LinkedList<>();

	private short lastPartition = -1;

	public PartitionsListingBorderVisitor(PartitionsGrid grid, IBlockingProvider blockingProvider) {
		this.grid = grid;
		this.blockingProvider = blockingProvider;
	}

	@Override
	public boolean visit(int insideX, int insideY, int outsideX, int outsideY) {
		if (blockingProvider.isBlocked(outsideX, outsideY)) {
			lastPartition = -1;
		} else {
			short currPartition = grid.partitionObjects[grid.partitions[outsideX + outsideY * grid.width]].partitionId;

			if (currPartition != lastPartition) {
				partitionsList.addLast(new BorderPartitionInfo(currPartition, new ShortPoint2D(outsideX, outsideY),
						new ShortPoint2D(insideX, insideY)));
			}

			lastPartition = currPartition;
		}
		return true;
	}

	public LinkedList<BorderPartitionInfo> getPartitionsList() {
		LinkedList<BorderPartitionInfo> resultList = new LinkedList<>();
		resultList.addAll(partitionsList);

		if (resultList.size() >= 2 && resultList.getFirst().partitionId == resultList.getLast().partitionId && lastPartition != -1) {
			resultList.removeFirst();
		}

		return resultList;
	}

	public static class BorderPartitionInfo {
		public final short partitionId;
		public final ShortPoint2D positionOfPartition;
		public final ShortPoint2D insideNeighborPosition;

		private BorderPartitionInfo(short partitionId, ShortPoint2D positionOfPartition, ShortPoint2D insideNeighborPosition) {
			this.partitionId = partitionId;
			this.positionOfPartition = positionOfPartition;
			this.insideNeighborPosition = insideNeighborPosition;
		}
	}
}