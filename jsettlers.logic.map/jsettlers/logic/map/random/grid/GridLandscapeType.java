package jsettlers.logic.map.random.grid;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.logic.map.random.landscape.MeshEdge;
import jsettlers.logic.map.random.landscape.MeshLandscapeType;

public class GridLandscapeType {
	private GridLandscapeType() {
	}

	public static ELandscapeType convert(MeshLandscapeType landscape) {
		switch (landscape) {
			case DESERT:
				return ELandscapeType.DESERT;

			case MOUNTAIN:
				return ELandscapeType.MOUNTAIN;

			case SAND:
				return ELandscapeType.SAND;
			case SEA:
				return ELandscapeType.WATER;

			case GRASS:
			default:
				return ELandscapeType.GRASS;
		}
	}

	public static ELandscapeType forEdge(MeshEdge edge) {
		if (edge.getLeft() == null) {
			return convert(edge.getRight().getLandscape());
		} else if (edge.getRight() == null) {
			return convert(edge.getLeft().getLandscape());
		}
		MeshLandscapeType left = edge.getLeft().getLandscape();
		MeshLandscapeType right = edge.getRight().getLandscape();
		if (left == right) {
			return convert(left);
		} else if ((left == MeshLandscapeType.GRASS && right == MeshLandscapeType.MOUNTAIN)
		        || (right == MeshLandscapeType.GRASS && left == MeshLandscapeType.MOUNTAIN)) {
			return ELandscapeType.MOUNTAINBORDER;

		} else if ((left == MeshLandscapeType.SEA && right == MeshLandscapeType.GRASS)
		        || (right == MeshLandscapeType.SEA && left == MeshLandscapeType.GRASS)) {
			return ELandscapeType.SAND;
		} else {
			return convert(left);
		}
	}
}
