package jsettlers.logic.algorithms.construction;

import java.util.BitSet;

import jsettlers.common.buildings.BuildingAreaBitSet;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;

/**
 * Algorithm to calculate the construction marks for the user.
 * 
 * @author Andreas Eberle
 * 
 */
public final class NewConstructionMarksAlgorithm {
	private final IConstructionMarkableMap map;
	private final byte player;

	private MapRectangle lastArea = null;

	public NewConstructionMarksAlgorithm(IConstructionMarkableMap map, byte player) {
		this.map = map;
		this.player = player;
	}

	public void calculateConstructMarks(final MapRectangle mapArea, final BuildingAreaBitSet buildingArea, final ELandscapeType[] landscapeTypes,
			RelativePoint[] flattenPositions) {
		if (lastArea != null) {
			removeConstructionMarks(lastArea, mapArea);
		}

		//
		final short[] xJumps = buildingArea.xJumps;
		final short[] yJumps = buildingArea.yJumps;

		final int lineLength = mapArea.getLineLength() + mapArea.getHeight() / 2;
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

				if (!mapArea.contains(x, y) || doneSet.get(dx + line * lineLength)) { // if this position has already been pruned.
					continue;
				}

				// go over all positions of the building and check if they are free
				for (int buildingDx = 0; buildingDx < buildingAreaWidth; buildingDx++) {
					for (int buildingDy = 0; buildingDy < buildingAreaHeight; buildingDy++) {
						int index = buildingDx + buildingDy * buildingAreaWidth;

						// if the position must be free, but isn't
						if (xJumps[index] != 0
								&& !map.canUsePositionForConstruction(x + buildingDx + xOffsetForBuilding, y + buildingDy + yOffsetForBuilding,
										landscapeTypes, player)) {

							map.setConstructMarking(x, y, false, null);

							// prune the positions we already know that they are invalid.
							for (int pruneX = 0; pruneX < xJumps[index]; pruneX++) {
								int currYJumps = yJumps[(buildingDx - pruneX) + buildingDy * buildingAreaWidth];
								for (int pruneY = 0; pruneY < currYJumps; pruneY++) {
									if (pruneY == 0 && pruneX == 0) {
										continue; // skip the original position
									}

									doneSet.set((dx + pruneX) + (line + pruneY) * lineLength);

									map.setConstructMarking(x + pruneX, y + pruneY, false, null);
								}
							}

							continue DX_LOOP;
						}
					}
				}

				// no bad position found, so set the construction mark
				map.setConstructMarking(x, y, true, flattenPositions);
			}
		}

		// set the lastArea variable for the next run
		lastArea = mapArea;
	}

	/**
	 * Removes all construction marks on the screen.
	 * 
	 */
	public void removeConstructionMarks() {
		if (lastArea != null) {
			for (ShortPoint2D pos : new MapShapeFilter(lastArea, map.getWidth(), map.getHeight())) {
				map.setConstructMarking(pos.x, pos.y, false, null);
			}
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
		for (ShortPoint2D pos : new MapShapeFilter(area, map.getWidth(), map.getHeight())) {
			if (!notIn.contains(pos)) {
				map.setConstructMarking(pos.x, pos.y, false, null);
			}
		}
	}
}
