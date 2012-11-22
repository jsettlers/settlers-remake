package jsettlers.logic.map.newGrid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.Color;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.EBuildingType.BuildingAreaBitSet;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
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
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.map.shapes.HexGridArea.HexGridAreaIterator;
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
import jsettlers.logic.algorithms.landmarks.LandmarksCorrectingThread;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.algorithms.path.area.IInAreaFinderMap;
import jsettlers.logic.algorithms.path.area.InAreaFinder;
import jsettlers.logic.algorithms.path.astar.normal.HexAStar;
import jsettlers.logic.algorithms.path.astar.normal.IAStarPathMap;
import jsettlers.logic.algorithms.path.dijkstra.DijkstraAlgorithm;
import jsettlers.logic.algorithms.path.dijkstra.IDijkstraPathMap;
import jsettlers.logic.algorithms.previewimage.PreviewImageCreator;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.buildings.military.IOccupyableBuilding;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.flags.FlagsGrid;
import jsettlers.logic.map.newGrid.landscape.LandscapeGrid;
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
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IDiggerRequester;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IMaterialRequester;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapFileHeader.MapType;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.interfaces.IAttackable;
import jsettlers.logic.newmovable.interfaces.INewMovableGrid;
import jsettlers.logic.objects.arrow.ArrowObject;
import jsettlers.logic.player.Player;
import jsettlers.logic.stack.IRequestsStackGrid;
import synchronic.timer.NetworkTimer;

/**
 * This is the main grid offering an interface for interacting with the grid.
 * 
 * @author Andreas Eberle
 */
public final class MainGrid implements Serializable {
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
	transient LandmarksCorrectingThread landmarksCorrection;
	transient ConstructionMarksGrid constructionMarksGrid;
	transient BordersThread bordersThread;
	transient IGuiInputGrid guiInputGrid;

	public MainGrid(short width, short height, byte numberOfPlayers) {
		this.width = width;
		this.height = height;

		this.partitionsGrid = new PartitionsGrid(width, height, numberOfPlayers);
		this.movablePathfinderGrid = new MovablePathfinderGrid();
		this.mapObjectsManager = new MapObjectsManager(new MapObjectsManagerGrid());

		this.landscapeGrid = new LandscapeGrid(width, height);
		this.objectsGrid = new ObjectsGrid(width, height);
		this.movableGrid = new MovableGrid(width, height, landscapeGrid);
		this.flagsGrid = new FlagsGrid(width, height);

		this.buildingsGrid = new BuildingsGrid();
		this.fogOfWar = new NewFogOfWar(width, height);

		initAdditionalGrids();
	}

	private void initAdditionalGrids() {
		this.graphicsGrid = new GraphicsGrid();
		this.landmarksCorrection = new LandmarksCorrectingThread(new LandmarksGrid());
		this.constructionMarksGrid = new ConstructionMarksGrid();
		this.bordersThread = new BordersThread(new BordersThreadGrid());
		this.guiInputGrid = new GUIInputGrid();
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		initAdditionalGrids();
	}

	public void startThreads() {
		bordersThread.start();
		fogOfWar.start(new FogOfWarGrid());
		landmarksCorrection.start();
	}

	public void stopThreads() {
		bordersThread.cancel();
		fogOfWar.cancel();
		landmarksCorrection.cancel();
	}

	public static MainGrid create(IMapData mapGrid, byte players) {
		return new MainGrid(mapGrid, players);
	}

