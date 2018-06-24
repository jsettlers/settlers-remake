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
package jsettlers.algorithms.construction;

import jsettlers.common.buildings.BuildingAreaBitSet;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.RelativePoint;

import java.util.BitSet;

/**
 * Algorithm to calculate the construction marks for the user.
 * 
 * @author Andreas Eberle
 * 
 */
public final class NewConstructionMarksAlgorithm {
	private final AbstractConstructionMarkableMap map;
	private final byte playerId;

	private MapRectangle lastArea = null;

	public NewConstructionMarksAlgorithm(AbstractConstructionMarkableMap map, byte player) {
		this.map = map;
		this.playerId = player;
	}

	public void calculateConstructMarks(final MapRectangle mapArea, EBuildingType buildingType) {
		if (lastArea != null) {
			removeConstructionMarks(lastArea, mapArea);
		}

		BuildingAreaBitSet buildingArea = buildingType.getBuildingAreaBitSet();
		boolean binaryConstructionMarkValues = !buildingType.needsFlattenedGround();
		RelativePoint[] positionsToBeFlattened = buildingType.getBuildingArea();

		// declare local variables
		final short[] xJumps = buildingArea.xJumps;
		final short[] yJumps = buildingArea.yJumps;

		final int lineLength = mapArea.getWidth() + mapArea.getHeight() / 2;
		final BitSet doneSet = new BitSet(lineLength * mapArea.getHeight());

		final int xOffsetForBuilding = buildingArea.minX;
		final int yOffsetForBuilding = buildingArea.minY;
		final int buildingAreaWidth = buildingArea.width;
		final int buildingAreaHeight = buildingArea.height;

		// iterate over the positions in the mapArea with the offset from the buildingArea
		for (int line = 0; line < mapArea.getHeight(); line++) {
			final int y = mapArea.getLineY(line);
			final int xLineOffset = mapArea.getMinX();

			DX_LOOP: for (int dx = 0; dx < lineLength; dx++) {
				final int x = xLineOffset + dx;
				final short partitionId;

				if (!mapArea.contains(x, y) || doneSet.get(dx + line * lineLength)) { // if this position has already been pruned.
					continue;
				}

				{ // get the partition and check if the player is allowed to use this partition
					int firstPosX = buildingArea.aPosition.calculateX(x);
					int firstPosY = buildingArea.aPosition.calculateY(y);

					if (!map.isInBounds(firstPosX, firstPosY)) {
						continue;
					}

					partitionId = map.getPartitionIdAt(firstPosX, firstPosY);

					if (!map.canPlayerConstructOnPartition(playerId, partitionId)) {
						continue;
					}
				}

				// go over all positions of the building and check if they are free
				for (int buildingDx = buildingAreaWidth - 1; buildingDx >= 0; buildingDx--) {
					for (int buildingDy = buildingAreaHeight - 1; buildingDy >= 0; buildingDy--) {
						int index = buildingDx + buildingDy * buildingAreaWidth;

						// relative position regarding the building
						int buildingPositionX = buildingDx + xOffsetForBuilding;
						int buildingPositionY = buildingDy + yOffsetForBuilding;

						// if the position must be free, but isn't
						if (xJumps[index] != 0
								&& !map.canUsePositionForConstruction(x + buildingPositionX, y + buildingPositionY,
										buildingType.getRequiredGroundTypeAt(buildingPositionX, buildingPositionY), partitionId)) {

							map.setConstructMarking(x, y, false, binaryConstructionMarkValues, null);

							// prune the positions we already know that they are invalid.
							for (int pruneX = 0; pruneX < xJumps[index]; pruneX++) {
								int currYJumps = yJumps[(buildingDx - pruneX) + buildingDy * buildingAreaWidth];
								for (int pruneY = 0; pruneY < currYJumps; pruneY++) {
									if (pruneY == 0 && pruneX == 0) {
										continue; // skip the original position
									}

									doneSet.set((dx + pruneX) + (line + pruneY) * lineLength);
									map.setConstructMarking(x + pruneX, y + pruneY, false, binaryConstructionMarkValues, null);
								}
							}

							continue DX_LOOP;
						}
					}
				}

				// no bad position found, so set the construction mark
				map.setConstructMarking(x, y, true, binaryConstructionMarkValues, positionsToBeFlattened);
			}
		}

		// set the lastArea variable for the next run
		lastArea = mapArea;
	}

	/**
	 * Removes all construction marks on the screen.
	 */
	public void removeConstructionMarks() {
		if (lastArea != null) {
			lastArea.stream()
					.filterBounds(map.getWidth(), map.getHeight())
					.forEach((x, y) -> map.setConstructMarking(x, y, false, false, null));
			lastArea = null;
		}
	}

	/**
	 * Removes all construction marks in the given area.
	 * 
	 * @param area
	 *            The area to remove the marks
	 * @param notIn
	 *            The area of marks that should be skipped.
	 */
	private void removeConstructionMarks(IMapArea area, IMapArea notIn) {
		area.stream()
				.filterBounds(map.getWidth(), map.getHeight())
				.filter((x, y) -> !notIn.contains(x, y))
				.forEach((x, y) -> map.setConstructMarking(x, y, false, false, null));
	}
}
