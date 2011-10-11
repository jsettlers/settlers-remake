package jsettlers.logic.movable;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.area.InAreaFinder;
import jsettlers.logic.algorithms.path.astar.HexAStar;
import jsettlers.logic.algorithms.path.dijkstra.DijkstraAlgorithm;
import jsettlers.logic.map.hex.interfaces.IHexMovable;
import jsettlers.logic.objects.MapObjectsManager;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface IMovableGrid {

	void movableLeft(ISPosition2D position, IHexMovable movable);

	void movableEntered(ISPosition2D position, IHexMovable movable);

	MapObjectsManager getMapObjectsManager();

	IHexMovable getMovable(ISPosition2D position);

	boolean isBlocked(short x, short y);

	boolean isBlocked(ISPosition2D position);

	boolean isBlocked(IPathCalculateable pathCalculateable, short x, short y);

	boolean pushMaterial(ISPosition2D position, EMaterialType materialType);

	boolean popMaterial(ISPosition2D position, EMaterialType materialType);

	void changeHeightAt(ISPosition2D position, byte newHeight);

	void setMarked(ISPosition2D position, boolean marked);

	boolean isMarked(ISPosition2D position);

	byte getHeightAt(ISPosition2D position);

	boolean isInBounds(ISPosition2D position);

	byte getPlayerAt(ISPosition2D position);

	void setPlayerAt(ISPosition2D position, byte player);

	boolean fitsSearchType(ISPosition2D position, ESearchType searchType, IPathCalculateable pathCalculateable);

	boolean executeSearchType(ISPosition2D position, ESearchType searchType);

	boolean canPop(ISPosition2D position, EMaterialType material);

	boolean canPush(ISPosition2D position, EMaterialType material);

	ELandscapeType getLandscapeTypeAt(ISPosition2D position);

	HexAStar getAStar();

	DijkstraAlgorithm getDijkstra();

	InAreaFinder getInAreaFinder();
}
