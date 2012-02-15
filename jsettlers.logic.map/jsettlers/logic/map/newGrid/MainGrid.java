package jsettlers.logic.map.newGrid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.EBuildingType.BuildingAreaBitSet;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.logging.StopWatch;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.IMapData;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.common.map.object.MovableObject;
import jsettlers.common.map.object.StackObject;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.map.UIState;
import jsettlers.input.IGuiInputGrid;
import jsettlers.logic.algorithms.borders.BordersThread;
import jsettlers.logic.algorithms.borders.IBordersThreadGrid;
import jsettlers.logic.algorithms.construction.IConstructionMarkableMap;
import jsettlers.logic.algorithms.fogofwar.IFogOfWarGrid;
import jsettlers.logic.algorithms.fogofwar.IViewDistancable;
import jsettlers.logic.algorithms.fogofwar.NewFogOfWar;
import jsettlers.logic.algorithms.landmarks.ILandmarksThreadGrid;
import jsettlers.logic.algorithms.landmarks.NewLandmarkCorrection;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.area.IInAreaFinderMap;
import jsettlers.logic.algorithms.path.area.InAreaFinder;
import jsettlers.logic.algorithms.path.astar.HexAStar;
import jsettlers.logic.algorithms.path.astar.IAStarPathMap;
import jsettlers.logic.algorithms.path.dijkstra.DijkstraAlgorithm;
import jsettlers.logic.algorithms.path.dijkstra.IDijkstraPathMap;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.buildings.military.Barrack;
import jsettlers.logic.buildings.military.IOccupyableBuilding;
import jsettlers.logic.buildings.military.OccupyingBuilding;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.flags.FlagsGrid;
import jsettlers.logic.map.newGrid.landscape.EResourceType;
import jsettlers.logic.map.newGrid.landscape.LandscapeGrid;
import jsettlers.logic.map.newGrid.movable.IHexMovable;
import jsettlers.logic.map.newGrid.movable.MovableGrid;
import jsettlers.logic.map.newGrid.objects.AbstractHexMapObject;
import jsettlers.logic.map.newGrid.objects.IMapObjectsManagerGrid;
import jsettlers.logic.map.newGrid.objects.MapObjectsManager;
import jsettlers.logic.map.newGrid.objects.ObjectsGrid;
import jsettlers.logic.map.newGrid.partition.IPartitionableGrid;
import jsettlers.logic.map.newGrid.partition.PartitionsGrid;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IDiggerRequester;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IMaterialRequester;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapFileHeader.MapType;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.stack.IRequestsStackGrid;
import random.RandomSingleton;
import synchronic.timer.NetworkTimer;

/**
 * This is the main grid offering an interface for interacting with the grid.
 * 
 * @author Andreas Eberle
 */
public class MainGrid implements Serializable {
	private static final long serialVersionUID = 3824511313693431423L;

	final short width;
	final short height;

	final LandscapeGrid landscapeGrid;
	final ObjectsGrid objectsGrid;
	final PartitionsGrid partitionsGrid;
	final MovableGrid movableGrid;
	final FlagsGrid flagsGrid;

	final MovablePathfinderGrid movablePathfinderGrid;
	final MapObjectsManager mapObjectsManager;
	final BuildingsGrid buildingsGrid;
	final NewFogOfWar fogOfWar;

	transient IGraphicsGrid graphicsGrid;
	// transient LandmarksCorrectingThread landmarksCorrectionThread;
	transient NewLandmarkCorrection landmarksCorrection;
	transient ConstructionMarksGrid constructionMarksGrid;
	transient BordersThread bordersThread;
	transient IGuiInputGrid guiInputGrid;

	public MainGrid(short width, short height) {
		this.width = width;
		this.height = height;

		this.movablePathfinderGrid = new MovablePathfinderGrid();
		this.mapObjectsManager = new MapObjectsManager(new MapObjectsManagerGrid());

		this.landscapeGrid = new LandscapeGrid(width, height);
		this.objectsGrid = new ObjectsGrid(width, height);
		this.movableGrid = new MovableGrid(width, height, landscapeGrid);
		this.flagsGrid = new FlagsGrid(width, height);
		this.partitionsGrid = new PartitionsGrid(width, height, new PartitionableGrid());

		this.buildingsGrid = new BuildingsGrid();
		this.fogOfWar = new NewFogOfWar(width, height);

		initAdditionalGrids();
	}

	private void initAdditionalGrids() {
		this.graphicsGrid = new GraphicsGrid();
		this.landmarksCorrection = new NewLandmarkCorrection(new LandmarksGrid());
		this.constructionMarksGrid = new ConstructionMarksGrid();
		this.bordersThread = new BordersThread(new BordersThreadGrid());
		this.guiInputGrid = new GUIInputGrid();

		this.fogOfWar.startThread(new FogOfWarGrid());

		this.partitionsGrid.initPartitionsAlgorithm(movablePathfinderGrid.aStar);
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		initAdditionalGrids();
	}

