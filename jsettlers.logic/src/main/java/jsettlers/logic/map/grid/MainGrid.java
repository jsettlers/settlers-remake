/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package jsettlers.logic.map.grid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import java8.util.Optional;
import jsettlers.algorithms.borders.BordersThread;
import jsettlers.algorithms.borders.IBordersThreadGrid;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.algorithms.fogofwar.FogOfWar;
import jsettlers.algorithms.fogofwar.IFogOfWarGrid;
import jsettlers.algorithms.fogofwar.IViewDistancable;
import jsettlers.algorithms.landmarks.EnclosedBlockedAreaFinderAlgorithm;
import jsettlers.algorithms.landmarks.IEnclosedBlockedAreaFinderGrid;
import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.algorithms.path.Path;
import jsettlers.algorithms.path.area.IInAreaFinderMap;
import jsettlers.algorithms.path.area.InAreaFinder;
import jsettlers.algorithms.path.astar.AbstractAStar;
import jsettlers.algorithms.path.astar.BucketQueueAStar;
import jsettlers.algorithms.path.astar.IAStarPathMap;
import jsettlers.algorithms.path.dijkstra.DijkstraAlgorithm;
import jsettlers.algorithms.path.dijkstra.IDijkstraPathMap;
import jsettlers.algorithms.previewimage.PreviewImageCreator;
import jsettlers.algorithms.traversing.area.IAreaVisitor;
import jsettlers.common.Color;
import jsettlers.common.buildings.BuildingAreaBitSet;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.EDebugColorModes;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.IDirectGridProvider;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapLine;
import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.menu.UIState;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.player.IPlayer;
import jsettlers.common.position.MutablePoint2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.collections.IPredicate;
import jsettlers.common.utils.coordinates.CoordinateStream;
import jsettlers.input.IGuiInputGrid;
import jsettlers.input.PlayerState;
import jsettlers.logic.DockPosition;
import jsettlers.logic.FerryEntrance;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.buildings.military.occupying.IOccupyableBuilding;
import jsettlers.logic.buildings.military.occupying.OccupyingBuilding;
import jsettlers.logic.buildings.stack.IRequestsStackGrid;
import jsettlers.logic.buildings.stack.multi.StockSettings;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.grid.flags.FlagsGrid;
import jsettlers.logic.map.grid.landscape.LandscapeGrid;
import jsettlers.logic.map.grid.movable.MovableGrid;
import jsettlers.logic.map.grid.objects.AbstractHexMapObject;
import jsettlers.logic.map.grid.objects.IMapObjectsManagerGrid;
import jsettlers.logic.map.grid.objects.MapObjectsManager;
import jsettlers.logic.map.grid.objects.ObjectsGrid;
import jsettlers.logic.map.grid.partition.IPlayerChangedListener;
import jsettlers.logic.map.grid.partition.PartitionsGrid;
import jsettlers.logic.map.grid.partition.manager.PartitionManager;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IDiggerRequester;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IOfferEmptiedListener;
import jsettlers.logic.map.grid.partition.manager.materials.offers.EOfferPriority;
import jsettlers.logic.map.grid.partition.manager.materials.requests.MaterialRequestObject;
import jsettlers.logic.map.grid.partition.manager.settings.MaterialProductionSettings;
import jsettlers.logic.map.loading.data.IMapData;
import jsettlers.logic.map.loading.data.objects.BuildingMapDataObject;
import jsettlers.logic.map.loading.data.objects.IPlayerIdProvider;
import jsettlers.logic.map.loading.data.objects.MapDataObject;
import jsettlers.logic.map.loading.data.objects.MapTreeObject;
import jsettlers.logic.map.loading.data.objects.MovableObject;
import jsettlers.logic.map.loading.data.objects.StackMapDataObject;
import jsettlers.logic.map.loading.data.objects.StoneMapDataObject;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.logic.map.loading.newmap.MapFileHeader.MapType;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.interfaces.AbstractMovableGrid;
import jsettlers.logic.movable.interfaces.IAttackable;
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.logic.objects.arrow.ArrowObject;
import jsettlers.logic.objects.stack.StackMapObject;
import jsettlers.logic.player.Player;
import jsettlers.logic.player.PlayerSetting;

/**
 * This is the main grid offering an interface for interacting with the grid.
 *
 * @author Andreas Eberle
 */
public final class MainGrid implements Serializable {
	private static final long serialVersionUID = 3824511313693431423L;

	final String mapId;
	final String mapName;

	final short width;
	final short height;

	final LandscapeGrid  landscapeGrid;
	final ObjectsGrid    objectsGrid;
	final PartitionsGrid partitionsGrid;
	final MovableGrid    movableGrid;
	final FlagsGrid      flagsGrid;

	final MovablePathfinderGrid movablePathfinderGrid;
	final MapObjectsManager     mapObjectsManager;
	final BuildingsGrid         buildingsGrid;

	transient         FogOfWar                       fogOfWar;
	transient         GraphicsGrid                   graphicsGrid;
	transient         ConstructionMarksGrid          constructionMarksGrid;
	transient         BordersThread                  bordersThread;
	transient         IGuiInputGrid                  guiInputGrid;
	private transient IEnclosedBlockedAreaFinderGrid enclosedBlockedAreaFinderGrid;

	public MainGrid(String mapId, String mapName, short width, short height, PlayerSetting[] playerSettings) {
		this.mapId = mapId;
		this.mapName = mapName;

		this.width = width;
		this.height = height;

		this.flagsGrid = new FlagsGrid(width, height);
		this.movablePathfinderGrid = new MovablePathfinderGrid();
		this.mapObjectsManager = new MapObjectsManager(new MapObjectsManagerGrid());

		this.objectsGrid = new ObjectsGrid(width, height);
		this.landscapeGrid = new LandscapeGrid(width, height, flagsGrid);
		this.movableGrid = new MovableGrid(width, height, landscapeGrid);

		this.partitionsGrid = new PartitionsGrid(width, height, playerSettings, landscapeGrid);
		this.buildingsGrid = new BuildingsGrid();

		initAdditional();
	}

	public boolean isWinePlantable(ShortPoint2D point) {
		return movablePathfinderGrid.pathfinderGrid.isWinePlantable(point.x, point.y);
	}

	public boolean isCornPlantable(ShortPoint2D point) {
		return movablePathfinderGrid.pathfinderGrid.isCornPlantable(point.x, point.y);
	}

	private void initAdditional() {
		this.graphicsGrid = new GraphicsGrid();
		this.constructionMarksGrid = new ConstructionMarksGrid();
		this.bordersThread = new BordersThread(new BordersThreadGrid());
		this.guiInputGrid = new GuiInputGrid();

		this.partitionsGrid.setPlayerChangedListener(new PlayerChangedListener());
		this.enclosedBlockedAreaFinderGrid = new EnclosedBlockedAreaFinderGrid();
	}

	public final short getHeight() {
		return height;
	}

	public final short getWidth() {
		return width;
	}

	public void initForPlayer(byte playerId, FogOfWar fogOfWar) {
		if (fogOfWar != null) {
			this.fogOfWar = fogOfWar;
		} else {
			this.fogOfWar = new FogOfWar(width, height, partitionsGrid.getPlayer(playerId));
		}
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		initAdditional();
		this.bordersThread.checkArea(0, 0, width, height);
	}

	public void startThreads() {
		bordersThread.start();
		if (fogOfWar != null) {
			fogOfWar.start(new FogOfWarGrid());
		}
	}

	public void stopThreads() {
		bordersThread.cancel();
		if (fogOfWar != null) {
			fogOfWar.cancel();
		}
	}

