package jsettlers.logic.map.random.instructions;

import java.util.Random;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.random.generation.PlayerStart;
import jsettlers.logic.map.random.grid.GridLandscapeType;
import jsettlers.logic.map.random.grid.MapGrid;
import jsettlers.logic.map.random.grid.PlaceholderObject;
import jsettlers.logic.map.random.landscape.MeshLandscapeType;

public abstract class ObjectInstruction extends GenerationInstruction {

	private TileMatcher matcher;

	public void execute(MapGrid grid, PlayerStart[] starts, Random random) {
		for (PlayerStart start : starts) {
			int startx = start.x + getIntParameter("dx", random);
			int starty = start.y + getIntParameter("dy", random);

			ELandscapeType onLandscape = GridLandscapeType.convert(MeshLandscapeType.parse(getParameter("on", random), null));
			LandFilter filter = getPlaceFilter(onLandscape, grid);

			boolean group = "true".equalsIgnoreCase(getParameter("group", random));

			int distance = getIntParameter("distance", random);

			if (!group) {
				matcher = new RandomMatcher(grid, startx, starty, distance, filter, random);
			} else {
				matcher = new GroupMatcher(grid, startx, starty, distance, filter, random);
			}

			int count = getIntParameter("count", random);

			int i = 0;
			for (ShortPoint2D place : matcher) {
				placeObject(grid, start, place.x, place.y, random);
				i++;
				if (i >= count) {
					break;
				}
			}
		}
	}

	private static class LandscapeFilter implements LandFilter {
		private final ELandscapeType onLandscape;
		private final MapGrid grid;

		public LandscapeFilter(ELandscapeType onLandscape, MapGrid grid) {
			this.onLandscape = onLandscape;
			this.grid = grid;
		}

		@Override
		public boolean isPlaceable(ShortPoint2D point) {
			return grid.isObjectPlaceable(point.x, point.y)
					&& (onLandscape == null || onLandscape.equals(grid.getLandscape(point.x, point.y)));
		}
	}

	protected LandFilter getPlaceFilter(ELandscapeType onLandscape, MapGrid grid) {
		return new LandscapeFilter(onLandscape, grid);
	}

	protected void placeObject(MapGrid grid, PlayerStart start, int x, int y, Random random) {
		grid.setMapObject(x, y, getObject(start, random));
		grid.reserveArea(x, y, getIntParameter("tight", random));
	}

	protected MapObject getObject(PlayerStart start, Random random) {
		return PlaceholderObject.getInstance();
	}

}