	private MainGrid(IMapData mapGrid, byte players) {
		this((short) mapGrid.getWidth(), (short) mapGrid.getHeight(), players);

		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				ELandscapeType landscape = mapGrid.getLandscape(x, y);
				setLandscapeTypeAt(x, y, landscape);
				landscapeGrid.setHeightAt(x, y, mapGrid.getLandscapeHeight(x, y));
				landscapeGrid.setResourceAt(x, y, mapGrid.getResourceType(x, y), mapGrid.getResourceAmount(x, y));
				landscapeGrid.setBlockedPartition(x, y, mapGrid.getBlockedPartition(x, y));
			}
		}

		// two phases, we might need the base grid tiles to add blocking, status
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

	private void addMapObject(short x, short y, MapObject object) {
		ShortPoint2D pos = new ShortPoint2D(x, y);

		if (object instanceof MapTreeObject) {
			if (isInBounds(x, y) && movablePathfinderGrid.pathfinderGrid.isTreePlantable(x, y)) {
				mapObjectsManager.plantAdultTree(pos);
			}
		} else if (object instanceof MapStoneObject) {
			mapObjectsManager.addStone(pos, ((MapStoneObject) object).getCapacity());
		} else if (object instanceof StackObject) {
			placeStack(pos, ((StackObject) object).getType(), ((StackObject) object).getCount());
		} else if (object instanceof BuildingObject) {
			BuildingObject buildingObject = (BuildingObject) object;
			Building building = constructBuildingAt(pos, buildingObject.getType(), partitionsGrid.getPlayer(buildingObject.getPlayerId()), true);

			if (building instanceof IOccupyableBuilding) {
				NewMovable soldier = createNewMovableAt(building.getDoor(), EMovableType.SWORDSMAN_L1, building.getPlayer());
				soldier.setOccupyableBuilding((IOccupyableBuilding) building);
			}
		} else if (object instanceof MovableObject) {
			MovableObject movableObject = (MovableObject) object;
			createNewMovableAt(pos, movableObject.getType(), partitionsGrid.getPlayer(movableObject.getPlayerId()));
		}
	}

	public MapFileHeader generateSaveHeader() {
		// TODO: description
		// TODO: count alive players, count all players
		PreviewImageCreator previewImageCreator = new PreviewImageCreator(width, height, MapFileHeader.PREVIEW_IMAGE_SIZE,
				landscapeGrid.getPreviewImageDataSupplier());

		short[] bgImage = previewImageCreator.getPreviewImage();

		return new MapFileHeader(MapType.SAVED_SINGLE, "saved game", "TODO: description", width, height, (short) 1, (short) 1, new Date(), bgImage);
	}

	private void placeStack(ShortPoint2D pos, EMaterialType materialType, int count) {
		for (int i = 0; i < count; i++) {
			movablePathfinderGrid.dropMaterial(pos, materialType, true);
		}
	}

	public IGraphicsGrid getGraphicsGrid() {
		return graphicsGrid;
	}

	public IGuiInputGrid getGuiInputGrid() {
		return guiInputGrid;
	}

	/**
	 * FOR TESTS ONLY!!
	 * 
	 * @return
	 */
	public IAStarPathMap getPathfinderGrid() {
		return movablePathfinderGrid.pathfinderGrid;
	}

	public final boolean isInBounds(short x, short y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	public final NewMovable createNewMovableAt(ShortPoint2D pos, EMovableType type, Player player) {
		return new NewMovable(movablePathfinderGrid, type, pos, player);
	}

	/**
	 * Creates a new building at the given position.
	 * 
	 * @param position
	 *            The position to place the building.
	 * @param type
	 *            The {@link EBuildingType} of the building.
	 * @param player
	 *            The player owning the building.
	 * @param fullyConstructed
	 *            If true, the building will be placed as fully constructed building.<br>
	 *            If false, it will only be placed as a construction site.
	 * @return The newly created building.
	 */
	final Building constructBuildingAt(ShortPoint2D position, EBuildingType type, Player player, boolean fullyConstructed) {
		Building building = Building.getBuilding(type, player);
		building.constructAt(buildingsGrid, position, fullyConstructed);
		return building;
	}

	protected final void setLandscapeTypeAt(short x, short y, ELandscapeType newType) {
		if (newType.isBlocking) {
			flagsGrid.setBlockedAndProtected(x, y, true);
		} else {
			if (landscapeGrid.getLandscapeTypeAt(x, y).isBlocking) {
				flagsGrid.setBlockedAndProtected(x, y, false);
			}
		}
		landscapeGrid.setLandscapeTypeAt(x, y, newType);
	}

	final class PathfinderGrid implements IAStarPathMap, IDijkstraPathMap, IInAreaFinderMap, Serializable {
		private static final long serialVersionUID = -2775530442375843213L;

		@Override
		public boolean isBlocked(IPathCalculateable requester, short x, short y) {
			return flagsGrid.isBlocked(x, y) || (requester.needsPlayersGround() && requester.getPlayerId() != partitionsGrid.getPlayerIdAt(x, y));
		}

		@Override
		public final float getCost(short sx, short sy, short tx, short ty) {
			// return Constants.TILE_PATHFINDER_COST * (flagsGrid.isProtected(sx, sy) ? 3.5f : 1);
			return 1;
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
				return !flagsGrid.isBlocked(x, y) && !hasSamePlayer(x, y, pathCalculable) && !partitionsGrid.isEnforcedByTower(x, y);

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
				return movable != null && movable.getPlayerId() != pathCalculable.getPlayerId();
			}

			case RIVER:
				return isRiver(x, y) && hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y);

			case FISHABLE:
				return hasSamePlayer(x, y, pathCalculable) && hasNeighbourLandscape(x, y, ELandscapeType.WATER1);

			case NON_BLOCKED_OR_PROTECTED:
				return !(flagsGrid.isProtected(x, y) || flagsGrid.isBlocked(x, y))
						&& (!pathCalculable.needsPlayersGround() || hasSamePlayer(x, y, pathCalculable)) && movableGrid.getMovableAt(x, y) == null;

			case SOLDIER_BOWMAN:
				return isSoldierAt(x, y, searchType, pathCalculable.getPlayerId());
			case SOLDIER_SWORDSMAN:
				return isSoldierAt(x, y, searchType, pathCalculable.getPlayerId());
			case SOLDIER_PIKEMAN:
				return isSoldierAt(x, y, searchType, pathCalculable.getPlayerId());

			case RESOURCE_SIGNABLE:
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
			NewMovable movable = movableGrid.getMovableAt(x, y);
			if (movable == null) {
				return false;
			} else {
				if (movable.getPlayerId() == player && movable.canOccupyBuilding()) {
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
			for (ShortPoint2D pos : new MapNeighboursArea(new ShortPoint2D(x, y))) {
				short currX = pos.x;
				short currY = pos.y;
				if (isInBounds(currX, currY) && landscapeGrid.getLandscapeTypeAt(currX, currY) == landscape) {
					return true;
				}
			}
			return false;
		}

		private final boolean hasSamePlayer(short x, short y, IPathCalculateable requester) {
			return partitionsGrid.getPlayerIdAt(x, y) == requester.getPlayerId();
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

		@Override
		public void setDebugColor(short x, short y, Color color) {
			landscapeGrid.setDebugColor(x, y, color.getARGB());
		}

		@Override
		public short getBlockedPartition(short x, short y) {
			return landscapeGrid.getBlockedPartitionAt(x, y);
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
			return movableGrid.getMovableAt(x, y);
		}

		@Override
		public final IMapObject getMapObjectsAt(int x, int y) {
			return objectsGrid.getObjectsAt(x, y);
		}

		@Override
		public final byte getHeightAt(int x, int y) {
			return landscapeGrid.getHeightAt(x, y);
		}

		@Override
		public final ELandscapeType getLandscapeTypeAt(int x, int y) {
			return landscapeGrid.getLandscapeTypeAt(x, y);
		}

		@Override
		public final int getDebugColorAt(int x, int y) {
			// int value = landscapeGrid.getBlockedPartitionAt(x, y) + 1;
			// return Color.getARGB((value % 3) * 0.33f, ((value / 3) % 3) * 0.33f, ((value / 9) % 3) * 0.33f, 1);

			int value = partitionsGrid.getPartitionIdAt(x, y) + 1;
			return Color.getARGB((value % 3) * 0.33f, ((value / 3) % 3) * 0.33f, ((value / 9) % 3) * 0.33f, 1);

			// int value = partitionsGrid.getTowerCounterAt(x, y) + 1;
			// return Color.getABGR((value % 3) * 0.33f, ((value / 3) % 3) * 0.33f, ((value / 9) % 3) * 0.33f, 1);

			// int value = partitionsGrid.getPlayerAt(x, y).playerId + 1;
			// return Color.getABGR((value % 3) * 0.33f, ((value / 3) % 3) * 0.33f, ((value / 9) % 3) * 0.33f, 1);

			// return landscapeGrid.getDebugColor(x, y);

			// return flagsGrid.isMarked(x, y) ? Color.ORANGE.getARGB()
			// : (objectsGrid.getMapObjectAt(x, y, EMapObjectType.INFORMABLE_MAP_OBJECT) != null ? Color.GREEN.getARGB() : (objectsGrid
			// .getMapObjectAt(x, y, EMapObjectType.ATTACKABLE_TOWER) != null ? Color.RED.getARGB()
			// : (flagsGrid.isBlocked(x, y) ? Color.BLACK.getARGB() : (flagsGrid.isProtected(x, y) ? Color.BLUE.getARGB() : 0))));

			// return Color.BLACK.getARGB();

			// return objectsGrid.getMapObjectAt( x, y, EMapObjectType.ARROW) != null ? Color.RED.getABGR() : 0;
		}

		@Override
		public final boolean isBorder(int x, int y) {
			return flagsGrid.isBorderAt(x, y);
		}

		@Override
		public final byte getPlayerIdAt(int x, int y) {
			return partitionsGrid.getPlayerIdAt(x, y);
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

		@Override
		public int nextDrawableX(int x, int y, int maxX) {
			return x + 1;
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

		@Override
		public EResourceType getRessourceTypeAt(short x, short y) {
			return landscapeGrid.getResourceTypeAt(x, y);
		}

		@Override
		public byte getRessourceAmountAt(short x, short y) {
			return landscapeGrid.getResourceAmountAt(x, y);
		}

		@Override
		public void hitWithArrowAt(ArrowObject arrow) {
			short x = arrow.getTargetX();
			short y = arrow.getTargetY();

			NewMovable movable = movableGrid.getMovableAt(x, y);
			if (movable != null) {
				movable.receiveHit(arrow.getHitStrength(), arrow.getPlayer());
				mapObjectsManager.removeMapObject(x, y, arrow);
			}
		}
	}

	final class LandmarksGrid implements ILandmarksThreadGrid {
		@Override
		public final boolean isBlocked(short x, short y) {
			return MainGrid.this.isInBounds(x, y) && flagsGrid.isBlocked(x, y) && landscapeGrid.getBlockedPartitionAt(x, y) > 0;
		}

		@Override
		public final boolean isInBounds(short x, short y) {
			return MainGrid.this.isInBounds(x, y);
		}

		@Override
		public final short getPartitionAt(short x, short y) {
			return partitionsGrid.getPartitionIdAt(x, y);
		}

		@Override
		public final void setPartitionAndPlayerAt(short x, short y, short partition) {
			partitionsGrid.setPartitionAt(x, y, partition);
			bordersThread.checkPosition(new ShortPoint2D(x, y));

			AbstractHexMapObject building = objectsGrid.getMapObjectAt(x, y, EMapObjectType.BUILDING);
			if (building != null && ((IPlayerable) building).getPlayerId() != partitionsGrid.getPlayerIdAt(x, y)) {
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

		@Override
		public final short getBlockedPartition(short x, short y) {
			return landscapeGrid.getBlockedPartitionAt(x, y);
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

		final boolean canConstructAt(short x, short y, EBuildingType type, Player player) {
			ELandscapeType[] landscapes = type.getGroundtypes();
			for (RelativePoint curr : type.getProtectedTiles()) {
				short currX = curr.calculateX(x);
				short currY = curr.calculateY(y);

				if (!canUsePositionForConstruction(currX, currY, landscapes, player.playerId)) {
					return false;
				}
			}
			return getConstructionMarkValue(x, y, type) >= 0;
		}

		@Override
		public final boolean canUsePositionForConstruction(short x, short y, ELandscapeType[] landscapeTypes, byte player) {
			return MainGrid.this.isInBounds(x, y) && !flagsGrid.isProtected(x, y) && partitionsGrid.getPlayerIdAt(x, y) == player
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

	final class MovablePathfinderGrid implements INewMovableGrid, Serializable {
		private static final long serialVersionUID = 4006228724969442801L;

		transient HexAStar aStar;
		transient DijkstraAlgorithm dijkstra;
		private transient InAreaFinder inAreaFinder;
		private transient PathfinderGrid pathfinderGrid;

		public MovablePathfinderGrid() {
			initPathfinders();
		}

		private final void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
			ois.defaultReadObject();
			initPathfinders();
		}

		private final void initPathfinders() {
			pathfinderGrid = new PathfinderGrid();

			aStar = new HexAStar(pathfinderGrid, width, height);
			dijkstra = new DijkstraAlgorithm(pathfinderGrid, aStar, width, height);
			inAreaFinder = new InAreaFinder(pathfinderGrid, width, height);
		}

		@Override
		public final boolean isBlocked(short x, short y) {
			return flagsGrid.isBlocked(x, y);
		}

		@Override
		public final boolean isProtected(short x, short y) {
			return flagsGrid.isProtected(x, y);
		}

		@Override
		public final boolean isBlockedOrProtected(short x, short y) {
			return isBlocked(x, y) || isProtected(x, y);
		}

		@Override
		public final boolean canPop(ShortPoint2D position, EMaterialType material) {
			return mapObjectsManager.canPop(position.x, position.y, material);
		}

		@Override
		public final byte getHeightAt(ShortPoint2D position) {
			return landscapeGrid.getHeightAt(position.x, position.y);
		}

		@Override
		public final void setMarked(ShortPoint2D position, boolean marked) {
			flagsGrid.setMarked(position.x, position.y, marked);
		}

		@Override
		public final boolean isMarked(ShortPoint2D position) {
			return flagsGrid.isMarked(position.x, position.y);
		}

		@Override
		public final boolean executeSearchType(ShortPoint2D position, ESearchType searchType) {
			return mapObjectsManager.executeSearchType(position, searchType);
		}

		@Override
		public final void placeSmoke(ShortPoint2D pos, boolean place) {
			if (place) {
				mapObjectsManager.addSimpleMapObject(pos, EMapObjectType.SMOKE, false, null);
			} else {
				mapObjectsManager.removeMapObjectType(pos.x, pos.y, EMapObjectType.SMOKE);
			}
		}

		@Override
		public void changePlayerAt(ShortPoint2D position, Player player) { // FIXME @Andreas Eberle make pioneer work again
			// partitionsGrid.changePlayerAt(position.x, position.y, player);
			// bordersThread.checkPosition(position);
			// landmarksCorrection.addLandmarkedPosition(position);
		}

		@Override
		public final boolean isValidPosition(IPathCalculateable pathRequester, ShortPoint2D pos) {
			short x = pos.x, y = pos.y;
			return MainGrid.this.isInBounds(x, y) && !isBlocked(x, y)
					&& (!pathRequester.needsPlayersGround() || pathRequester.getPlayerId() == partitionsGrid.getPlayerIdAt(x, y));
		}

		@Override
		public float getResourceAmountAround(short x, short y, EResourceType type) {
			return landscapeGrid.getResourceAmountAround(x, y, type);
		}

		@Override
		public void addJobless(IManageableBearer bearer) {
			partitionsGrid.getPartitionAt(bearer).addJobless(bearer);
		}

		@Override
		public void removeJobless(IManageableBearer bearer) {
			partitionsGrid.getPartitionAt(bearer).removeJobless(bearer);
		}

		@Override
		public void addJobless(IManageableWorker worker) {
			partitionsGrid.getPartitionAt(worker).addJobless(worker);
		}

		@Override
		public void removeJobless(IManageableWorker worker) {
			partitionsGrid.getPartitionAt(worker).removeJobless(worker);
		}

		@Override
		public void addJobless(IManageableDigger digger) {
			partitionsGrid.getPartitionAt(digger).addJobless(digger);
		}

		@Override
		public void removeJobless(IManageableDigger digger) {
			partitionsGrid.getPartitionAt(digger).removeJobless(digger);
		}

		@Override
		public void addJobless(IManageableBricklayer bricklayer) {
			partitionsGrid.getPartitionAt(bricklayer).addJobless(bricklayer);
		}

		@Override
		public void removeJobless(IManageableBricklayer bricklayer) {
			partitionsGrid.getPartitionAt(bricklayer).removeJobless(bricklayer);
		}

		@Override
		public boolean takeMaterial(ShortPoint2D position, EMaterialType materialType) {
			short x = position.x;
			short y = position.y;
			if (mapObjectsManager.popMaterial(x, y, materialType)) {
				return true;
			} else
				return false;
		}

		@Override
		public boolean dropMaterial(ShortPoint2D position, EMaterialType materialType, boolean offer) {
			if (mapObjectsManager.pushMaterial(position.x, position.y, materialType)) {
				if (offer) {
					partitionsGrid.getPartitionAt(position.x, position.y).addOffer(position, materialType);
				}
				return true;
			} else
				return false;
		}

		@Override
		public EDirection getDirectionOfSearched(ShortPoint2D position, ESearchType searchType) {
			if (searchType == ESearchType.FISHABLE) {
				for (EDirection direction : EDirection.values) {
					ShortPoint2D currPos = direction.getNextHexPoint(position);
					short x = currPos.x, y = currPos.y;

					if (isInBounds(x, y) && landscapeGrid.getLandscapeTypeAt(x, y).isWater()) {
						return direction;
					}
				}
				return null;
			} else if (searchType == ESearchType.RIVER) {
				for (EDirection direction : EDirection.values) {
					ShortPoint2D currPos = direction.getNextHexPoint(position);
					short x = currPos.x, y = currPos.y;
					ELandscapeType landscapeTypeAt = landscapeGrid.getLandscapeTypeAt(x, y);

					if (isInBounds(x, y)
							&& (landscapeTypeAt == ELandscapeType.RIVER1 || landscapeTypeAt == ELandscapeType.RIVER2
									|| landscapeTypeAt == ELandscapeType.RIVER3 || landscapeTypeAt == ELandscapeType.RIVER4)) {
						return direction;
					}
				}
				return null;
			} else {
				return null;
			}
		}

		@Override
		public EMaterialType popToolProductionRequest(ShortPoint2D pos) {
			return partitionsGrid.getPartitionAt(pos.x, pos.y).popToolProduction(pos);
		}

		@Override
		public final boolean isPigAdult(ShortPoint2D pos) {
			return mapObjectsManager.isPigAdult(pos);
		}

		@Override
		public void placePigAt(ShortPoint2D pos, boolean place) {
			mapObjectsManager.placePig(pos, place);
		}

		@Override
		public boolean hasPigAt(ShortPoint2D position) {
			return mapObjectsManager.isPigThere(position);
		}

		@Override
		public boolean canPushMaterial(ShortPoint2D position) {
			return mapObjectsManager.canPush(position);
		}

		@Override
		public void changeHeightTowards(short x, short y, byte targetHeight) {
			byte currHeight = landscapeGrid.getHeightAt(x, y);
			landscapeGrid.setHeightAt(x, y, (byte) (currHeight + Math.signum(targetHeight - currHeight)));
			landscapeGrid.setLandscapeTypeAt(x, y, ELandscapeType.FLATTENED);
		}

		@Override
		public boolean hasNoMovableAt(short x, short y) {
			return movableGrid.hasNoMovableAt(x, y);
		}

		@Override
		public boolean isFreePosition(ShortPoint2D position) {
			short x = position.x;
			short y = position.y;

			return isInBounds(x, y) && !flagsGrid.isBlocked(x, y) && movableGrid.hasNoMovableAt(x, y);
		}

		@Override
		public void leavePosition(ShortPoint2D position, NewMovable movable) {
			movableGrid.movableLeft(position, movable);
		}

		@Override
		public void enterPosition(ShortPoint2D position, NewMovable movable, boolean informFullArea) {
			movableGrid.movableEntered(position, movable);

			if (movable.isAttackable()) {
				movableGrid.informMovables(movable, position.x, position.y, informFullArea);
				objectsGrid.informObjectsAboutAttackble(position, movable, informFullArea, !EMovableType.isBowman(movable.getMovableType()));
			}
		}

		@Override
		public Path calculatePathTo(IPathCalculateable pathRequester, ShortPoint2D targetPos) {
			return aStar.findPath(pathRequester, targetPos);
		}

		@Override
		public Path searchDijkstra(IPathCalculateable pathCalculateable, short centerX, short centerY, short radius, ESearchType searchType) {
			return dijkstra.find(pathCalculateable, centerX, centerY, (short) 1, radius, searchType);
		}

		@Override
		public Path searchInArea(IPathCalculateable pathCalculateable, short centerX, short centerY, short radius, ESearchType searchType) {
			ShortPoint2D target = inAreaFinder.find(pathCalculateable, centerX, centerY, radius, searchType);
			if (target != null) {
				return calculatePathTo(pathCalculateable, target);
			} else {
				return null;
			}
		}

		@Override
		public NewMovable getMovableAt(short x, short y) {
			return movableGrid.getMovableAt(x, y);
		}

		@Override
		public void addSelfDeletingMapObject(ShortPoint2D position, EMapObjectType mapObjectType, float duration, Player player) {
			mapObjectsManager.addSelfDeletingMapObject(position, mapObjectType, duration, player);
		}

		@Override
		public boolean isInBounds(short x, short y) {
			return MainGrid.this.isInBounds(x, y);
		}

		@Override
		public boolean fitsSearchType(IPathCalculateable pathCalculable, ShortPoint2D pos, ESearchType searchType) {
			return pathfinderGrid.fitsSearchType(pos.x, pos.y, searchType, pathCalculable);
		}

		@Override
		public ELandscapeType getLandscapeTypeAt(short x, short y) {
			return landscapeGrid.getLandscapeTypeAt(x, y);
		}

		@Override
		public IAttackable getEnemyInSearchArea(ShortPoint2D position, IAttackable searchingAttackable, short searchRadius) {
			boolean isBowman = EMovableType.isBowman(searchingAttackable.getMovableType());

			IAttackable enemy = getEnemyInSearchArea(searchingAttackable.getPlayerId(), new HexGridArea(position.x, position.y, (short) 1,
					searchRadius), isBowman);
			if (enemy == null && !isBowman) {
				enemy = getEnemyInSearchArea(searchingAttackable.getPlayerId(), new HexGridArea(position.x, position.y, searchRadius,
						Constants.TOWER_SEARCH_RADIUS), isBowman);
			}

			return enemy;
		}

		private IAttackable getEnemyInSearchArea(byte searchingPlayer, HexGridArea area, boolean isBowman) {
			for (ShortPoint2D curr : area) {
				short x = curr.x;
				short y = curr.y;

				if (0 <= x && x < width && 0 <= y && y < height) {
					IAttackable currAttackable = movableGrid.getMovableAt(x, y);
					if (currAttackable == null && !isBowman) {
						currAttackable = (IAttackable) objectsGrid.getMapObjectAt(x, y, EMapObjectType.ATTACKABLE_TOWER);
					}

					if (currAttackable != null && MovableGrid.isEnemy(searchingPlayer, currAttackable)) {
						return currAttackable;
					}
				}
			}

			return null;
		}

		@Override
		public void addArrowObject(ShortPoint2D attackedPos, ShortPoint2D shooterPos, Player shooterPlayer, float hitStrength) {
			mapObjectsManager.addArrowObject(attackedPos, shooterPos, shooterPlayer, hitStrength);
		}

		@Override
		public final ShortPoint2D calcDecentralizeVector(short x, short y) {
			HexGridArea area = new HexGridArea(x, y, (short) 1, Constants.MOVABLE_FLOCK_TO_DECENTRALIZE_MAX_RADIUS);
			HexGridAreaIterator iter = area.iterator();
			int dx = 0, dy = 0;

			while (iter.hasNext()) {
				short radius = iter.getRadiusOfNext();
				ShortPoint2D curr = iter.next();
				short currX = curr.x;
				short currY = curr.y;

				int factor;

				if (!MainGrid.this.isInBounds(currX, currY) || flagsGrid.isBlocked(currX, currY)) {
					factor = radius == 1 ? 6 : 2;
				} else if (!movableGrid.hasNoMovableAt(currX, currY)) {
					factor = Constants.MOVABLE_FLOCK_TO_DECENTRALIZE_MAX_RADIUS - radius + 1;
				} else {
					continue;
				}

				dx += (x - currX) * factor;
				dy += (y - currY) * factor;
			}

			return new ShortPoint2D(dx, dy);
		}
	}

	final class BordersThreadGrid implements IBordersThreadGrid {
		@Override
		public final byte getPlayerIdAt(short x, short y) {
			return partitionsGrid.getPlayerIdAt(x, y);
		}

		@Override
		public final void setBorderAt(short x, short y, boolean isBorder) {
			flagsGrid.setBorderAt(x, y, isBorder);
		}

		@Override
		public final boolean isInBounds(short x, short y) {
			return MainGrid.this.isInBounds(x, y);
		}

		@Override
		public final short getBlockedPartition(short x, short y) {
			return landscapeGrid.getBlockedPartitionAt(x, y);
		}
	}

	final class BuildingsGrid implements IBuildingsGrid, Serializable {
		private static final long serialVersionUID = -5567034251907577276L;

		private final RequestStackGrid requestStackGrid = new RequestStackGrid();

		@Override
		public final byte getHeightAt(ShortPoint2D position) {
			return landscapeGrid.getHeightAt(position.x, position.y);
		}

		@Override
		public final void pushMaterialsTo(ShortPoint2D position, EMaterialType type, byte numberOf) {
			for (int i = 0; i < numberOf; i++) {
				movablePathfinderGrid.dropMaterial(position, type, true);
			}
		}

		@Override
		public final boolean setBuilding(ShortPoint2D position, Building newBuilding) {
			if (MainGrid.this.isInBounds(position.x, position.y)) {
				FreeMapArea area = new FreeMapArea(position, newBuilding.getBuildingType().getProtectedTiles());

				if (canConstructAt(area)) {
					setProtectedState(area, true);
					mapObjectsManager.addBuildingTo(position, newBuilding);
					objectsGrid.setBuildingArea(area, newBuilding);
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		private final void setProtectedState(FreeMapArea area, boolean setProtected) {
			for (ShortPoint2D curr : area) {
				if (MainGrid.this.isInBounds(curr.x, curr.y))
					flagsGrid.setProtected(curr.x, curr.y, setProtected);
			}
		}

		private final boolean canConstructAt(FreeMapArea area) {
			boolean isFree = true;

			for (ShortPoint2D curr : area) {
				short x = curr.x;
				short y = curr.y;

				if (!isInBounds(x, y) || flagsGrid.isProtected(x, y) || flagsGrid.isBlocked(x, y)) {
					isFree = false; // TODO @Andreas Eberle remove if
				}
			}
			return isFree;
		}

		@Override
		public final void removeBuildingAt(ShortPoint2D pos) {
			IBuilding building = (IBuilding) objectsGrid.getMapObjectAt(pos.x, pos.y, EMapObjectType.BUILDING);
			mapObjectsManager.removeMapObjectType(pos.x, pos.y, EMapObjectType.BUILDING);

			FreeMapArea area = new FreeMapArea(pos, building.getBuildingType().getProtectedTiles());

			objectsGrid.setBuildingArea(area, null);

			for (ShortPoint2D curr : area) {
				short x = curr.x;
				short y = curr.y;
				if (isInBounds(x, y)) {
					flagsGrid.setBlockedAndProtected(x, y, false);
				}
			}
		}

		@Override
		public final void setBlocked(FreeMapArea area, boolean blocked) {
			for (ShortPoint2D curr : area) {
				if (MainGrid.this.isInBounds(curr.x, curr.y))
					flagsGrid.setBlockedAndProtected(curr.x, curr.y, blocked);
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
		public final NewMovable getMovable(ShortPoint2D position) {
			return movableGrid.getMovableAt(position.x, position.y);
		}

		@Override
		public final MapObjectsManager getMapObjectsManager() {
			return mapObjectsManager;
		}

		@Override
		public final INewMovableGrid getMovableGrid() {
			return movablePathfinderGrid;
		}

		@Override
		public final void requestDiggers(IDiggerRequester requester, byte amount) {
			partitionsGrid.getPartitionAt(requester).requestDiggers(requester, amount);
		}

		@Override
		public final void requestBricklayer(Building building, ShortPoint2D bricklayerTargetPos, EDirection direction) {
			partitionsGrid.getPartitionAt(building).requestBricklayer(building, bricklayerTargetPos, direction);
		}

		@Override
		public final IRequestsStackGrid getRequestStackGrid() {
			return requestStackGrid;
		}

		@Override
		public final void requestBuildingWorker(EMovableType workerType, WorkerBuilding workerBuilding) {
			partitionsGrid.getPartitionAt(workerBuilding).requestBuildingWorker(workerType, workerBuilding);
		}

		@Override
		public final void requestSoilderable(IBarrack barrack) {
			partitionsGrid.getPartitionAt(barrack).requestSoilderable(barrack);
		}

		@Override
		public final DijkstraAlgorithm getDijkstra() {
			return movablePathfinderGrid.dijkstra;
		}

		private class RequestStackGrid implements IRequestsStackGrid, Serializable {
			private static final long serialVersionUID = 1278397366408051067L;

			@Override
			public final void request(IMaterialRequester requester, EMaterialType materialType, byte priority) {
				partitionsGrid.getPartitionAt(requester).request(requester, materialType, priority);
			}

			@Override
			public final boolean hasMaterial(ShortPoint2D position, EMaterialType materialType) {
				return mapObjectsManager.canPop(position.x, position.y, materialType);
			}

			@Override
			public final void popMaterial(ShortPoint2D position, EMaterialType materialType) {
				mapObjectsManager.popMaterial(position.x, position.y, materialType);
			}

			@Override
			public final byte getStackSize(ShortPoint2D position, EMaterialType materialType) {
				return mapObjectsManager.getStackSize(position.x, position.y, materialType);
			}

			@Override
			public final void releaseRequestsAt(ShortPoint2D position, EMaterialType materialType) {
				partitionsGrid.getPartitionAt(position.x, position.y).releaseRequestsAt(position, materialType);

				byte stackSize = mapObjectsManager.getStackSize(position.x, position.y, materialType);
				for (byte i = 0; i < stackSize; i++) {
					partitionsGrid.getPartitionAt(position.x, position.y).addOffer(position, materialType);
				}
			}
		}

		@Override
		public void occupyAreaByTower(Player player, MapCircle influencingArea) {
			partitionsGrid.addTowerAndOccupyArea(player.playerId, influencingArea);
			bordersThread.checkPositions(influencingArea);
			landmarksCorrection.addLandmarkedPositions(influencingArea);
		}

		@Override
		public void freeAreaOccupiedByTower(ShortPoint2D towerPosition) {
			Iterable<ShortPoint2D> changedPositions = partitionsGrid.removeTowerAndFreeOccupiedArea(towerPosition);
			bordersThread.checkPositions(changedPositions);
			landmarksCorrection.addLandmarkedPositions(changedPositions);
		}

		@Override
		public void changePlayerOfTower(ShortPoint2D towerPosition, Player newPlayer, FreeMapArea groundArea) {
			Iterable<ShortPoint2D> changedPositions = partitionsGrid.changePlayerOfTower(towerPosition, newPlayer.playerId, groundArea);
			bordersThread.checkPositions(changedPositions);
			landmarksCorrection.addLandmarkedPositions(changedPositions);
		}

	}

	final class GUIInputGrid implements IGuiInputGrid {
		@Override
		public final NewMovable getMovable(short x, short y) {
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
		public final boolean isInBounds(ShortPoint2D position) {
			return MainGrid.this.isInBounds(position.x, position.y);
		}

		@Override
		public final void resetDebugColors() {
			landscapeGrid.resetDebugColors();
		}

		@Override
		public final ShortPoint2D getConstructablePositionAround(ShortPoint2D pos, EBuildingType type) {
			Player player = partitionsGrid.getPlayerAt(pos.x, pos.y);
			if (constructionMarksGrid.canConstructAt(pos.x, pos.y, type, player)) {
				return pos;
			} else {
				for (ShortPoint2D neighbour : new MapNeighboursArea(pos)) {
					if (constructionMarksGrid.canConstructAt(neighbour.x, neighbour.y, type, player)) {
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
				Thread.sleep(100); // FIXME @Andreas serializer should wait until
									// threads did their work!
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			MapList list = MapList.getDefaultList();
			// TODO @Andreas Eberle: pass on ui state.
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

		@Override
		public void constructBuildingAt(ShortPoint2D position, EBuildingType type) {
			MainGrid.this.constructBuildingAt(position, type, partitionsGrid.getPlayerAt(position.x, position.y), false);
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
			landmarksCorrection.addLandmarkedPosition(new ShortPoint2D(x, y));
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
			return NewMovable.getAllMovables();
		}

		@Override
		public final ConcurrentLinkedQueue<? extends IViewDistancable> getBuildingViewDistancables() {
			return Building.getAllBuildings();
		}
	}

}