	public MainGrid(String mapId, String mapName, IMapData mapGrid, PlayerSetting[] playerSettings) {
		this(mapId, mapName, (short) mapGrid.getWidth(), (short) mapGrid.getHeight(), playerSettings);

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
				MapDataObject object = mapGrid.getMapObject(x, y);
				if (object != null && isOccupyableBuilding(object) && isActivePlayer(object, playerSettings)) {
					addMapObject(x, y, object);
				}
				if ((x + y / 2) % 4 == 0 && y % 4 == 0 && isSurroundedByWater(x, y)) {
					mapObjectsManager.addWaves(x, y);
					if (landscapeGrid.getResourceAmountAt(x, y) > 50) {
						mapObjectsManager.addFish(x, y);
					}
				}
			}
		}

		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				MapDataObject object = mapGrid.getMapObject(x, y);
				if (object != null && !isOccupyableBuilding(object) && isActivePlayer(object, playerSettings)) {
					try {
						addMapObject(x, y, object);
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			}
		}
		System.out.println("grid filled");
	}

	private boolean isActivePlayer(MapDataObject object, PlayerSetting[] playerSettings) {
		return !(object instanceof IPlayerIdProvider) || playerSettings[((IPlayerIdProvider) object).getPlayerId()].isAvailable();
	}

	private static boolean isOccupyableBuilding(MapDataObject object) {
		return object instanceof BuildingMapDataObject && ((BuildingMapDataObject) object).getType().isMilitaryBuilding();
	}

	private boolean isSurroundedByWater(int x, int y) {
		for (EDirection direction : EDirection.VALUES) {
			if (!isWaterSafe(direction.getNextTileX(x), direction.getNextTileY(y))) {
				return false;
			}
		}
		return true;
	}

	private boolean isWaterSafe(int x, int y) {
		return isInBounds(x, y) && landscapeGrid.getLandscapeTypeAt(x, y).isWater();
	}

	private void addMapObject(int x, int y, MapDataObject object) {
		ShortPoint2D pos = new ShortPoint2D(x, y);

		if (object instanceof MapTreeObject) {
			if (isInBounds(x, y) && movablePathfinderGrid.pathfinderGrid.isTreePlantable(x, y)) {
				mapObjectsManager.plantAdultTree(pos);
			}
		} else if (object instanceof StoneMapDataObject) {
			mapObjectsManager.addStone(pos, ((StoneMapDataObject) object).getCapacity());
		} else if (object instanceof StackMapDataObject) {
			placeStack(pos, ((StackMapDataObject) object).getType(), ((StackMapDataObject) object).getCount());
		} else if (object instanceof BuildingMapDataObject) {
			BuildingMapDataObject buildingObject = (BuildingMapDataObject) object;
			Building building = constructBuildingAt(pos, buildingObject.getType(), partitionsGrid.getPlayer(buildingObject.getPlayerId()), true);

			if (building instanceof IOccupyableBuilding) {
				IOccupyableBuilding occupyableBuilding = (IOccupyableBuilding) building;
				ILogicMovable soldier = createNewMovableAt(building.getDoor(), EMovableType.SWORDSMAN_L1, building.getPlayer());
				occupyableBuilding.requestSoldier(soldier);
			}
		} else if (object instanceof MovableObject) {
			MovableObject movableObject = (MovableObject) object;
			createNewMovableAt(pos, movableObject.getType(), partitionsGrid.getPlayer(movableObject.getPlayerId()));
		}
	}

	private void placeStack(ShortPoint2D pos, EMaterialType materialType, int count) {
		for (int i = 0; i < count; i++) {
			movablePathfinderGrid.dropMaterial(pos, materialType, true, false);
		}
	}

	public void save(Byte playerId, UIState uiState) throws IOException {
		boolean savedPausingState = MatchConstants.clock().isPausing();
		MatchConstants.clock().setPausing(true);
		try {
			Thread.sleep(300L); // FIXME @Andreas serializer should wait until threads did their work!
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		PlayerState[] playerStates = calculatePlayerStates(playerId, uiState);
		MapFileHeader header = generateSaveHeader(playerId);
		MapList list = MapList.getDefaultList();
		list.saveMap(playerStates, header, MainGrid.this);

		MatchConstants.clock().setPausing(savedPausingState);
	}

	private PlayerState[] calculatePlayerStates(Byte playerId, UIState uiState) {
		byte numberOfPlayers = partitionsGrid.getNumberOfPlayers();
		PlayerState[] playerStates = new PlayerState[numberOfPlayers];
		for (byte currPlayerId = 0; currPlayerId < numberOfPlayers; currPlayerId++) {
			// find a tower of the player
			UIState currUIState = calculateUiStateByTower(currPlayerId);
			playerStates[currPlayerId] = new PlayerState(currPlayerId, currUIState);
		}
		if (playerId != null) {
			if (uiState == null) {
				uiState = calculateUiStateByTower(playerId);
			}
			playerStates[playerId] = new PlayerState(playerId, uiState, fogOfWar);
		}
		return playerStates;
	}

	private UIState calculateUiStateByTower(byte currPlayerId) {
		for (Building building : Building.getAllBuildings()) {
			if (building.getPlayer().playerId == currPlayerId && building instanceof OccupyingBuilding) {
				return new UIState(((OccupyingBuilding) building).getPosition());
			}
		}
		return null;
	}

	public MapFileHeader generateSaveHeader(Byte playerId) {
		// TODO: description
		PreviewImageCreator previewImageCreator = new PreviewImageCreator(width, height, MapFileHeader.PREVIEW_IMAGE_SIZE,
			landscapeGrid.getPreviewImageDataSupplier()
		);

		short[] bgImage = previewImageCreator.getPreviewImage();

		Player[] players = partitionsGrid.getPlayers();
		PlayerSetting[] playerConfigurations = new PlayerSetting[players.length];
		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			if (player != null) {
				playerConfigurations[i] = new PlayerSetting(player.getPlayerType(), player.getCivilisation(), player.getTeamId());
			} else {
				playerConfigurations[i] = new PlayerSetting();
			}
		}

		return new MapFileHeader(
			MapType.SAVED_SINGLE,
			mapName,
			mapId,
			"TODO: description",
			width,
			height,
			(short) 1,
			playerConfigurations,
			new Date(),
			bgImage,
			playerId
		);
	}

	public ConstructionMarksGrid getConstructionMarksGrid() {
		return constructionMarksGrid;
	}

	public LandscapeGrid getLandscapeGrid() {
		return landscapeGrid;
	}

	public ObjectsGrid getObjectsGrid() {
		return objectsGrid;
	}

	public PartitionsGrid getPartitionsGrid() {
		return partitionsGrid;
	}

	public IGraphicsGrid getGraphicsGrid() {
		return graphicsGrid;
	}

	public IGuiInputGrid getGuiInputGrid() {
		return guiInputGrid;
	}

	public MovableGrid getMovableGrid() {
		return movableGrid;
	}

	public final boolean isInBounds(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	final ILogicMovable createNewMovableAt(ShortPoint2D pos, EMovableType type, Player player) {
		return new Movable(movablePathfinderGrid, type, pos, player);
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
		Building building = Building.createBuilding(type, player, position, buildingsGrid);
		building.construct(fullyConstructed);

		if (fullyConstructed) {
			byte buildingHeight = landscapeGrid.getHeightAt(position.x, position.y);
			for (RelativePoint curr : building.getFlattenTiles()) {
				landscapeGrid.flattenAndChangeHeightTowards(curr.getDx() + position.x, curr.getDy() + position.y, buildingHeight);
			}
		}

		return building;
	}

	final void setLandscapeTypeAt(int x, int y, ELandscapeType newType) {
		if (newType.isBlocking) {
			flagsGrid.setBlockedAndProtected(x, y, true);
		} else {
			if (landscapeGrid.getLandscapeTypeAt(x, y).isBlocking) {
				flagsGrid.setBlockedAndProtected(x, y, false);
			}
		}
		landscapeGrid.setLandscapeTypeAt(x, y, newType);
	}

	final void checkPositionThatChangedPlayer(int x, int y) {
		if (!isInBounds(x, y)) {
			return;
		}

		EnclosedBlockedAreaFinderAlgorithm.checkLandmark(enclosedBlockedAreaFinderGrid, x, y);

		ILogicMovable movable = movableGrid.getMovableAt(x, y);
		if (movable != null) {
			movable.checkPlayerOfPosition(partitionsGrid.getPlayerAt(x, y));
		}
	}

	final boolean isValidPosition(IPathCalculatable pathCalculatable, int x, int y) {
		if (pathCalculatable.isShip()) {
			return isNavigable(x, y);
		}
		return isInBounds(x, y) && !flagsGrid.isBlocked(x, y)
			&& (!pathCalculatable.needsPlayersGround() || pathCalculatable.getPlayer().getPlayerId() == partitionsGrid.getPlayerIdAt(x, y));
	}

	final boolean isNavigable(int x, int y) {
		Optional<ShortPoint2D> blockingOptional = HexGridArea.stream(x, y, 0, 2)
															 .filterBounds(width, height)
															 .filter((x1, y1) -> !landscapeGrid.getLandscapeTypeAt(x1, y1).isWater || objectsGrid.getMapObjectAt(x1, y1, EMapObjectType.DOCK) != null)
															 .getFirst();
		return !blockingOptional.isPresent();
	}

	public FlagsGrid getFlagsGrid() {
		return flagsGrid;
	}

	public void initWithPlayerSettings(PlayerSetting[] playerSettings) {
		partitionsGrid.initWithPlayerSettings(playerSettings);
	}

	final class PathfinderGrid implements IAStarPathMap, IDijkstraPathMap, IInAreaFinderMap, Serializable {
		private static final long serialVersionUID = -2775530442375843213L;

		@Override
		public boolean isBlocked(IPathCalculatable requester, int x, int y) {
			if (requester.isShip()) {
				return !isWaterSafe(x, y);
			}
			return flagsGrid.isBlocked(x, y) || (requester.needsPlayersGround() && requester.getPlayer().getPlayerId() != partitionsGrid.getPlayerIdAt(x, y));
		}

		@Override
		public final float getCost(int sx, int sy, int tx, int ty) {
			return 1;
		}

		@Override
		public final void markAsOpen(int x, int y) {
			landscapeGrid.setDebugColor(x, y, Color.BLUE.getARGB());
		}

		@Override
		public final void markAsClosed(int x, int y) {
			landscapeGrid.setDebugColor(x, y, Color.RED.getARGB());
		}

		@Override
		public final void setDijkstraSearched(int x, int y) {
			markAsOpen(x, y);
		}

		@Override
		public final boolean fitsSearchType(int x, int y, ESearchType searchType, IPathCalculatable pathCalculable) {
			switch (searchType) {

				case UNENFORCED_FOREIGN_GROUND:
					return !objectsGrid.isBuildingAt(x, y) && !hasSameTeam(x, y, pathCalculable) && !partitionsGrid.isEnforcedByTower(x, y);

				case VALID_FREE_POSITION:
					return isValidPosition(pathCalculable, x, y) && movableGrid.hasNoMovableAt(x, y);

				case PLANTABLE_TREE:
					return y < height - 1 && isTreePlantable(x, y + 1) && !hasProtectedNeighbor(x, y + 1)
						&& hasSamePlayer(x, y + 1, pathCalculable) && !isMarked(x, y);
				case CUTTABLE_TREE:
					return isInBounds(x - 1, y - 1)
						&& isMapObjectCuttable(x - 1, y - 1, EMapObjectType.TREE_ADULT)
						&& hasSamePlayer(x - 1, y - 1, pathCalculable) && !isMarked(x, y);

				case PLANTABLE_CORN:
					return !isMarked(x, y) && hasSamePlayer(x, y, pathCalculable) && isCornPlantable(x, y);
				case CUTTABLE_CORN:
					return isMapObjectCuttable(x, y, EMapObjectType.CORN_ADULT) && hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y);

				case PLANTABLE_WINE:
					return !isMarked(x, y) && hasSamePlayer(x, y, pathCalculable) && isWinePlantable(x, y);
				case HARVESTABLE_WINE:
					return isMapObjectCuttable(x, y, EMapObjectType.WINE_HARVESTABLE) && hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y);

				case CUTTABLE_STONE:
					return y + 1 < height && x - 1 > 0 && isMapObjectCuttable(x - 1, y + 1, EMapObjectType.STONE)
						&& hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y);

				case RIVER:
					return isRiver(x, y) && hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y);

				case FISHABLE:
					return hasSamePlayer(x, y, pathCalculable) && hasNeighbourLandscape(x, y, ELandscapeType.WATER1);

				case NON_BLOCKED_OR_PROTECTED:
					return !(flagsGrid.isProtected(x, y) || flagsGrid.isBlocked(x, y))
						&& (!pathCalculable.needsPlayersGround() || hasSamePlayer(x, y, pathCalculable)) && movableGrid.getMovableAt(x, y) == null;

				case SOLDIER_BOWMAN:
				case SOLDIER_SWORDSMAN:
				case SOLDIER_PIKEMAN:
				case SOLDIER_INFANTRY:
					return isSoldierAt(x, y, searchType, pathCalculable.getPlayer());

				case RESOURCE_SIGNABLE:
					return isInBounds(x, y) && !flagsGrid.isProtected(x, y) && !flagsGrid.isMarked(x, y) && canAddResourceSign(x, y);

				case FOREIGN_MATERIAL:
					return isInBounds(x, y) && !hasSamePlayer(x, y, pathCalculable) && mapObjectsManager.hasStealableMaterial(x, y);

				case ENEMY: {
					IMovable movable = movableGrid.getMovableAt(x, y);
					return movable != null && movable.getPlayer().getTeamId() != pathCalculable.getPlayer().getTeamId();
				}

				default:
					System.err.println("ERROR: Can't handle search type in fitsSearchType(): " + searchType);
					return false;
			}
		}

		@Override
		public boolean fitsSearchType(int x, int y, Set<ESearchType> types, IPathCalculatable requester) {
			for (ESearchType searchType : types) {
				if (fitsSearchType(x, y, searchType, requester)) {
					return true;
				}
			}
			return false;
		}

		final boolean canAddResourceSign(int x, int y) {
			return x % 2 == 0
				&& y % 2 == 0
				&& landscapeGrid.getLandscapeTypeAt(x, y) == ELandscapeType.MOUNTAIN
				&& !objectsGrid.hasMapObjectType(x, y,
				EMapObjectType.FOUND_COAL,
				EMapObjectType.FOUND_IRON,
				EMapObjectType.FOUND_GOLD,
				EMapObjectType.FOUND_NOTHING,
				EMapObjectType.FOUND_GEMSTONE,
				EMapObjectType.FOUND_BRIMSTONE
			);
		}

		private boolean isSoldierAt(int x, int y, ESearchType searchType, IPlayer player) {
			ILogicMovable movable = movableGrid.getMovableAt(x, y);
			if (movable == null) {
				return false;
			} else {
				if (movable.getPlayer() == player && movable.canOccupyBuilding()) {
					EMovableType movableType = movable.getMovableType();

					switch (searchType) {
						case SOLDIER_BOWMAN:
							return movableType.isBowman();
						case SOLDIER_SWORDSMAN:
							return movableType.isSwordsman();
						case SOLDIER_PIKEMAN:
							return movableType.isPikeman();
						case SOLDIER_INFANTRY:
							return movableType.isInfantry();
						default:
							return false;
					}
				} else {
					return false;
				}
			}
		}

		private boolean isMarked(int x, int y) {
			return flagsGrid.isMarked(x, y);
		}

		private boolean hasProtectedNeighbor(int x, int y) {
			for (EDirection currDir : EDirection.VALUES) {
				if (flagsGrid.isProtected(currDir.getNextTileX(x), currDir.getNextTileY(y))) { return true; }
			}
			return false;
		}

		private boolean hasNeighbourLandscape(int x, int y, ELandscapeType landscape) {
			for (ShortPoint2D pos : new MapNeighboursArea(new ShortPoint2D(x, y))) {
				if (isInBounds(pos.x, pos.y) && landscapeGrid.getLandscapeTypeAt(pos.x, pos.y) == landscape) {
					return true;
				}
			}
			return false;
		}

		private boolean hasSamePlayer(int x, int y, IPathCalculatable requester) {
			return partitionsGrid.getPlayerIdAt(x, y) == requester.getPlayer().getPlayerId();
		}

		private boolean hasSameTeam(int x, int y, IPathCalculatable requester) {
			Player player = partitionsGrid.getPlayerAt(x, y);
			return player != null && player.getTeamId() == requester.getPlayer().getTeamId();
		}

		private boolean isRiver(int x, int y) {
			ELandscapeType type = landscapeGrid.getLandscapeTypeAt(x, y);
			return type == ELandscapeType.RIVER1 || type == ELandscapeType.RIVER2 || type == ELandscapeType.RIVER3 || type == ELandscapeType.RIVER4;
		}

		final boolean isTreePlantable(int x, int y) {
			return landscapeGrid.getLandscapeTypeAt(x, y).isGrass() && !flagsGrid.isProtected(x, y) && !hasBlockedNeighbor((short) x, (short) y);
		}

		private boolean hasBlockedNeighbor(short x, short y) {
			return !MapNeighboursArea.stream(x, y).iterate((currX, currY) -> isInBounds(currX, currY) && !flagsGrid.isBlocked(currX, currY));
		}

		private boolean isCornPlantable(int x, int y) {
			return !flagsGrid.isProtected(x, y)
				&& !hasProtectedNeighbor(x, y)
				&& !objectsGrid.hasMapObjectType(x, y, EMapObjectType.CORN_GROWING, EMapObjectType.CORN_ADULT)
				&& !objectsGrid.hasNeighborObjectType(x, y, EMapObjectType.CORN_ADULT, EMapObjectType.CORN_GROWING)
				&& landscapeGrid.isHexAreaOfType(x, y, 0, 2, ELandscapeType.GRASS, ELandscapeType.EARTH);
		}

		private boolean isMapObjectCuttable(int x, int y, EMapObjectType type) {
			return objectsGrid.hasCuttableObject(x, y, type);
		}

		private boolean isWinePlantable(int x, int y) {
			if (!flagsGrid.isProtected(x, y)
				&& !objectsGrid.hasMapObjectType(x, y, EMapObjectType.WINE_GROWING, EMapObjectType.WINE_HARVESTABLE, EMapObjectType.WINE_DEAD)
				&& landscapeGrid.isHexAreaOfType(x, y, 0, 1, ELandscapeType.GRASS, ELandscapeType.EARTH)) {

				EDirection direction = getDirectionOfMaximumHeightDifference(x, y, 2);
				if (direction != null) { // if minimum height difference has been found
					ShortPoint2D inDirPos = direction.getNextHexPoint(x, y);
					ShortPoint2D invDirPos = direction.getInverseDirection().getNextHexPoint(x, y);

					return !objectsGrid.hasMapObjectType(inDirPos.x, inDirPos.y, EMapObjectType.WINE_GROWING, EMapObjectType.WINE_HARVESTABLE, EMapObjectType.WINE_DEAD)
						&& !objectsGrid.hasMapObjectType(invDirPos.x, invDirPos.y, EMapObjectType.WINE_GROWING, EMapObjectType.WINE_HARVESTABLE, EMapObjectType.WINE_DEAD);
				}
			}
			return false;
		}

		private EDirection getDirectionOfMaximumHeightDifference(int x, int y, int minimumHeightDifference) {
			byte height = landscapeGrid.getHeightAt(x, y);
			for (ShortPoint2D pos : new MapNeighboursArea((short) x, (short) y)) {
				if (Math.abs(height - landscapeGrid.getHeightAt(pos.x, pos.y)) >= minimumHeightDifference) {
					return EDirection.getDirection((short) x, (short) y, pos.x, pos.y);
				}
			}
			return null;
		}

		@Override
		public void setDebugColor(int x, int y, Color color) {
			landscapeGrid.setDebugColor(x, y, color.getARGB());
		}

		@Override
		public short getBlockedPartition(int x, int y) {
			return landscapeGrid.getBlockedPartitionAt(x, y);
		}

	}

	final class GraphicsGrid implements IGraphicsGrid, IDirectGridProvider {
		private transient BitSet bordersGrid = new BitSet(width * height);

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
		public IMovable[] getMovableArray() {
			return movableGrid.getMovableArray();
		}

		@Override
		public final IMapObject getMapObjectsAt(int x, int y) {
			return objectsGrid.getObjectsAt(x, y);
		}

		public final IMapObject[] getObjectArray() {
			return objectsGrid.getObjectArray();
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
		public final int getDebugColorAt(int x, int y, EDebugColorModes debugColorMode) {
			if (!MatchConstants.ENABLE_DEBUG_COLORS) {
				return 0;
			}

			switch (debugColorMode) {
				case BLOCKED_PARTITIONS:
					return getScaledColor(landscapeGrid.getBlockedPartitionAt(x, y) + 1);
				case PARTITION_ID:
					return getScaledColor(partitionsGrid.getPartitionIdAt(x, y));
				case REAL_PARTITION_ID:
					return getScaledColor(partitionsGrid.getRealPartitionIdAt(x, y));
				case PLAYER_ID:
					return getScaledColor(partitionsGrid.getPlayerIdAt(x, y) + 1);
				case TOWER_COUNT:
					return getScaledColor(partitionsGrid.getTowerCountAt(x, y) + 1);
				case DEBUG_COLOR:
					return landscapeGrid.getDebugColor(x, y);
				case MARKS_AND_OBJECTS:
					return flagsGrid.isMarked(x, y) ? Color.ORANGE.getARGB()
						: (objectsGrid.getMapObjectAt(x, y, EMapObjectType.INFORMABLE_MAP_OBJECT) != null ? Color.GREEN.getARGB()
						: (objectsGrid
						.getMapObjectAt(x, y, EMapObjectType.ATTACKABLE_TOWER) != null ? Color.RED.getARGB()
						: (flagsGrid.isBlocked(x, y) ? Color.BLACK.getARGB()
						: (flagsGrid.isProtected(x, y) ? Color.BLUE.getARGB() : 0))));
				case RESOURCE_AMOUNTS:
					float resource = ((float) landscapeGrid.getResourceAmountAt(x, y)) / Byte.MAX_VALUE;
					return Color.getARGB(1, .6f, 0, resource);
				case NONE:
				default:
					return 0;
			}
		}

		private int getScaledColor(int value) {
			final int SCALE = 4;
			return Color.getABGR(((float) (value % SCALE)) / SCALE, ((float) ((value / SCALE) % SCALE)) / SCALE,
				((float) ((value / SCALE / SCALE) % SCALE)) / SCALE, 1
			);
		}

		@Override
		public final boolean isBorder(int x, int y) {
			return bordersGrid.get(x + y * width);
		}

		@Override
		public BitSet getBorderArray() {
			return bordersGrid;
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
		public byte[][] getVisibleStatusArray() {
			return fogOfWar.getVisibleStatusArray();
		}

		@Override
		public final void setBackgroundListener(IGraphicsBackgroundListener backgroundListener) {
			landscapeGrid.setBackgroundListener(backgroundListener);
		}

		@Override
		public IPartitionData getPartitionData(int x, int y) {
			return partitionsGrid.getPartitionDataForManagerAt(x, y);
		}

		@Override
		public boolean isBuilding(int x, int y) {
			return flagsGrid.isBlocked(x, y) && objectsGrid.isBuildingAt(x, y);
		}
	}

	final class MapObjectsManagerGrid implements IMapObjectsManagerGrid {
		private static final long serialVersionUID = 6223899915568781576L;

		@Override
		public final void setLandscape(int x, int y, ELandscapeType landscapeType) {
			setLandscapeTypeAt(x, y, landscapeType);
		}

		@Override
		public final boolean isBlocked(int x, int y) {
			return flagsGrid.isBlocked(x, y);
		}

		@Override
		public final void setBlocked(int x, int y, boolean blocked) {
			flagsGrid.setBlockedAndProtected(x, y, blocked);
		}

		@Override
		public final boolean isProtected(int x, int y) {
			return flagsGrid.isProtected(x, y);
		}

		@Override
		public final void setProtected(int x, int y, boolean protect) {
			flagsGrid.setProtected(x, y, protect);
		}

		@Override
		public final boolean removeMapObject(int x, int y, AbstractHexMapObject mapObject) {
			return objectsGrid.removeMapObject(x, y, mapObject);
		}

		@Override
		public final AbstractHexMapObject getMapObject(int x, int y, EMapObjectType mapObjectType) {
			return objectsGrid.getMapObjectAt(x, y, mapObjectType);
		}

		@Override
		public final void addMapObject(int x, int y, AbstractHexMapObject mapObject) {
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
		public final boolean isInBounds(int x, int y) {
			return MainGrid.this.isInBounds(x, y);
		}

		@Override
		public EResourceType getResourceTypeAt(int x, int y) {
			return landscapeGrid.getResourceTypeAt(x, y);
		}

		@Override
		public byte getResourceAmountAt(int x, int y) {
			return landscapeGrid.getResourceAmountAt(x, y);
		}

		@Override
		public void hitWithArrowAt(ArrowObject arrow) {
			short x = arrow.getTargetX();
			short y = arrow.getTargetY();

			ILogicMovable movable = movableGrid.getMovableAt(x, y);
			if (movable != null) {
				movable.receiveHit(arrow.getHitStrength(), arrow.getSourcePos(), arrow.getShooterPlayerId());
				mapObjectsManager.removeMapObject(x, y, arrow);
			}
		}

		@Override
		public void spawnDonkey(ShortPoint2D position, Player player) {
			Player realPlayer = partitionsGrid.getPlayer(player.getPlayerId());
			ILogicMovable donkey = new Movable(movablePathfinderGrid, EMovableType.DONKEY, position, realPlayer);
			donkey.leavePosition();
		}

		@Override
		public boolean isBuildingAreaAt(short x, short y) {
			return objectsGrid.isBuildingAt(x, y);
		}

		@Override
		public boolean hasMapObjectType(int x, int y, EMapObjectType... mapObjectTypes) {
			return objectsGrid.hasMapObjectType(x, y, mapObjectTypes);
		}
	}

	final class EnclosedBlockedAreaFinderGrid implements IEnclosedBlockedAreaFinderGrid {
		@Override
		public final boolean isPioneerBlockedAndWithoutTowerProtection(int x, int y) {
			return MainGrid.this.isInBounds(x, y) && flagsGrid.isPioneerBlocked(x, y) && !landscapeGrid.isBlockedPartition(x, y) && !partitionsGrid.isEnforcedByTower(x, y);
		}

		@Override
		public boolean isOfPlayerOrBlocked(int x, int y, byte playerId) {
			return partitionsGrid.getPlayerIdAt(x, y) == playerId || landscapeGrid.isBlockedPartition(x, y);
		}

		@Override
		public final boolean isInBounds(int x, int y) {
			return MainGrid.this.isInBounds(x, y);
		}

		@Override
		public final byte getPlayerIdAt(int x, int y) {
			return partitionsGrid.getPlayerIdAt(x, y);
		}

		private void destroyBuildingOrTakeOver(int x, int y, byte playerId) {
			if (flagsGrid.isBlocked(x, y)) {
				partitionsGrid.changePlayerAt(x, y, playerId);
			}

			Building building = objectsGrid.getBuildingAt(x, y);
			if (building != null && building.getPlayer().getPlayerId() != playerId) {
				building.kill();
			}
		}

		@Override
		public IAreaVisitor getDestroyBuildingOrTakeOverVisitor(byte newPlayer) {
			return (x, y) -> {
				destroyBuildingOrTakeOver(x, y, newPlayer);
				return true;
			};
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

	final class ConstructionMarksGrid extends AbstractConstructionMarkableMap {
		@Override
		public final void setConstructMarking(int x, int y, boolean set, boolean binaryConstructionMarkValues, RelativePoint[] flattenPositions) {
			if (isInBounds(x, y)) {
				if (set) {
					byte newValue = binaryConstructionMarkValues ? 0 : calculateConstructionMarkValue(x, y, flattenPositions);
					mapObjectsManager.setConstructionMarking(x, y, newValue);
				} else {
					mapObjectsManager.setConstructionMarking(x, y, (byte) -1);
				}
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
		public boolean canConstructAt(int x, int y, EBuildingType buildingType, byte playerId) {
			RelativePoint[] buildingArea = buildingType.getBuildingArea();
			BuildingAreaBitSet areaBitSet = buildingType.getBuildingAreaBitSet();
			if (!isInBounds(areaBitSet.minX + x, areaBitSet.minY + y) || !isInBounds(areaBitSet.maxX + x, areaBitSet.maxY + y)) {
				return false;
			}

			short partitionId = getPartitionIdAt(areaBitSet.aPosition.calculateX(x), areaBitSet.aPosition.calculateY(y));

			if (!canPlayerConstructOnPartition(playerId, partitionId)) {
				return false;
			}
			for (RelativePoint curr : buildingArea) {
				int currX = curr.calculateX(x);
				int currY = curr.calculateY(y);

				if (!canUsePositionForConstruction(currX, currY, buildingType.getRequiredGroundTypeAt(currX, currY), partitionId)) {
					return false;
				}
			}
			return !buildingType.needsFlattenedGround() || calculateConstructionMarkValue(x, y, buildingArea) >= 0;
		}

		@Override
		public boolean canUsePositionForConstruction(int x, int y, Set<ELandscapeType> allowedGroundTypes, short partitionId) {
			return isInBounds(x, y)
				&& !flagsGrid.isProtected(x, y)
				&& partitionsGrid.getPartitionIdAt(x, y) == partitionId
				&& allowedGroundTypes.contains(landscapeGrid.getLandscapeTypeAt(x, y));
		}

		@Override
		public byte calculateConstructionMarkValue(int mapX, int mapY, final RelativePoint[] flattenPositions) {
			int sum = 0;

			for (RelativePoint currPos : flattenPositions) {
				sum += landscapeGrid.getHeightAt(currPos.calculateX(mapX), currPos.calculateY(mapY));
			}

			float avg = ((float) sum) / flattenPositions.length;
			float diff = 0;

			for (RelativePoint currPos : flattenPositions) {
				float currDiff = Math.abs(landscapeGrid.getHeightAt(currPos.calculateX(mapX), currPos.calculateY(mapY)) - avg);
				diff += currDiff;
			}

			int result = (int) (Constants.CONSTRUCTION_MARK_SCALE_FACTOR * Math.pow(diff, Constants.CONSTRUCTION_MARK_POW_FACTOR)
				/ flattenPositions.length);

			if (result <= Byte.MAX_VALUE) {
				return (byte) result;
			} else {
				return -1;
			}
		}

		@Override
		public short getPartitionIdAt(int x, int y) {
			return partitionsGrid.getPartitionIdAt(x, y);
		}

		@Override
		public boolean canPlayerConstructOnPartition(byte playerId, short partitionId) {
			return (playerId == 0 && MatchConstants.ENABLE_ALL_PLAYER_SELECTION && !partitionsGrid.isDefaultPartition(partitionId))
				|| partitionsGrid.ownsPlayerPartition(partitionId, playerId);
		}

		@Override
		public boolean isInBounds(int x, int y) {
			return MainGrid.this.isInBounds(x, y);
		}
	}

	final class MovablePathfinderGrid extends AbstractMovableGrid {
		private static final long serialVersionUID = 4006228724969442801L;

		private transient PathfinderGrid    pathfinderGrid;
		private transient AbstractAStar     aStar;
		transient         DijkstraAlgorithm dijkstra; // not private, because it's used by BuildingsGrid
		private transient InAreaFinder      inAreaFinder;

		public MovablePathfinderGrid() {
			initPathfinders();
		}

		private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
			ois.defaultReadObject();
			initPathfinders();
		}

		private void initPathfinders() {
			pathfinderGrid = new PathfinderGrid();

			aStar = new BucketQueueAStar(pathfinderGrid, width, height);
			dijkstra = new DijkstraAlgorithm(pathfinderGrid, aStar, width, height);
			inAreaFinder = new InAreaFinder(pathfinderGrid, width, height);
		}

		@Override
		public final boolean isBlocked(int x, int y) {
			return flagsGrid.isBlocked(x, y);
		}

		@Override
		public final boolean isProtected(int x, int y) {
			return flagsGrid.isProtected(x, y);
		}

		@Override
		public final boolean isBlockedOrProtected(int x, int y) {
			return isBlocked(x, y) || isProtected(x, y);
		}

		@Override
		public final boolean canTakeMaterial(ShortPoint2D position, EMaterialType material) {
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
		public final void placeSmoke(ShortPoint2D pos, boolean place) {
			if (place) {
				mapObjectsManager.addSimpleMapObject(pos, EMapObjectType.SMOKE, false, null);
			} else {
				mapObjectsManager.removeMapObjectType(pos.x, pos.y, EMapObjectType.SMOKE);
			}
		}

		@Override
		public void changePlayerAt(ShortPoint2D position, Player player) {
			partitionsGrid.changePlayerAt(position, player.playerId);
			bordersThread.checkPosition(position);

			checkPositionThatChangedPlayer(position.x, position.y);
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
			return mapObjectsManager.popMaterial(position.x, position.y, materialType);
		}

		@Override
		public boolean dropMaterial(ShortPoint2D position, EMaterialType materialType, boolean offer, boolean forced) {
			boolean successful;

			if (forced) {
				position = mapObjectsManager.pushMaterialForced(position.x, position.y, materialType);
				successful = position != null;
			} else {
				successful = mapObjectsManager.pushMaterial(position.x, position.y, materialType);
			}

			if (successful && offer) {
				partitionsGrid.getPartitionAt(position.x, position.y).addOffer(position, materialType, EOfferPriority.OFFER_TO_ALL);
			}

			return successful;
		}

		@Override
		public EDirection getDirectionOfSearched(ShortPoint2D position, ESearchType searchType) {
			IPredicate<ELandscapeType> predicate;

			if (searchType == ESearchType.FISHABLE) {
				predicate = ELandscapeType::isWater;
			} else if (searchType == ESearchType.RIVER) {
				predicate = ELandscapeType::isRiver;
			} else {
				return null;
			}

			for (EDirection direction : EDirection.VALUES) {
				int x = direction.getNextTileX(position.x);
				int y = direction.getNextTileY(position.y);

				if (isInBounds(x, y) && predicate.evaluate(landscapeGrid.getLandscapeTypeAt(x, y))) {
					return direction;
				}
			}
			return null;
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
		public boolean feedDonkeyAt(ShortPoint2D position) {
			Player player = partitionsGrid.getPlayerAt(position.x, position.y);
			return mapObjectsManager.feedDonkeyAt(position, player);
		}

		@Override
		public boolean canPushMaterial(ShortPoint2D position) {
			return mapObjectsManager.canPush(position);
		}

		@Override
		public void changeHeightTowards(int x, int y, byte targetHeight) {
			landscapeGrid.flattenAndChangeHeightTowards(x, y, targetHeight);
			objectsGrid.removeMapObjectTypes(x, y, EMapObjectType.TO_BE_REMOVED_WHEN_FLATTENED);
		}

		@Override
		public boolean hasNoMovableAt(int x, int y) {
			return movableGrid.hasNoMovableAt(x, y);
		}

		@Override
		public boolean isFreePosition(int x, int y) {
			return isInBounds(x, y) && !flagsGrid.isBlocked(x, y) && movableGrid.hasNoMovableAt(x, y);
		}

		@Override
		public void leavePosition(ShortPoint2D position, ILogicMovable movable) {
			movableGrid.movableLeft(position, movable);
		}

		@Override
		public void enterPosition(ShortPoint2D position, ILogicMovable movable, boolean informFullArea) {
			movableGrid.movableEntered(position, movable);
			notifyAttackers(position, movable, informFullArea);
		}

		public void notifyAttackers(ShortPoint2D position, ILogicMovable movable, boolean informFullArea) {
			if (movable.isAttackable()) {
				movableGrid.informMovables(movable, position.x, position.y, informFullArea);
				objectsGrid.informObjectsAboutAttackable(position, movable, informFullArea, !movable.getMovableType().isBowman());
			}
		}

		@Override
		public Path calculatePathTo(IPathCalculatable pathRequester, ShortPoint2D targetPos) {
			return aStar.findPath(pathRequester, targetPos);
		}

		@Override
		public Path searchDijkstra(IPathCalculatable pathCalculateable, short centerX, short centerY, short radius, ESearchType searchType) {
			return dijkstra.find(pathCalculateable, centerX, centerY, (short) 0, radius, searchType);
		}

		@Override
		public Path searchInArea(IPathCalculatable pathCalculateable, short centerX, short centerY, short radius, ESearchType searchType) {
			ShortPoint2D target = inAreaFinder.find(pathCalculateable, centerX, centerY, radius, searchType);
			if (target != null) {
				return calculatePathTo(pathCalculateable, target);
			} else {
				return null;
			}
		}

		@Override
		public ILogicMovable getMovableAt(int x, int y) {
			return movableGrid.getMovableAt(x, y);
		}

		@Override
		public void addSelfDeletingMapObject(ShortPoint2D position, EMapObjectType mapObjectType, float duration, Player player) {
			mapObjectsManager.addSelfDeletingMapObject(position, mapObjectType, duration, player);
		}

		@Override
		public boolean isInBounds(int x, int y) {
			return MainGrid.this.isInBounds(x, y);
		}

		@Override
		public boolean fitsSearchType(IPathCalculatable pathCalculable, int x, int y, ESearchType searchType) {
			return pathfinderGrid.fitsSearchType(x, y, searchType, pathCalculable);
		}

		@Override
		public final boolean executeSearchType(IPathCalculatable pathCalculable, ShortPoint2D position, ESearchType searchType) {
			if (fitsSearchType(pathCalculable, position.x, position.y, searchType)) {
				return mapObjectsManager.executeSearchType(position, searchType);
			} else {
				return false;
			}
		}

		@Override
		public ELandscapeType getLandscapeTypeAt(int x, int y) {
			return landscapeGrid.getLandscapeTypeAt(x, y);
		}

		@Override
		public IAttackable getEnemyInSearchArea(final ShortPoint2D position, final IAttackable searchingAttackable, final short minSearchRadius,
												final short maxSearchRadius, final boolean includeTowers) {
			boolean isBowman = searchingAttackable.getMovableType().isBowman();

			IAttackable enemy = getEnemyInSearchArea(searchingAttackable.getPlayer(), new HexGridArea(position.x, position.y, minSearchRadius,
				maxSearchRadius
			), isBowman, includeTowers);
			if (includeTowers && !isBowman && enemy == null) {
				enemy = getEnemyInSearchArea(searchingAttackable.getPlayer(), new HexGridArea(position.x, position.y, maxSearchRadius, Constants.TOWER_ATTACKABLE_SEARCH_RADIUS), false, true);
			}

			return enemy;
		}

		private IAttackable getEnemyInSearchArea(IPlayer searchingPlayer, HexGridArea area, boolean isBowman, boolean includeTowers) {
			return area.stream().filterBounds(width, height).iterateForResult((x, y) -> {
				IAttackable currAttackable = movableGrid.getMovableAt(x, y);
				if (includeTowers && !isBowman && currAttackable == null) {
					currAttackable = (IAttackable) objectsGrid.getMapObjectAt(x, y, EMapObjectType.ATTACKABLE_TOWER);
				}

				if (currAttackable != null && MovableGrid.isEnemy(searchingPlayer, currAttackable)) {
					return Optional.of(currAttackable);
				} else {
					return Optional.empty();
				}
			}).orElse(null);
		}

		@Override
		public void addArrowObject(ShortPoint2D attackedPos, ShortPoint2D shooterPos, byte shooterPlayerId, float hitStrength) {
			mapObjectsManager.addArrowObject(attackedPos, shooterPos, shooterPlayerId, hitStrength);
		}

		@Override
		public final ShortPoint2D calcDecentralizeVector(short x, short y) {
			MutablePoint2D vector = new MutablePoint2D();

			HexGridArea.stream(x, y, 1, Constants.MOVABLE_FLOCK_TO_DECENTRALIZE_MAX_RADIUS).forEach((currX, currY) -> {
				int radius = ShortPoint2D.getOnGridDist(currX - x, currY - y);

				int factor;
				if (!MainGrid.this.isInBounds(currX, currY)) {
					factor = radius == 1 ? 6 : 2;
				} else if (!movableGrid.hasNoMovableAt(currX, currY)) {
					factor = Constants.MOVABLE_FLOCK_TO_DECENTRALIZE_MAX_RADIUS - radius + 1;
				} else {
					return;
				}
				vector.x += (x - currX) * factor;
				vector.y += (y - currY) * factor;
			});

			return vector.toShortPoint2D();
		}

		@Override
		public Player getPlayerAt(ShortPoint2D position) {
			return partitionsGrid.getPlayerAt(position.x, position.y);
		}

		@Override
		public boolean isValidPosition(IPathCalculatable pathCalculatable, int x, int y) {
			return MainGrid.this.isValidPosition(pathCalculatable, x, y);
		}

		@Override
		public boolean isValidNextPathPosition(IPathCalculatable pathCalculatable, ShortPoint2D nextPos, ShortPoint2D targetPos) {
			return isValidPosition(pathCalculatable, nextPos.x, nextPos.y) && (!pathCalculatable.needsPlayersGround()
				|| partitionsGrid.getPartitionAt(pathCalculatable) == partitionsGrid.getPartitionAt(targetPos.x, targetPos.y));
		}

		@Override
		public int getWidth() {
			return width;
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public boolean isWater(int x, int y) {
			return landscapeGrid.getLandscapeTypeAt(x, y).isWater;
		}

		@Override
		public boolean tryTakingResource(ShortPoint2D position, EResourceType resource) {
			return landscapeGrid.tryTakingResource(position, resource);
		}
	}

	final class BordersThreadGrid implements IBordersThreadGrid {
		@Override
		public final byte getPlayerIdAt(int x, int y) {
			return partitionsGrid.getPlayerIdAt(x, y);
		}

		@Override
		public final void setBorderAt(int x, int y, boolean isBorder) {
			graphicsGrid.bordersGrid.set(x + y * width, isBorder);
		}

		@Override
		public final boolean isInBounds(int x, int y) {
			return MainGrid.this.isInBounds(x, y);
		}

		@Override
		public final short getBlockedPartition(int x, int y) {
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
				movablePathfinderGrid.dropMaterial(position, type, true, true);
			}
		}

		@Override
		public DockPosition findValidDockPosition(ShortPoint2D requestedPosition, ShortPoint2D buildingPosition, int maximumDistance) {
			if (!isWaterSafe(requestedPosition.x, requestedPosition.y)) {
				return null; // requested position is not in water
			}

			short buildingPartition = partitionsGrid.getPartitionIdAt(buildingPosition.x, buildingPosition.y);

			Optional<ShortPoint2D> coastPosition = HexGridArea
				.stream(requestedPosition.x, requestedPosition.y, 0, 10)
				.filterBounds(width, height)
				.filter((x, y) -> ShortPoint2D.getOnGridDist(buildingPosition.x, buildingPosition.y, x, y) <= maximumDistance)
				.filter((x, y) -> !landscapeGrid.getLandscapeTypeAt(x, y).isWater())
				.filter((x, y) -> partitionsGrid.getPartitionIdAt(x, y) == buildingPartition) // ensure the dock is the same partition (accessible by worker of building)
				.filter((x, y) -> { // check that the dock goes from land to water
					EDirection direction = EDirection.getApproxDirection(x, y, requestedPosition.x, requestedPosition.y);
					ShortPoint2D firstDockWaterPosition = direction.getNextHexPoint(x, y);
					ShortPoint2D secondDockWaterPosition = direction.getNextHexPoint(firstDockWaterPosition.x, firstDockWaterPosition.y);

					return isWaterSafe(firstDockWaterPosition.x, firstDockWaterPosition.y) && isWaterSafe(secondDockWaterPosition.x, secondDockWaterPosition.y);
				})
				.getFirst();

			if (!coastPosition.isPresent()) {
				return null;
			}

			EDirection direction = EDirection.getApproxDirection(coastPosition.get(), requestedPosition);
			return new DockPosition(coastPosition.get(), direction);
		}

		@Override
		public void setDock(DockPosition dockPosition, Player player) {
			ShortPoint2D point = dockPosition.getDirection().rotateRight(3).getNextHexPoint(dockPosition.getPosition());
			short partition = landscapeGrid.getBlockedPartitionAt(point.x, point.y);
			for (int i = 0; i < 3; i++) {
				point = dockPosition.getDirection().getNextHexPoint(dockPosition.getPosition(), i);
				mapObjectsManager.addSimpleMapObject(point, EMapObjectType.DOCK, false, player);
				flagsGrid.setBlockedAndProtected(point.x, point.y, false);
				partitionsGrid.changePlayerAt(point, player.getPlayerId());
				landscapeGrid.setBlockedPartition(point.x, point.y, partition);
			}
		}

		@Override
		public void removeDock(DockPosition dockPosition) {
			for (int i = 0; i < 3; i++) {
				ShortPoint2D point = dockPosition.getDirection().getNextHexPoint(dockPosition.getPosition(), i);
				mapObjectsManager.removeMapObjectType(point.x, point.y, EMapObjectType.DOCK);
				flagsGrid.setBlockedAndProtected(point.x, point.y, true);
			}
		}

		@Override
		public boolean isCoastReachable(ShortPoint2D position) {
			return !HexGridArea.stream(position.x, position.y, 0, 3)
							   .filterBounds(width, height)
							   .filter((x, y) -> !landscapeGrid.getLandscapeTypeAt(x, y).isWater)
							   .isEmpty();
		}

		@Override
		public final boolean setBuilding(ShortPoint2D position, Building newBuilding) {
			if (MainGrid.this.isInBounds(position.x, position.y)) {
				FreeMapArea protectedArea = new FreeMapArea(position, newBuilding.getBuildingType().getProtectedTiles());

				if (canConstructAt(protectedArea)) {
					setProtectedState(protectedArea, true);
					mapObjectsManager.addBuildingTo(position, newBuilding);
					objectsGrid.setBuildingArea(protectedArea, newBuilding);
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		private void setProtectedState(FreeMapArea area, boolean setProtected) {
			area.stream().forEach((x, y) -> flagsGrid.setProtected(x, y, setProtected));
		}

		private boolean canConstructAt(FreeMapArea area) {
			for (ShortPoint2D curr : area) {
				short x = curr.x;
				short y = curr.y;

				if (!isInBounds(x, y) || flagsGrid.isProtected(x, y) || flagsGrid.isBlocked(x, y)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public final void removeBuildingAt(ShortPoint2D pos) {
			IBuilding building = (IBuilding) objectsGrid.getMapObjectAt(pos.x, pos.y, EMapObjectType.BUILDING);
			mapObjectsManager.removeMapObjectType(pos.x, pos.y, EMapObjectType.BUILDING);

			FreeMapArea area = new FreeMapArea(pos, building.getBuildingType().getProtectedTiles());
			objectsGrid.setBuildingArea(area, null);

			area.stream().filterBounds(width, height).forEach((x, y) -> {
				StackMapObject stack = (StackMapObject) objectsGrid.getMapObjectAt(x, y, EMapObjectType.STACK_OBJECT);
				flagsGrid.setBlockedAndProtected(x, y, false, stack != null); // if there is a stack, the position must stay protected
			});
		}

		@Override
		public final void setBlocked(FreeMapArea area, boolean blocked) {
			area.stream().filterBounds(width, height).forEach((x, y) -> flagsGrid.setBlockedAndProtected(x, y, blocked));
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
		public final ILogicMovable getMovable(ShortPoint2D position) {
			return movableGrid.getMovableAt(position.x, position.y);
		}

		@Override
		public final MapObjectsManager getMapObjectsManager() {
			return mapObjectsManager;
		}

		@Override
		public final AbstractMovableGrid getMovableGrid() {
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
		public final void requestSoldierable(IBarrack barrack) {
			partitionsGrid.getPartitionAt(barrack).requestSoldierable(barrack);
		}

		@Override
		public final DijkstraAlgorithm getDijkstra() {
			return movablePathfinderGrid.dijkstra;
		}

		private class RequestStackGrid implements IRequestsStackGrid, Serializable {
			private static final long serialVersionUID = 1278397366408051067L;

			@Override
			public final void request(EMaterialType materialType, MaterialRequestObject requestObject) {
				partitionsGrid.getPartitionAt(requestObject).request(materialType, requestObject);
			}

			@Override
			public final boolean hasMaterial(ShortPoint2D position, EMaterialType materialType) {
				return mapObjectsManager.canPop(position.x, position.y, materialType);
			}

			@Override
			public final boolean popMaterial(ShortPoint2D position, EMaterialType materialType) {
				return mapObjectsManager.popMaterial(position.x, position.y, materialType);
			}

			@Override
			public StockSettings getPartitionStockSettings(ShortPoint2D position) {
				return partitionsGrid.getPartitionSettings(position).getStockSettings();
			}

			@Override
			public final byte getStackSize(ShortPoint2D position, EMaterialType materialType) {
				return mapObjectsManager.getStackSize(position.x, position.y, materialType);
			}

			@Override
			public final void createOffersForAvailableMaterials(ShortPoint2D position, EMaterialType materialType) {
				byte stackSize = mapObjectsManager.getStackSize(position.x, position.y, materialType);
				PartitionManager partition = partitionsGrid.getPartitionAt(position.x, position.y);
				for (byte i = 0; i < stackSize; i++) {
					partition.addOffer(position, materialType, EOfferPriority.OFFER_TO_ALL);
				}
			}

			@Override
			public void offer(ShortPoint2D position, EMaterialType materialType, EOfferPriority priority, IOfferEmptiedListener offerListener) {
				partitionsGrid.getPartitionAt(position.x, position.y).addOffer(position, materialType, priority, offerListener);
			}

			@Override
			public void updateOfferPriorities(ShortPoint2D position, EMaterialType materialType, EOfferPriority newPriority) {
				partitionsGrid.getPartitionAt(position.x, position.y).updateOfferPriority(position, materialType, newPriority);
			}
		}

		@Override
		public void occupyAreaByTower(Player player, MapCircle influencingArea, FreeMapArea groundArea) {
			partitionsGrid.addTowerAndOccupyArea(player.playerId, influencingArea, groundArea);
			checkAllPositionsForEnclosedBlockedAreas(influencingArea.stream()); // TODO @Andreas Eberle only test the borders of changed areas!!
		}

		@Override
		public void freeAreaOccupiedByTower(ShortPoint2D towerPosition) {
			partitionsGrid.removeTowerAndFreeOccupiedArea(towerPosition);
		}

		@Override
		public void changePlayerOfTower(ShortPoint2D towerPosition, Player newPlayer, FreeMapArea groundArea) {
			CoordinateStream positions = partitionsGrid.changePlayerOfTower(towerPosition, newPlayer.playerId);
			checkAllPositionsForEnclosedBlockedAreas(positions);
		}

		private void checkAllPositionsForEnclosedBlockedAreas(CoordinateStream area) {
			area.forEach(MainGrid.this::checkPositionThatChangedPlayer);
		}

		@Override
		public boolean isAreaFlattenedAtHeight(ShortPoint2D position, RelativePoint[] positions, byte expectedHeight) {
			return landscapeGrid.isAreaFlattenedAtHeight(position, positions, expectedHeight);
		}

		@Override
		public void drawWorkAreaCircle(ShortPoint2D buildingPosition, ShortPoint2D workAreaCenter, short radius, boolean draw) {
			short buildingPartition = partitionsGrid.getPartitionIdAt(buildingPosition.x, buildingPosition.y);

			final int numCircles = 4;

			for (int circle = 1; circle <= 4; circle++) {
				float circleRadius = radius * circle / (float) numCircles;
				float mapObjectProgress = (circle - 1) / (float) (numCircles - 1);

				MapCircle.streamBorder(workAreaCenter.x, workAreaCenter.y, circleRadius)
						 .filterBounds(width, height)
						 .forEach((x, y) -> addOrRemoveMarkObject(buildingPartition, draw, x, y, mapObjectProgress));
			}
		}

		@Override
		public void drawTradingPathLine(ShortPoint2D start, ShortPoint2D[] waypoints, boolean draw) {
			ShortPoint2D lastWaypoint = start;
			float progress = 0;
			for (ShortPoint2D currentWaypoint : waypoints) {
				if (currentWaypoint == null) {
					continue;
				}

				float fixedProgress = progress;
				MapLine.stream(lastWaypoint, currentWaypoint)
					   .filterBounds(width, height)
					   .forEach((x, y) -> {
						   if (draw) {
							   mapObjectsManager.addBuildingWorkAreaObject(x, y, fixedProgress);
						   } else {
							   mapObjectsManager.removeMapObjectType(x, y, EMapObjectType.WORKAREA_MARK);
						   }
					   });
				lastWaypoint = currentWaypoint;
				progress += 1f / (waypoints.length - 1);
			}
		}

		private void addOrRemoveMarkObject(short buildingPartition, boolean draw, int x, int y, float progress) {
			if (draw) {
				// Only place an object if the position is the same as the one of the building.
				if (partitionsGrid.getPartitionIdAt(x, y) == buildingPartition) {
					mapObjectsManager.addBuildingWorkAreaObject(x, y, progress);
				}
			} else {
				mapObjectsManager.removeMapObjectType(x, y, EMapObjectType.WORKAREA_MARK);
			}
		}

		@Override
		public short getPartitionIdAt(ShortPoint2D pos) {
			return partitionsGrid.getPartitionIdAt(pos.x, pos.y);
		}

		@Override
		public boolean tryTakingResource(ShortPoint2D position, EResourceType resource) {
			return landscapeGrid.tryTakingResource(position, resource);
		}

		@Override
		public int getAmountOfResource(EResourceType resource, Iterable<ShortPoint2D> positions) {
			return landscapeGrid.getAmountOfResource(resource, positions);
		}

		@Override
		public MaterialProductionSettings getMaterialProductionAt(int x, int y) {
			return partitionsGrid.getMaterialProductionAt(x, y);
		}

		@Override
		public ShortPoint2D getClosestReachablePosition(final ShortPoint2D start,
														ShortPoint2D target,
														final boolean needsPlayersGround,
														final boolean isShip,
														final IPlayer player,
														short targetRadius) {
			IPathCalculatable pathSearcher = new IPathCalculatable() {
				@Override
				public ShortPoint2D getPosition() {
					return start;
				}

				@Override
				public IPlayer getPlayer() {
					return player;
				}

				@Override
				public boolean needsPlayersGround() {
					return needsPlayersGround;
				}

				@Override
				public boolean isShip() {
					return isShip;
				}
			};
			Path path = movablePathfinderGrid.searchDijkstra(pathSearcher, target.x, target.y, targetRadius, ESearchType.VALID_FREE_POSITION);

			return path != null ? path.getTargetPosition() : null;
		}
	}

	final class GuiInputGrid implements IGuiInputGrid {
		@Override
		public final ILogicMovable getMovable(int x, int y) {
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
		public final IBuilding getBuildingAt(int x, int y) {
			return objectsGrid.getBuildingAt(x, y);
		}

		@Override
		public final boolean isInBounds(ShortPoint2D position) {
			return MainGrid.this.isInBounds(position.x, position.y);
		}

		@Override
		public FerryEntrance ferryAtPosition(ShortPoint2D position, byte playerId) {
			Optional<ILogicMovable> ferryOptional = HexGridArea.stream(position.x, position.y, 0, Constants.MAX_FERRY_ENTRANCE_SEARCH_DISTANCE)
															   .filterBounds(width, height)
															   .filter((x, y) -> landscapeGrid.getLandscapeTypeAt(x, y).isWater())
															   .iterateForResult((x, y) -> {
																   ILogicMovable movable = movableGrid.getMovableAt(x, y);
																   return Optional.ofNullable(movable).filter(m -> m.getMovableType() == EMovableType.FERRY);
															   });

			if (!ferryOptional.isPresent()) {
				return null;
			}

			ILogicMovable ferry = ferryOptional.get();
			ShortPoint2D ferryPosition = ferry.getPosition();
			Optional<ShortPoint2D> entranceOptional = HexGridArea.stream(ferryPosition.x, ferryPosition.y, 0, Constants.MAX_FERRY_ENTRANCE_SEARCH_DISTANCE)
																 .filterBounds(width, height)
																 .filter((x, y) -> !isBlocked(x, y))
																 .getFirst();

			if (!entranceOptional.isPresent()) {
				return null;
			}

			return new FerryEntrance(ferry, entranceOptional.get());
		}

		@Override
		public boolean isNavigable(int x, int y) {
			return MainGrid.this.isNavigable(x, y);
		}

		@Override
		public final void resetDebugColors() {
			landscapeGrid.resetDebugColors();
		}

		@Override
		public final Optional<ShortPoint2D> getConstructablePosition(ShortPoint2D pos, EBuildingType type, byte playerId) {
			return MapCircle.stream(pos, Constants.BUILDING_PLACEMENT_MAX_SEARCH_RADIUS)
							.filterBounds(width, height)
							.filter((x, y) -> constructionMarksGrid.canConstructAt(x, y, type, playerId))
							.min((x, y) -> ShortPoint2D.getOnGridDist(pos.x, pos.y, x, y));
		}

		@Override
		public final void save(Byte playerId, UIState uiState) throws IOException, InterruptedException {
			MainGrid.this.save(playerId, uiState);
		}

		@Override
		public final void toggleFogOfWar() {
			fogOfWar.toggleEnabled();
		}

		@Override
		public AbstractConstructionMarkableMap getConstructionMarksGrid() {
			return constructionMarksGrid;
		}

		@Override
		public void constructBuildingAt(ShortPoint2D position, EBuildingType type, byte playerId) {
			if (constructionMarksGrid.canConstructAt(position.x, position.y, type, playerId)) {
				MainGrid.this.constructBuildingAt(position, type, partitionsGrid.getPlayerAt(position.x, position.y), false);
			} else {
				System.out.println("WARNING: TRIED TO CONSTRUCT BUILDING WHERE IT WASN'T POSSIBLE! Type: " + type + "  pos: " + position
					+ "  playerId: " + playerId);
			}
		}

		@Override
		public void positionClicked(int x, int y) {
			System.out.println("clicked pos (" + x + "|" + y + "):  player: " + partitionsGrid.getPlayerIdAt(x, y) + "  partition: "
				+ partitionsGrid.getPartitionIdAt(x, y) + "  real partition: " + partitionsGrid.getRealPartitionIdAt(x, y) + "  towerCount: "
				+ partitionsGrid.getTowerCountAt(x, y) + " blocked partition: " + landscapeGrid.getBlockedPartitionAt(x, y) + " landscapeType: "
				+ landscapeGrid.getLandscapeTypeAt(x, y));
		}

		@Override
		public void setMaterialDistributionSettings(ShortPoint2D managerPosition, EMaterialType materialType, EBuildingType buildingType, float ratio) {
			if (isInBounds(managerPosition)) {
				partitionsGrid.getPartitionSettings(managerPosition).setMaterialDistributionSettings(materialType, buildingType, ratio);
			}
		}

		@Override
		public void setMaterialPrioritiesSettings(ShortPoint2D managerPosition, EMaterialType[] materialTypeForPriority) {
			if (isInBounds(managerPosition)) {
				partitionsGrid.getPartitionSettings(managerPosition).setMaterialPriorities(materialTypeForPriority);
			}
		}

		@Override
		public short getBlockedPartition(int x, int y) {
			return landscapeGrid.getBlockedPartitionAt(x, y);
		}

		@Override
		public boolean isBlocked(int x, int y) {
			return flagsGrid.isBlocked(x, y);
		}

		@Override
		public Player getPlayer(byte playerId) {
			return partitionsGrid.getPlayer(playerId);
		}

		@Override
		public byte getNumberOfPlayers() {
			return partitionsGrid.getNumberOfPlayers();
		}

		@Override
		public MaterialProductionSettings getMaterialProductionAt(ShortPoint2D position) {
			return getPartitionsGrid().getMaterialProductionAt(position.x, position.y);
		}

		@Override
		public void setAcceptedStockMaterial(ShortPoint2D position, EMaterialType materialType, boolean accepted) {
			partitionsGrid.getPartitionSettings(position).setAcceptedStockMaterial(materialType, accepted);
		}
	}

	/**
	 * This class implements the {@link IPlayerChangedListener} interface and executes all work that needs to be done when a position of the grid changes it's player.
	 *
	 * @author Andreas Eberle
	 */
	final class PlayerChangedListener implements IPlayerChangedListener {

		@Override
		public void playerChangedAt(int x, int y, byte newPlayerId) {
			final ShortPoint2D position = new ShortPoint2D(x, y);
			bordersThread.checkPosition(position);

			Building building = objectsGrid.getBuildingAt(x, y);
			if (building != null && building.getPlayer().getPlayerId() != newPlayerId) {
				building.kill();
			}
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