	private MainGrid(IMapData mapGrid) {
		this((short) mapGrid.getWidth(), (short) mapGrid.getHeight());

		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				ELandscapeType landscape = mapGrid.getLandscape(x, y);
				setLandscapeTypeAt(x, y, landscape);
				landscapeGrid.setHeightAt(x, y, mapGrid.getLandscapeHeight(x, y));

				if (landscape == ELandscapeType.MOUNTAIN) {
					landscapeGrid.setResourceAt(x, y, EResourceType.values()[RandomSingleton.getInt(0, 2)], (byte) RandomSingleton.getInt(-100, 127));
				} else if (landscape.isWater()) {
					landscapeGrid.setResourceAt(x, y, EResourceType.FISH, (byte) RandomSingleton.getInt(-100, 127));
				}
			}
		}

		// tow passes, we might need the base grid tiles to add blocking, ...
		// status
		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				MapObject object = mapGrid.getMapObject(x, y);
				if (object != null && isOccupyableBuilding(object)) {
					addMapObject(x, y, object);
				}
				if ((x + y / 2) % 4 == 0 && y % 4 == 0 && isInsideWater(x, y)) {
					mapObjectsManager.addWaves(x, y);
					if (landscapeGrid.getResourceAmountAt(x, y) > 50) {
						mapObjectsManager.addFish(x, y);
					}
				}
			}
		}

		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				MapObject object = mapGrid.getMapObject(x, y);
				if (object != null && !isOccupyableBuilding(object)) {
					addMapObject(x, y, object);
				}
			}
		}
		System.out.println("grid filled");
	}

	private static boolean isOccupyableBuilding(MapObject object) {
		return object instanceof BuildingObject && ((BuildingObject) object).getType().getOccupyerPlaces().length > 0;
	}

	private boolean isInsideWater(short x, short y) {
		return isWaterSafe(x - 1, y) && isWaterSafe(x, y) && isWaterSafe(x + 1, y) && isWaterSafe(x - 1, y + 1) && isWaterSafe(x, y + 1)
				&& isWaterSafe(x + 1, y + 1) && isWaterSafe(x, y + 2) && isWaterSafe(x + 1, y + 2) && isWaterSafe(x + 2, y + 2);
	}

	private boolean isWaterSafe(int x, int y) {
		return isInBounds((short) x, (short) y) && landscapeGrid.getLandscapeTypeAt((short) x, (short) y).isWater();
	}

	public void stopGame() {
		bordersThread.cancel();
		fogOfWar.cancel();
	}

	public static MainGrid create(IMapData mapGrid) {
		return new MainGrid(mapGrid);
	}

	private void addMapObject(short x, short y, MapObject object) {
		ISPosition2D pos = new ShortPoint2D(x, y);

		if (object instanceof MapTreeObject) {
			if (isInBounds(x, y) && movablePathfinderGrid.isTreePlantable(x, y)) {
				mapObjectsManager.plantAdultTree(pos);
			}
		} else if (object instanceof MapStoneObject) {
			mapObjectsManager.addStone(pos, ((MapStoneObject) object).getCapacity());
		} else if (object instanceof StackObject) {
			placeStack(pos, ((StackObject) object).getType(), ((StackObject) object).getCount());
		} else if (object instanceof BuildingObject) {
			Building building = Building.getBuilding(((BuildingObject) object).getType(), ((BuildingObject) object).getPlayer());
			building.appearAt(buildingsGrid, pos);

			if (building instanceof IOccupyableBuilding) {
				Movable soldier = createNewMovableAt(((IOccupyableBuilding) building).getDoor(), EMovableType.SWORDSMAN_L1, building.getPlayer());
				soldier.setOccupyableBuilding((IOccupyableBuilding) building);
			}
		} else if (object instanceof MovableObject) {
			createNewMovableAt(pos, ((MovableObject) object).getType(), ((MovableObject) object).getPlayer());
		}
	}

	public MapFileHeader generateSaveHeader() {
		// TODO: description
		// TODO: count alive players, count all players
		return new MapFileHeader(MapType.SAVED_SINGLE, "saved game", "TODO: description", width, height, (short) 1, (short) 1, new Date());
	}

	private void placeStack(ISPosition2D pos, EMaterialType materialType, int count) {
		for (int i = 0; i < count; i++) {
			movablePathfinderGrid.pushMaterial(pos, materialType, true);
		}
	}

	public IGraphicsGrid getGraphicsGrid() {
		return graphicsGrid;
	}

	public IGuiInputGrid getGuiInputGrid() {
		return guiInputGrid;
	}

	protected final boolean isInBounds(short x, short y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	private void changePlayerAt(ISPosition2D position, byte player) {
		partitionsGrid.changePlayerAt(position.getX(), position.getY(), player);
		bordersThread.checkPosition(position);
		landmarksCorrection.reTest(position.getX(), position.getY());
	}

	public final Movable createNewMovableAt(ISPosition2D pos, EMovableType type, byte player) {
		Movable movable = new Movable(movablePathfinderGrid, pos, type, player);
		buildingsGrid.placeNewMovable(pos, movable);
		return movable;
	}

	private final boolean isLandscapeBlocking(ELandscapeType landscape) {
		return landscape.isWater() || landscape == ELandscapeType.MOOR || landscape == ELandscapeType.MOORINNER || landscape == ELandscapeType.SNOW;
	}

	protected final void setLandscapeTypeAt(short x, short y, ELandscapeType newType) {
		if (isLandscapeBlocking(newType)) {
			flagsGrid.setBlockedAndProtected(x, y, true);
		} else {
			if (isLandscapeBlocking(landscapeGrid.getLandscapeTypeAt(x, y))) {
				flagsGrid.setBlockedAndProtected(x, y, false);
			}
		}
		landscapeGrid.setLandscapeTypeAt(x, y, newType);
	}

	class PathfinderGrid implements IAStarPathMap, IDijkstraPathMap, IInAreaFinderMap, Serializable {
		private static final long serialVersionUID = -2775530442375843213L;

		@Override
		public boolean isBlocked(IPathCalculateable requester, short x, short y) {
			return flagsGrid.isBlocked(x, y) || (requester.needsPlayersGround() && requester.getPlayer() != partitionsGrid.getPlayerAt(x, y));
		}

		@Override
		public final float getCost(short sx, short sy, short tx, short ty) {
			return Constants.TILE_PATHFINDER_COST * (flagsGrid.isProtected(sx, sy) ? 3.5f : 1);
		}

		@Override
		public final void markAsOpen(short x, short y) {
			landscapeGrid.setDebugColor(x, y, Color.BLUE.getARGB());
		}

		@Override
		public final void markAsClosed(short x, short y) {
			landscapeGrid.setDebugColor(x, y, Color.RED.getARGB());
		}

		@Override
		public final void setDijkstraSearched(short x, short y) {
			markAsOpen(x, y);
		}

		@Override
		public final boolean fitsSearchType(short x, short y, ESearchType searchType, IPathCalculateable pathCalculable) {
			switch (searchType) {

			case FOREIGN_GROUND:
				return !flagsGrid.isBlocked(x, y) && !hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y)
						&& !partitionsGrid.isEnforcedByTower(x, y);

			case CUTTABLE_TREE:
				return isInBounds((short) (x - 1), (short) (y - 1))
						&& objectsGrid.hasCuttableObject((short) (x - 1), (short) (y - 1), EMapObjectType.TREE_ADULT)
						&& hasSamePlayer((short) (x - 1), (short) (y - 1), pathCalculable) && !isMarked(x, y);

			case PLANTABLE_TREE:
				return y < height - 1 && isTreePlantable(x, (short) (y + 1)) && !hasProtectedNeighbor(x, (short) (y + 1))
						&& hasSamePlayer(x, (short) (y + 1), pathCalculable) && !isMarked(x, y);

			case PLANTABLE_CORN:
				return isCornPlantable(x, y) && hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y) && !flagsGrid.isProtected(x, y);

			case CUTTABLE_CORN:
				return isCornCuttable(x, y) && hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y);

			case CUTTABLE_STONE:
				return y + 1 < height && x - 1 < width && objectsGrid.hasCuttableObject((short) (x - 1), (short) (y + 1), EMapObjectType.STONE)
						&& hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y);

			case ENEMY: {
				IMovable movable = movableGrid.getMovableAt(x, y);
				return movable != null && movable.getPlayer() != pathCalculable.getPlayer();
			}

			case RIVER:
				return isRiver(x, y) && hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y);

			case FISHABLE:
				return hasSamePlayer(x, y, pathCalculable) && hasNeighbourLandscape(x, y, ELandscapeType.WATER1);

			case NON_BLOCKED_OR_PROTECTED:
				return !(flagsGrid.isProtected(x, y) || flagsGrid.isBlocked(x, y))
						&& (!pathCalculable.needsPlayersGround() || hasSamePlayer(x, y, pathCalculable)) && movableGrid.getMovableAt(x, y) == null;

			case SOLDIER_BOWMAN:
				return isSoldierAt(x, y, searchType, pathCalculable.getPlayer());
			case SOLDIER_SWORDSMAN:
				return isSoldierAt(x, y, searchType, pathCalculable.getPlayer());
			case SOLDIER_PIKEMAN:
				return isSoldierAt(x, y, searchType, pathCalculable.getPlayer());

			case MOUNTAIN:
				return isInBounds(x, y) && !flagsGrid.isMarked(x, y) && canAddRessourceSign(x, y);

			case FOREIGN_MATERIAL:
				return isInBounds(x, y) && !hasSamePlayer(x, y, pathCalculable) && mapObjectsManager.hasStealableMaterial(x, y);

			default:
				System.err.println("can't handle search type in fitsSearchType(): " + searchType);
				return false;
			}
		}

		protected final boolean canAddRessourceSign(short x, short y) {
			return x % 2 == 0
					&& y % 2 == 0
					&& landscapeGrid.getLandscapeTypeAt(x, y) == ELandscapeType.MOUNTAIN
					&& !(objectsGrid.hasMapObjectType(x, y, EMapObjectType.FOUND_COAL)
							|| objectsGrid.hasMapObjectType(x, y, EMapObjectType.FOUND_IRON) || objectsGrid.hasMapObjectType(x, y,
							EMapObjectType.FOUND_GOLD));
		}

		private final boolean isSoldierAt(short x, short y, ESearchType searchType, byte player) {
			IHexMovable movable = movableGrid.getMovableAt(x, y);
			if (movable == null) {
				return false;
			} else {
				if (movable.getPlayer() == player && movable.canOccupyBuilding()) {
					EMovableType type = movable.getMovableType();
					switch (searchType) {
					case SOLDIER_BOWMAN:
						return type == EMovableType.BOWMAN_L1 || type == EMovableType.BOWMAN_L2 || type == EMovableType.BOWMAN_L3;
					case SOLDIER_SWORDSMAN:
						return type == EMovableType.SWORDSMAN_L1 || type == EMovableType.SWORDSMAN_L2 || type == EMovableType.SWORDSMAN_L3;
					case SOLDIER_PIKEMAN:
						return type == EMovableType.PIKEMAN_L1 || type == EMovableType.PIKEMAN_L2 || type == EMovableType.PIKEMAN_L3;
					default:
						return false;
					}
				} else {
					return false;
				}
			}
		}

		private final boolean isMarked(short x, short y) {
			return flagsGrid.isMarked(x, y);
		}

		private final boolean hasProtectedNeighbor(short x, short y) {
			for (EDirection currDir : EDirection.values) {
				if (flagsGrid.isProtected(currDir.getNextTileX(x), currDir.getNextTileY(y)))
					return true;
			}
			return false;
		}

		private final boolean hasNeighbourLandscape(short x, short y, ELandscapeType landscape) {
			for (ISPosition2D pos : new MapNeighboursArea(new ShortPoint2D(x, y))) {
				short currX = pos.getX();
				short currY = pos.getY();
				if (isInBounds(currX, currY) && landscapeGrid.getLandscapeTypeAt(currX, currY) == landscape) {
					return true;
				}
			}
			return false;
		}

		private final boolean hasSamePlayer(short x, short y, IPathCalculateable requester) {
			return partitionsGrid.getPlayerAt(x, y) == requester.getPlayer();
		}

		private final boolean isRiver(short x, short y) {
			ELandscapeType type = landscapeGrid.getLandscapeTypeAt(x, y);
			return type == ELandscapeType.RIVER1 || type == ELandscapeType.RIVER2 || type == ELandscapeType.RIVER3 || type == ELandscapeType.RIVER4;
		}

		final boolean isTreePlantable(short x, short y) {
			return landscapeGrid.getLandscapeTypeAt(x, y).isGrass() && !flagsGrid.isProtected(x, y) && !hasBlockedNeighbor(x, y);
		}

		private final boolean hasBlockedNeighbor(short x, short y) {
			for (EDirection currDir : EDirection.values) {
				short currX = currDir.getNextTileX(x);
				short currY = currDir.getNextTileY(y);
				if (!isInBounds(currX, currY) || flagsGrid.isBlocked(currX, currY)) {
					return true;
				}
			}

			return false;
		}

		private final boolean isCornPlantable(short x, short y) {
			ELandscapeType landscapeType = landscapeGrid.getLandscapeTypeAt(x, y);
			return (landscapeType.isGrass() || landscapeType == ELandscapeType.EARTH) && !flagsGrid.isProtected(x, y) && !hasProtectedNeighbor(x, y)
					&& !objectsGrid.hasMapObjectType(x, y, EMapObjectType.CORN_GROWING)
					&& !objectsGrid.hasMapObjectType(x, y, EMapObjectType.CORN_ADULT)
					&& !objectsGrid.hasNeighborObjectType(x, y, EMapObjectType.CORN_ADULT)
					&& !objectsGrid.hasNeighborObjectType(x, y, EMapObjectType.CORN_GROWING);
		}

		private final boolean isCornCuttable(short x, short y) {
			return objectsGrid.hasCuttableObject(x, y, EMapObjectType.CORN_ADULT);
		}

	}

	final class GraphicsGrid implements IGraphicsGrid {
		@Override
		public final short getHeight() {
			return height;
		}

		@Override
		public final short getWidth() {
			return width;
		}

		@Override
		public final IMovable getMovableAt(int x, int y) {
			return movableGrid.getMovableAt((short) x, (short) y);
		}

		@Override
		public final IMapObject getMapObjectsAt(int x, int y) {
			return objectsGrid.getObjectsAt((short) x, (short) y);
		}

		@Override
		public final byte getHeightAt(int x, int y) {
			return landscapeGrid.getHeightAt((short) x, (short) y);
		}

		@Override
		public final ELandscapeType getLandscapeTypeAt(int x, int y) {
			return landscapeGrid.getLandscapeTypeAt((short) x, (short) y);
		}

		@Override
		public final int getDebugColorAt(int x, int y) {
			// short value = (short) (partitionsGrid.getPartitionAt((short) x, (short) y) + 1);
			// return new Color((value % 3) * 0.33f, ((value / 3) % 3) * 0.33f, ((value / 9) % 3) * 0.33f, 1);

			// short value = (short) (partitionsGrid.getTowerCounterAt((short) x, (short) y) + 1);
			// return new Color((value % 3) * 0.33f, ((value / 3) % 3) * 0.33f, ((value / 9) % 3) * 0.33f, 1);

			// short value = (short) (partitionsGrid.getPlayerAt((short) x, (short) y) + 1);
			// return new Color((value % 3) * 0.33f, ((value / 3) % 3) * 0.33f, ((value / 9) % 3) * 0.33f, 1);

			return landscapeGrid.getDebugColor(x, y);

			// return objectsGrid.getMapObjectAt((short) x, (short) y, EMapObjectType.ATTACKABLE_TOWER) != null ? Color.RED : flagsGrid.isBlocked(
			// (short) x, (short) y) ? new Color(0, 0, 0, 1) : (flagsGrid.isProtected((short) x, (short) y) ? new Color(0, 0, 1, 1) : (flagsGrid
			// .isMarked((short) x, (short) y) ? new Color(0, 1, 0, 1) : null));

		}

		@Override
		public final boolean isBorder(int x, int y) {
			return flagsGrid.isBorderAt((short) x, (short) y);
		}

		@Override
		public final byte getPlayerAt(int x, int y) {
			return partitionsGrid.getPlayerAt((short) x, (short) y);
		}

		@Override
		public final byte getVisibleStatus(int x, int y) {
			return fogOfWar.getVisibleStatus(x, y);
		}

		@Override
		public final boolean isFogOfWarVisible(int x, int y) {
			return fogOfWar.isVisible(x, y);
		}

		@Override
		public final void setBackgroundListener(IGraphicsBackgroundListener backgroundListener) {
			landscapeGrid.setBackgroundListener(backgroundListener);
		}

	}

	final class MapObjectsManagerGrid implements IMapObjectsManagerGrid {
		private static final long serialVersionUID = 6223899915568781576L;

		@Override
		public final void setLandscape(short x, short y, ELandscapeType landscapeType) {
			setLandscapeTypeAt(x, y, landscapeType);
		}

		@Override
		public final void setBlocked(short x, short y, boolean blocked) {
			flagsGrid.setBlockedAndProtected(x, y, blocked);
		}

		@Override
		public final AbstractHexMapObject removeMapObjectType(short x, short y, EMapObjectType mapObjectType) {
			return objectsGrid.removeMapObjectType(x, y, mapObjectType);
		}

		@Override
		public final boolean removeMapObject(short x, short y, AbstractHexMapObject mapObject) {
			return objectsGrid.removeMapObject(x, y, mapObject);
		}

		@Override
		public final boolean isBlocked(short x, short y) {
			return flagsGrid.isBlocked(x, y);
		}

		@Override
		public final AbstractHexMapObject getMapObject(short x, short y, EMapObjectType mapObjectType) {
			return objectsGrid.getMapObjectAt(x, y, mapObjectType);
		}

		@Override
		public final void addMapObject(short x, short y, AbstractHexMapObject mapObject) {
			objectsGrid.addMapObjectAt(x, y, mapObject);
		}

		@Override
		public final short getWidth() {
			return width;
		}

		@Override
		public final short getHeight() {
			return height;
		}

		@Override
		public final boolean isInBounds(short x, short y) {
			return MainGrid.this.isInBounds(x, y);
		}

		@Override
		public final void setProtected(short x, short y, boolean protect) {
			flagsGrid.setProtected(x, y, protect);
		}

	}

	final class LandmarksGrid implements ILandmarksThreadGrid {
		@Override
		public final boolean isBlocked(short x, short y) {
			return flagsGrid.isBlocked(x, y);
		}

		@Override
		public final boolean isInBounds(short x, short y) {
			return MainGrid.this.isInBounds(x, y);
		}

		@Override
		public final short getPartitionAt(short x, short y) {
			return partitionsGrid.getPartitionAt(x, y);
		}

		@Override
		public final void setPartitionAndPlayerAt(short x, short y, short partition) {
			partitionsGrid.setPartitionAndPlayerAt(x, y, partition);
			bordersThread.checkPosition(new ShortPoint2D(x, y));

			AbstractHexMapObject building = objectsGrid.getMapObjectAt(x, y, EMapObjectType.BUILDING);
			if (building != null && ((IPlayerable) building).getPlayer() != partitionsGrid.getPlayerAt(x, y)) {
				((Building) building).kill();
			}
		}

		@Override
		public short getHeight() {
			return height;
		}

		@Override
		public short getWidth() {
			return width;
		}
	}

	final class ConstructionMarksGrid implements IConstructionMarkableMap {
		@Override
		public final void setConstructMarking(short x, short y, byte value) {
			mapObjectsManager.setConstructionMarking(x, y, value);
		}

		@Override
		public final short getWidth() {
			return width;
		}

		@Override
		public final short getHeight() {
			return height;
		}

		final boolean canConstructAt(short x, short y, EBuildingType type, byte player) {
			ELandscapeType[] landscapes = type.getGroundtypes();
			for (RelativePoint curr : type.getProtectedTiles()) {
				short currX = curr.calculateX(x);
				short currY = curr.calculateY(y);

				if (!canUsePositionForConstruction(currX, currY, landscapes, player)) {
					return false;
				}
			}
			return getConstructionMarkValue(x, y, type) >= 0;
		}

		@Override
		public final boolean canUsePositionForConstruction(short x, short y, ELandscapeType[] landscapeTypes, byte player) {
			return MainGrid.this.isInBounds(x, y) && !flagsGrid.isProtected(x, y) && partitionsGrid.getPlayerAt(x, y) == player
					&& isAllowedLandscape(x, y, landscapeTypes);
		}

		private final boolean isAllowedLandscape(short x, short y, ELandscapeType[] landscapes) {
			ELandscapeType landscapeAt = landscapeGrid.getLandscapeTypeAt(x, y);
			for (byte i = 0; i < landscapes.length; i++) {
				if (landscapeAt == landscapes[i]) {
					return true;
				}
			}
			return false;
		}

		@Override
		public final boolean isInBounds(short x, short y) {
			return MainGrid.this.isInBounds(x, y);
		}

		@Override
		public byte getConstructionMarkValue(short mapX, short mapY, EBuildingType buildingType) {
			final BuildingAreaBitSet buildingSet = buildingType.getBuildingAreaBitSet();
			int sum = 0;

			for (short x = buildingSet.minX; x <= buildingSet.maxX; x++) {
				for (short y = buildingSet.minX; y <= buildingSet.maxX; y++) {
					if (buildingSet.get(x, y)) {
						sum += landscapeGrid.getHeightAt((short) (mapX + x), (short) (mapY + y));
					}
				}
			}

			int avg = sum / buildingSet.numberOfPositions;
			float diff = 0;
			for (short x = buildingSet.minX; x <= buildingSet.maxX; x++) {
				for (short y = buildingSet.minX; y <= buildingSet.maxX; y++) {
					if (buildingSet.get(x, y)) {
						float currDiff = Math.abs(landscapeGrid.getHeightAt((short) (mapX + x), (short) (mapY + y))) - avg;
						diff += currDiff;
					}
				}
			}

			diff /= buildingSet.numberOfPositions;
			int result = (int) (2 * diff);

			if (result <= Byte.MAX_VALUE) {
				return (byte) result;
			} else {
				return -1;
			}
		}
	}

	final class MovablePathfinderGrid extends PathfinderGrid implements IMovableGrid, Serializable {
		private static final long serialVersionUID = 4006228724969442801L;

		transient HexAStar aStar;
		transient DijkstraAlgorithm dijkstra;
		transient private InAreaFinder inAreaFinder;

		public MovablePathfinderGrid() {
			initPathfinders();
		}

		private final void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
			ois.defaultReadObject();
			initPathfinders();
		}

		private final void initPathfinders() {
			aStar = new HexAStar(this, width, height);
			dijkstra = new DijkstraAlgorithm(this, aStar, width, height);
			inAreaFinder = new InAreaFinder(this, width, height);
		}

		@Override
		public final void movableLeft(ISPosition2D position, IHexMovable movable) {
			movableGrid.movableLeft(position, movable);
		}

		@Override
		public final void movableEntered(ISPosition2D position, IHexMovable movable) {
			movableGrid.movableEntered(position, movable);
		}

		@Override
		public final MapObjectsManager getMapObjectsManager() {
			return mapObjectsManager;
		}

		@Override
		public final IHexMovable getMovable(ISPosition2D position) {
			return movableGrid.getMovableAt(position.getX(), position.getY());
		}

		@Override
		public final boolean isBlocked(short x, short y) {
			return flagsGrid.isBlocked(x, y);
		}

		@Override
		public final boolean canPush(ISPosition2D position) {
			return mapObjectsManager.canPush(position);
		}

		@Override
		public final boolean pushMaterial(ISPosition2D position, EMaterialType materialType, boolean offer) {
			if (mapObjectsManager.pushMaterial(position.getX(), position.getY(), materialType)) {
				if (offer) {
					partitionsGrid.pushMaterial(position, materialType);
				}
				return true;
			} else
				return false;
		}

		@Override
		public final boolean canPop(ISPosition2D position, EMaterialType material) {
			return mapObjectsManager.canPop(position.getX(), position.getY(), material);
		}

		@Override
		public final boolean popMaterial(ISPosition2D position, EMaterialType materialType) {
			if (mapObjectsManager.popMaterial(position.getX(), position.getY(), materialType)) {
				return true;
			} else
				return false;
		}

		@Override
		public final ELandscapeType getLandscapeTypeAt(ISPosition2D position) {
			return landscapeGrid.getLandscapeTypeAt(position.getX(), position.getY());
		}

		@Override
		public final byte getHeightAt(ISPosition2D position) {
			return landscapeGrid.getHeightAt(position.getX(), position.getY());
		}

		@Override
		public final void changeHeightAt(ISPosition2D position, byte delta) {
			landscapeGrid.changeHeightAt(position.getX(), position.getY(), delta);
		}

		@Override
		public final void setMarked(ISPosition2D position, boolean marked) {
			flagsGrid.setMarked(position.getX(), position.getY(), marked);
		}

		@Override
		public final boolean isMarked(ISPosition2D position) {
			return flagsGrid.isMarked(position.getX(), position.getY());
		}

		@Override
		public final boolean isInBounds(ISPosition2D position) {
			final short x = position.getX();
			final short y = position.getY();
			return 0 <= x && x < width && 0 <= y && y < height;
		}

		@Override
		public final byte getPlayerAt(ISPosition2D position) {
			return partitionsGrid.getPlayerAt(position);
		}

		@Override
		public final void changePlayerAt(ISPosition2D position, byte player) {
			MainGrid.this.changePlayerAt(position, player);
		}

		@Override
		public final boolean executeSearchType(ISPosition2D position, ESearchType searchType) {
			return mapObjectsManager.executeSearchType(position, searchType);
		}

		@Override
		public final HexAStar getAStar() {
			return aStar;
		}

		@Override
		public final DijkstraAlgorithm getDijkstra() {
			return dijkstra;
		}

		@Override
		public final InAreaFinder getInAreaFinder() {
			return inAreaFinder;
		}

		@Override
		public final boolean fitsSearchType(ISPosition2D position, ESearchType searchType, IPathCalculateable pathCalculateable) {
			return super.fitsSearchType(position.getX(), position.getY(), searchType, pathCalculateable);
		}

		@Override
		public final void addJobless(IManageableBearer manageable) {
			partitionsGrid.addJobless(manageable);
		}

		@Override
		public final void addJobless(IManageableWorker worker) {
			partitionsGrid.addJobless(worker);

		}

		@Override
		public final void addJobless(IManageableBricklayer bricklayer) {
			partitionsGrid.addJobless(bricklayer);
		}

		@Override
		public final void addJobless(IManageableDigger digger) {
			partitionsGrid.addJobless(digger);
		}

		@Override
		public final void changeLandscapeAt(ISPosition2D pos, ELandscapeType type) {
			setLandscapeTypeAt(pos.getX(), pos.getY(), type);
		}

		@Override
		public final void placeSmoke(ISPosition2D pos, boolean place) {
			if (place) {
				mapObjectsManager.addSimpleMapObject(pos, EMapObjectType.SMOKE, false, (byte) 0);
			} else {
				mapObjectsManager.removeMapObjectType(pos.getX(), pos.getY(), EMapObjectType.SMOKE);
			}
		}

		@Override
		public final boolean isProtected(short x, short y) {
			return flagsGrid.isProtected(x, y);
		}

		@Override
		public final void placePig(ISPosition2D pos, boolean place) {
			mapObjectsManager.placePig(pos, place);
		}

		@Override
		public final boolean isPigThere(ISPosition2D pos) {
			return mapObjectsManager.isPigThere(pos);
		}

		@Override
		public final boolean isPigAdult(ISPosition2D pos) {
			return mapObjectsManager.isPigAdult(pos);
		}

		@Override
		public final boolean isEnforcedByTower(ISPosition2D pos) {
			return partitionsGrid.isEnforcedByTower(pos.getX(), pos.getY());
		}

		@Override
		public final boolean isAllowedForMovable(short x, short y, IPathCalculateable pathCalculatable) {
			return MainGrid.this.isInBounds(x, y) && !isBlocked(x, y)
					&& (!pathCalculatable.needsPlayersGround() || pathCalculatable.getPlayer() == partitionsGrid.getPlayerAt(x, y));
		}

		@Override
		public final EResourceType getResourceTypeAt(short x, short y) {
			return landscapeGrid.getResourceTypeAt(x, y);
		}

		@Override
		public final byte getResourceAmountAt(short x, short y) {
			return landscapeGrid.getResourceAmountAt(x, y);
		}

		@Override
		public final boolean canAddRessourceSign(ISPosition2D pos) {
			return super.canAddRessourceSign(pos.getX(), pos.getY());
		}

		@Override
		public final EMaterialType getMaterialTypeAt(ISPosition2D pos) {
			return mapObjectsManager.getMaterialTypeAt(pos.getX(), pos.getY());
		}

		@Override
		public final EMaterialType stealMaterialAt(ISPosition2D pos) {
			EMaterialType materialType = mapObjectsManager.stealMaterialAt(pos.getX(), pos.getY());
			if (materialType != null) {
				partitionsGrid.removeOfferAt(pos, materialType);
			}
			return materialType;
		}

		@Override
		public EMaterialType popToolProduction(ISPosition2D pos) {
			return partitionsGrid.popToolProduction(pos);
		}

		@Override
		public float getResourceAmountAround(short x, short y, EResourceType type) {
			return landscapeGrid.getResourceAmountAround(x, y, type);
		}

	}

	final class BordersThreadGrid implements IBordersThreadGrid {
		@Override
		public final byte getPlayerAt(short x, short y) {
			return partitionsGrid.getPlayerAt(x, y);
		}

		@Override
		public final void setBorderAt(short x, short y, boolean isBorder) {
			flagsGrid.setBorderAt(x, y, isBorder);
		}

		@Override
		public final boolean isInBounds(short x, short y) {
			return MainGrid.this.isInBounds(x, y);
		}
	}

	final class BuildingsGrid implements IBuildingsGrid, Serializable {
		private static final long serialVersionUID = -5567034251907577276L;

		private final RequestStackGrid requestStackGrid = new RequestStackGrid();

		@Override
		public final byte getHeightAt(ISPosition2D position) {
			return landscapeGrid.getHeightAt(position.getX(), position.getY());
		}

		@Override
		public final void pushMaterialsTo(ISPosition2D position, EMaterialType type, byte numberOf) {
			for (int i = 0; i < numberOf; i++) {
				movablePathfinderGrid.pushMaterial(position, type, true);
			}
		}

		@Override
		public final boolean setBuilding(ISPosition2D position, Building newBuilding) {
			if (MainGrid.this.isInBounds(position.getX(), position.getY())) {
				FreeMapArea area = new FreeMapArea(position, newBuilding.getBuildingType().getProtectedTiles());

				if (canConstructAt(area)) {
					setProtectedState(area, true);
					mapObjectsManager.addBuildingTo(position, newBuilding);
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		private final void setProtectedState(FreeMapArea area, boolean setProtected) {
			for (ISPosition2D curr : area) {
				if (MainGrid.this.isInBounds(curr.getX(), curr.getY()))
					flagsGrid.setProtected(curr.getX(), curr.getY(), setProtected);
			}
		}

		private final boolean canConstructAt(FreeMapArea area) {
			boolean isFree = true;

			for (ISPosition2D curr : area) {
				short x = curr.getX();
				short y = curr.getY();
				if (!isInBounds(x, y) || flagsGrid.isProtected(x, y) || flagsGrid.isBlocked(x, y)) {
					isFree = false;
				}
			}
			return isFree;
		}

		@Override
		public final void removeBuildingAt(ISPosition2D pos) {
			IBuilding building = (IBuilding) objectsGrid.getMapObjectAt(pos.getX(), pos.getY(), EMapObjectType.BUILDING);
			mapObjectsManager.removeMapObjectType(pos.getX(), pos.getY(), EMapObjectType.BUILDING);

			FreeMapArea area = new FreeMapArea(pos, building.getBuildingType().getProtectedTiles());
			for (ISPosition2D curr : area) {
				if (isInBounds(curr.getX(), curr.getY())) {
					flagsGrid.setBlockedAndProtected(curr.getX(), curr.getY(), false);
				}
			}
		}

		@Override
		public final void occupyArea(MapCircle toBeOccupied, ISPosition2D occupiersPosition, byte player) {
			List<ISPosition2D> occupiedPositions = partitionsGrid.occupyArea(toBeOccupied, occupiersPosition, player);
			bordersThread.checkPositions(occupiedPositions);
			landmarksCorrection.addLandmarkedPositions(occupiedPositions);
		}

		@Override
		public final void freeOccupiedArea(MapCircle occupied, ISPosition2D pos) {
			List<ISPosition2D> totallyFreed = partitionsGrid.freeOccupiedArea(occupied, pos);
			if (!totallyFreed.isEmpty()) {
				StopWatch watch = new MilliStopWatch();
				watch.start();

				List<OccupyingBuilding> allOccupying = OccupyingBuilding.getAllOccupyingBuildings();
				int maxSqDistance = 6 * CommonConstants.TOWERRADIUS * CommonConstants.TOWERRADIUS;

				List<OccupyingDistanceCombi> occupyingInRange = new LinkedList<OccupyingDistanceCombi>();
				for (OccupyingBuilding curr : allOccupying) {
					ISPosition2D currPos = curr.getPos();
					int dx = currPos.getX() - pos.getX();
					int dy = currPos.getY() - pos.getY();
					int sqDistance = dx * dx + dy * dy;
					if (sqDistance <= maxSqDistance && sqDistance > 0) { // > 0 to remove the tower just freeing the position
						occupyingInRange.add(new OccupyingDistanceCombi(sqDistance, curr));
					}
				}

				if (!occupyingInRange.isEmpty()) {
					Collections.sort(occupyingInRange);
					FreeMapArea freedArea = new FreeMapArea(totallyFreed);

					for (OccupyingDistanceCombi currOcc : occupyingInRange) {
						MapCircle currOccArea = currOcc.building.getOccupyablePositions();

						Iterator<ISPosition2D> iter = freedArea.iterator();
						for (ISPosition2D currPos = iter.next(); iter.hasNext(); currPos = iter.next()) {
							if (currOccArea.contains(currPos)) {
								iter.remove();
								partitionsGrid.occupyAt(currPos.getX(), currPos.getY(), currOcc.building.getPlayer());
								bordersThread.checkPosition(currPos);
								landmarksCorrection.reTest(currPos.getX(), currPos.getY());
							}
						}

						if (freedArea.isEmpty()) {
							break;
						}
					}
				}

				watch.stop("------------------ freeOccupiedArea needed: ");
			}
		}

		private final class OccupyingDistanceCombi implements Comparable<OccupyingDistanceCombi> {
			final int sqDistance;
			final OccupyingBuilding building;

			OccupyingDistanceCombi(int sqDistance, OccupyingBuilding building) {
				this.sqDistance = sqDistance;
				this.building = building;
			}

			@Override
			public final int compareTo(OccupyingDistanceCombi arg) {
				return sqDistance - arg.sqDistance;
			}

		}

		@Override
		public final void setBlocked(FreeMapArea area, boolean blocked) {
			for (ISPosition2D curr : area) {
				if (MainGrid.this.isInBounds(curr.getX(), curr.getY()))
					flagsGrid.setBlockedAndProtected(curr.getX(), curr.getY(), blocked);
			}
		}

		@Override
		public final short getWidth() {
			return width;
		}

		@Override
		public final short getHeight() {
			return height;
		}

		@Override
		public final IHexMovable getMovable(ISPosition2D position) {
			return movableGrid.getMovableAt(position.getX(), position.getY());
		}

		@Override
		public final void placeNewMovable(ISPosition2D position, IHexMovable movable) {
			movableGrid.movableEntered(position, movable);
		}

		@Override
		public final MapObjectsManager getMapObjectsManager() {
			return mapObjectsManager;
		}

		@Override
		public final IMovableGrid getMovableGrid() {
			return movablePathfinderGrid;
		}

		@Override
		public final void requestDiggers(IDiggerRequester requester, byte amount) {
			partitionsGrid.requestDiggers(requester, amount);
		}

		@Override
		public final void requestBricklayer(Building building, ISPosition2D bricklayerTargetPos, EDirection direction) {
			partitionsGrid.requestBricklayer(building, bricklayerTargetPos, direction);
		}

		@Override
		public final IRequestsStackGrid getRequestStackGrid() {
			return requestStackGrid;
		}

		@Override
		public final void requestBuildingWorker(EMovableType workerType, WorkerBuilding workerBuilding) {
			partitionsGrid.requestBuildingWorker(workerType, workerBuilding);
		}

		@Override
		public final void requestSoilderable(Barrack barrack) {
			partitionsGrid.requestSoilderable(barrack);
		}

		@Override
		public final DijkstraAlgorithm getDijkstra() {
			return movablePathfinderGrid.dijkstra;
		}

		private class RequestStackGrid implements IRequestsStackGrid, Serializable {
			private static final long serialVersionUID = 1278397366408051067L;

			@Override
			public final void request(IMaterialRequester requester, EMaterialType materialType, byte priority) {
				partitionsGrid.request(requester, materialType, priority);
			}

			@Override
			public final boolean hasMaterial(ISPosition2D position, EMaterialType materialType) {
				return mapObjectsManager.canPop(position.getX(), position.getY(), materialType);
			}

			@Override
			public final void popMaterial(ISPosition2D position, EMaterialType materialType) {
				mapObjectsManager.popMaterial(position.getX(), position.getY(), materialType);
			}

			@Override
			public final byte getStackSize(ISPosition2D position, EMaterialType materialType) {
				return mapObjectsManager.getStackSize(position.getX(), position.getY(), materialType);
			}

			@Override
			public final void releaseRequestsAt(ISPosition2D position, EMaterialType materialType) {
				partitionsGrid.releaseRequestsAt(position, materialType);

				byte stackSize = mapObjectsManager.getStackSize(position.getX(), position.getY(), materialType);
				for (byte i = 0; i < stackSize; i++) {
					partitionsGrid.pushMaterial(position, materialType);
				}
			}
		}

	}

	final class GUIInputGrid implements IGuiInputGrid {
		@Override
		public final IHexMovable getMovable(short x, short y) {
			return movableGrid.getMovableAt(x, y);
		}

		@Override
		public final short getWidth() {
			return width;
		}

		@Override
		public final short getHeight() {
			return height;
		}

		@Override
		public final IBuilding getBuildingAt(short x, short y) {
			return (IBuilding) objectsGrid.getMapObjectAt(x, y, EMapObjectType.BUILDING);
		}

		@Override
		public final boolean isInBounds(ISPosition2D position) {
			return MainGrid.this.isInBounds(position.getX(), position.getY());
		}

		@Override
		public final IBuildingsGrid getBuildingsGrid() {
			return buildingsGrid;
		}

		@Override
		public final byte getPlayerAt(ISPosition2D position) {
			return partitionsGrid.getPlayerAt(position);
		}

		@Override
		public final void resetDebugColors() {
			landscapeGrid.resetDebugColors();
		}

		@Override
		public final ISPosition2D getConstructablePositionAround(ISPosition2D pos, EBuildingType type) {
			byte player = partitionsGrid.getPlayerAt(pos);
			if (constructionMarksGrid.canConstructAt(pos.getX(), pos.getY(), type, player)) {
				return pos;
			} else {
				for (ISPosition2D neighbour : new MapNeighboursArea(pos)) {
					if (constructionMarksGrid.canConstructAt(neighbour.getX(), neighbour.getY(), type, player)) {
						return neighbour;
					}
				}
				return null;
			}
		}

		@Override
		public final void save() throws FileNotFoundException, IOException, InterruptedException {
			boolean pausing = NetworkTimer.isPausing();
			NetworkTimer.get().setPausing(true);
			try {
				Thread.sleep(30); // FIXME @Andreas serializer should wait until
									// threads did their work!
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			MapList list = MapList.getDefaultList();
			// TODO: pass on ui state.
			list.saveMap(new UIState(0, new ShortPoint2D(0, 0)), MainGrid.this);

			NetworkTimer.get().setPausing(pausing);
		}

		@Override
		public final void toggleFogOfWar() {
			fogOfWar.toggleEnabled();
		}

		@Override
		public IConstructionMarkableMap getConstructionMarksGrid() {
			return constructionMarksGrid;
		}
	}

	final class PartitionableGrid implements IPartitionableGrid, Serializable {
		private static final long serialVersionUID = 5631266851555264047L;

		@Override
		public final boolean isBlocked(short x, short y) {
			return flagsGrid.isBlocked(x, y);
		}

		@Override
		public final void changedPartitionAt(short x, short y) {

			landmarksCorrection.reTest(x, y);
		}

		@Override
		public final void setDebugColor(final short x, final short y, Color color) {
			landscapeGrid.setDebugColor(x, y, color.getARGB());
		}

	}

	final class FogOfWarGrid implements IFogOfWarGrid {
		@Override
		public final IMovable getMovableAt(short x, short y) {
			return movableGrid.getMovableAt(x, y);
		}

		@Override
		public final IMapObject getMapObjectsAt(short x, short y) {
			return objectsGrid.getObjectsAt(x, y);
		}

		@Override
		public final ConcurrentLinkedQueue<? extends IViewDistancable> getMovableViewDistancables() {
			return Movable.getAllMovables();
		}

		@Override
		public final ConcurrentLinkedQueue<? extends IViewDistancable> getBuildingViewDistancables() {
			return Building.getAllBuildings();
		}
	}

}
