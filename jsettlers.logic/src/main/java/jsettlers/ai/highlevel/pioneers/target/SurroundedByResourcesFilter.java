package jsettlers.ai.highlevel.pioneers.target;

import jsettlers.ai.highlevel.AiPositions;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.position.RelativePoint;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.landscape.LandscapeGrid;

/**
 * @author codingberlin
 */
public class SurroundedByResourcesFilter implements AiPositions.AiPositionFilter {
	private final LandscapeGrid landscapeGrid;
	private final MainGrid mainGrid;
	private final EResourceType resourceType;
	private final static RelativePoint[] someNeighbours = {
			new RelativePoint(1, 1), new RelativePoint(-1, -1), new RelativePoint(1, -1), new RelativePoint(-1, 1) };

	public SurroundedByResourcesFilter(final MainGrid mainGrid, final LandscapeGrid landscapeGrid, final EResourceType resourceType) {
		this.resourceType = resourceType;
		this.landscapeGrid = landscapeGrid;
		this.mainGrid = mainGrid;
	}

	@Override
	public boolean contains(int x, int y) {
		for (RelativePoint relativeNeighbour : someNeighbours) {
			int neighbourX = relativeNeighbour.calculateX(x);
			int neighbourY = relativeNeighbour.calculateY(y);
			if (!mainGrid.isInBounds(neighbourX, neighbourY) || landscapeGrid.getResourceTypeAt(neighbourX, neighbourY) != resourceType
					|| landscapeGrid.getResourceAmountAt(neighbourX, neighbourY) == 0) {
				return false;
			}
		}
		return true;
	}
}
