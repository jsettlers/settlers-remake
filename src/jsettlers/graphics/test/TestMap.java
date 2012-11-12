package jsettlers.graphics.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;

public class TestMap implements IGraphicsGrid {
	private static final int HEIGHT = 150;
	private static final int WIDTH = 150;
	private static final int SETTLERS = 300;
	private static final int BUILDINGS = 80;
	private static final double HEIGHTCIRCLESIZE = 40;
	private static final int RIVERS = 50;
	private static final int MATERIALS = 100;
	private static final int TREES = 50;
	private static final int TREES_IN_FOREST = 40;
	private static final int FORESTS = 30;
	private static final double FORESTSIZE = 20;
	private static final int STONES_IN_GROUP = 10;
	private static final int STONEGROUPS = 15;

	private ArrayList<TestTile> tiles = new ArrayList<TestTile>();

	private ArrayList<TestSettler> settlers = new ArrayList<TestSettler>();
	private ArrayList<TestBuilding> buildings = new ArrayList<TestBuilding>();

	// only for direction, ... calculations, not for displaying.
	// MapDrawContext context = new MapDrawContext(this);

	private byte[][] heights = new byte[WIDTH][HEIGHT];

	public TestMap() {
		generateHeights();

		for (int i = 0; i < WIDTH * HEIGHT; i++) {
			int x = i % WIDTH;
			int y = i / WIDTH;
			TestTile tile = new TestTile((short) x, (short) y, this.heights[x][y]);
			this.tiles.add(tile);
		}

		addRivers();
		addSettlers();
		addBuildings();
		addMaterials();
		addTrees();
		// addPlayers();
		addStones();

		Timer timer = new Timer(true);
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				moveStep();
			}
		}, 100, 100);
	}

	private void addTrees() {
		for (int i = 0; i < TREES; i++) {

			int cx = (int) (Math.random() * WIDTH);
			int cy = (int) (Math.random() * HEIGHT);

			TestTile tile = getTile(cx, cy);
			if (tile.getLandscapeType() == ELandscapeType.GRASS) {
				tile.setMapObject(new TestTree());
			}
		}

		for (int i = 0; i < FORESTS; i++) {
			int cx = (int) (Math.random() * (WIDTH - FORESTSIZE));
			int cy = (int) (Math.random() * (HEIGHT - FORESTSIZE));
			addForrest(cx, cy);
		}
	}

	private void addForrest(int x, int y) {
		for (int i = 0; i < TREES_IN_FOREST; i++) {
			int cx = (int) (Math.random() * FORESTSIZE) + x;
			int cy = (int) (Math.random() * FORESTSIZE) + y;

			TestTile tile = getTile(cx, cy);
			if (tile.getLandscapeType() == ELandscapeType.GRASS) {
				tile.setMapObject(new TestTree());
			}
		}
	}

	private void addStones() {
		for (int j = 0; j < STONEGROUPS; j++) {
			int x = (int) (Math.random() * (WIDTH - FORESTS));
			int y = (int) (Math.random() * (HEIGHT - FORESTSIZE));
			for (int i = 0; i < STONES_IN_GROUP; i++) {
				int cx = (int) (Math.random() * FORESTSIZE) + x;
				int cy = (int) (Math.random() * FORESTSIZE) + y;

				TestTile tile = getTile(cx, cy);
				if (tile.getLandscapeType() == ELandscapeType.GRASS) {
					tile.setMapObject(new TestStone((int) (Math.random() * 14)));
				}
			}
		}
	}

	private TestTile getTile(int x, int y) {
		int i = y * WIDTH + x;
		if (i < tiles.size() && i >= 0) {
			return this.tiles.get(i);
		} else {
			return null;
		}
	}

	public TestTile getTile(ShortPoint2D pos) {
		return getTile(pos.x, pos.y);
	}

	private void addBuildings() {
		for (int i = 0; i < BUILDINGS; i++) {
			int cx = (int) (Math.random() * WIDTH);
			int cy = (int) (Math.random() * HEIGHT);

			TestTile tile = getTile(cx, cy);
			TestBuilding building = null;
			if (tile.getLandscapeType() == ELandscapeType.GRASS) {
				building = new TestBuilding(tile.getPos(), EBuildingType.LUMBERJACK);
			} else if (tile.getLandscapeType() == ELandscapeType.MOUNTAIN) {
				building = new TestBuilding(tile.getPos(), EBuildingType.COALMINE);
			} else if (tile.getLandscapeType() == ELandscapeType.SAND) {
				building = new TestBuilding(tile.getPos(), EBuildingType.FISHER);
			}
			if (building != null) {
				this.buildings.add(building);
				tile.setBuilding(building);
			}
		}
	}

	private void addSettlers() {
		for (int i = 0; i < SETTLERS; i++) {
			int cx = (int) (Math.random() * WIDTH);
			int cy = (int) (Math.random() * HEIGHT);

			if (this.heights[cx][cy] < 3 || this.heights[cx][cy] > 10) {
				continue;
			}

			TestTile tile = getTile(cx, cy);

			EMovableType type = getRandomSettlerType();
			TestSettler settler = new TestSettler(getRandomDirection(), type, tile, (byte) (Math.random() * 8));
			if (type == EMovableType.BEARER) {
				settler.setMaterial(getRandomMaterial());
			}
			tile.setMovable(settler);
			this.settlers.add(settler);
		}
	}

	private EMovableType getRandomSettlerType() {
		if (Math.random() < .6) {
			return EMovableType.BEARER;
		} else {
			return EMovableType.values()[(int) (Math.random() * EMovableType.values().length)];
		}
	}

	private EDirection getRandomDirection() {
		int random = (int) (Math.random() * 6);
		switch (random) {
		case 0:
			return EDirection.EAST;
		case 1:
			return EDirection.NORTH_EAST;
		case 2:
			return EDirection.NORTH_WEST;
		case 3:
			return EDirection.WEST;
		case 4:
			return EDirection.SOUTH_WEST;
		default:
			return EDirection.SOUTH_EAST;
		}
	}

	private EMaterialType getRandomMaterial() {
		int random = (int) (Math.random() * 23);
		switch (random) {
		case 0:
			return EMaterialType.AXE;
		case 1:
			return EMaterialType.BOW;
		case 2:
			return EMaterialType.BREAD;
		case 3:
			return EMaterialType.COAL;
		case 4:
			return EMaterialType.CROP;
		case 5:
			return EMaterialType.FISH;
		case 6:
			return EMaterialType.FISHINGROD;
		case 7:
			return EMaterialType.FLOUR;
		case 8:
			return EMaterialType.GOLD;
		case 9:
			return EMaterialType.GOLDORE;
		case 10:
			return EMaterialType.HAMMER;
		case 11:
			return EMaterialType.IRON;
		case 12:
			return EMaterialType.IRONORE;
		case 13:
			return EMaterialType.MEAT;
		case 14:
			return EMaterialType.NO_MATERIAL;
		case 15:
			return EMaterialType.PICK;
		case 16:
			return EMaterialType.PIG;
		case 17:
			return EMaterialType.PLANK;
		case 18:
			return EMaterialType.SAW;
		case 19:
			return EMaterialType.SCYTHE;
		case 20:
			return EMaterialType.SPEAR;
		case 21:
			return EMaterialType.SWORD;
		case 22:
			return EMaterialType.WATER;
		default:
			return EMaterialType.TRUNK;
		}
	}

	private void generateHeights() {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				this.heights[x][y] = (byte) (-3 * Math.random());
			}
		}

		for (int i = 0; i < 30; i++) {
			int cx = (int) (Math.random() * WIDTH);
			int cy = (int) (Math.random() * HEIGHT);
			double r = Math.random() * HEIGHTCIRCLESIZE;
			MapCircle circle = new MapCircle((short) cx, (short) cy, (float) r);

			for (ShortPoint2D pos : new MapShapeFilter(circle, WIDTH, HEIGHT)) {
				double add = (r - circle.distanceToCenter(pos.x, pos.y)) / 5;
				this.heights[pos.x][pos.y] += (byte) add;
			}
		}
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				if (this.heights[x][y] < 0) {
					this.heights[x][y] = 0;
				}
			}
		}

		smootHeights();

		removeCanyons();
	}

	private void removeCanyons() {
		for (int y = 0; y < HEIGHT - 2; y++) {
			for (int x = 0; x < WIDTH - 2; x++) {
				if (this.heights[x + 1][y] < this.heights[x][y] && this.heights[x + 1][y] < this.heights[x + 2][y]) {
					this.heights[x + 1][y] = this.heights[x][y];
				}

				if (this.heights[x][y + 1] < this.heights[x][y] && this.heights[x][y + 1] < this.heights[x][y + 2]) {
					this.heights[x][y + 1] = this.heights[x][y];
				}
			}
		}
	}

	private int smootHeights() {
		int changed = 0;
		for (int y = 0; y < HEIGHT - 1; y++) {
			for (int x = 0; x < WIDTH - 1; x++) {
				if (this.heights[x + 1][y] > this.heights[x][y] + 1) {
					this.heights[x + 1][y] = (byte) (this.heights[x][y] + 1);
					changed++;
				} else if (this.heights[x + 1][y] < this.heights[x][y] - 1) {
					this.heights[x + 1][y] = (byte) (this.heights[x][y] - 1);
					changed++;
				}

				if (this.heights[x][y + 1] > this.heights[x][y] + 1) {
					this.heights[x][y + 1] = (byte) (this.heights[x][y] + 1);
					changed++;
				} else if (this.heights[x][y + 1] < this.heights[x][y] - 1) {
					this.heights[x][y + 1] = (byte) (this.heights[x][y] - 1);
					changed++;
				}
			}
		}
		return changed;
	}

	private void addRivers() {
		for (int i = 0; i < RIVERS; i++) {
			int x = (int) (Math.random() * WIDTH);
			int y = (int) (Math.random() * HEIGHT);
			TestTile current = getTile(x, y);

			if (current.getLandscapeType() != ELandscapeType.GRASS) {
				continue;
			}

			while (current != null && !current.getLandscapeType().isWater()) {
				current.setRiver(true);

				List<EDirection> directions = Arrays.asList(EDirection.values);
				Collections.shuffle(directions);
				TestTile goTo = null;
				for (EDirection possibleDirection : directions) {
					TestTile tile = this.getTile(possibleDirection.getNextHexPoint(current.getPos()));
					if (tile != null && tile.getHeight() <= current.getHeight() && tile.getLandscapeType() != ELandscapeType.RIVER1
							&& tile.getLandscapeType() != ELandscapeType.RIVER2 && tile.getLandscapeType() != ELandscapeType.RIVER3
							&& tile.getLandscapeType() != ELandscapeType.RIVER4 && getNeighbourRiverCount(tile) < 2) {
						goTo = tile;
						break;
					}
				}

				current = goTo;
			}
		}
	}

	private int getNeighbourRiverCount(TestTile tile) {
		int rivers = 0;
		for (EDirection dir : EDirection.values) {
			TestTile toTest = this.getTile(dir.getNextHexPoint(tile.getPos()));
			if (toTest != null
					&& (toTest.getLandscapeType() == ELandscapeType.RIVER1 || toTest.getLandscapeType() == ELandscapeType.RIVER2
							|| toTest.getLandscapeType() == ELandscapeType.RIVER3 || toTest.getLandscapeType() == ELandscapeType.RIVER4)) {
				rivers++;
			}
		}
		return rivers;
	}

	private void addMaterials() {
		for (int i = 0; i < MATERIALS; i++) {
			int x = (int) (Math.random() * WIDTH);
			int y = (int) (Math.random() * HEIGHT);

			EMaterialType type = getRandomMaterial();
			int count = (int) (Math.random() * 8 + 1);

			TestTile tile = getTile(x, y);
			ELandscapeType landscape = tile.getLandscapeType();
			if (allowsStackPlacement(landscape)) {
				tile.setStack(new TestStack(type, count));
			}
		}
	}

	private static boolean allowsStackPlacement(ELandscapeType landscape) {
		return landscape.isWater() && landscape != ELandscapeType.MOOR && landscape != ELandscapeType.RIVER1 && landscape != ELandscapeType.RIVER2
				&& landscape != ELandscapeType.RIVER3 && landscape != ELandscapeType.RIVER4 && landscape != ELandscapeType.SNOW;
	}

	public void moveStep() {
		for (TestSettler settler : this.settlers) {
			settler.increaseProgress();
			if (settler.moveOn()) {
				TestTile newPosition = this.getTile(settler.getDirection().getNextHexPoint(settler.getPos()));
				if (newPosition == null) {
					// should not happen
					EDirection direction = getRandomDirection();
					settler.setDirection(direction);
				} else {

					TestTile nextPosition = this.getTile(settler.getDirection().getNextHexPoint(newPosition.getPos()));

					this.getTile(settler.getPos()).setMovable(null);
					newPosition.setMovable(settler);
					settler.setPosition(newPosition);

					if (nextPosition == null || nextPosition.getMovable() != null || nextPosition.getLandscapeType() == ELandscapeType.WATER1) {
						// may not move...
						EDirection direction = getRandomDirection();
						settler.setDirection(direction);
					}
				}
			}
		}
		for (TestBuilding bulding : this.buildings) {
			bulding.increaseConstructed();
		}
	}

	public EBuildingType getConstructionPreviewBuilding() {
		return null;
	}

	@Override
	public short getHeight() {
		return HEIGHT;
	}

	@Override
	public short getWidth() {
		return WIDTH;
	}

	public TestTile getTile(short x, short y) {
		return getTile((int) x, (int) y);
	}

	/**
	 * Gets any unspecified building.
	 * 
	 * @return The building.
	 */
	public IBuilding getAnyBuilding() {
		return this.buildings.get(0);
	}

	public List<? extends IMovable> getAllSettlers() {
		return this.settlers;
	}

	@Override
	public IMovable getMovableAt(int x, int y) {
		return getTile(x, y).getMovable();
	}

	@Override
	public IMapObject getMapObjectsAt(int x, int y) {
		if (getTile(x, y).getBuilding() != null) {
			return getTile(x, y).getBuilding();
		} else {
			return getTile(x, y).getHeadMapObject();
		}
	}

	@Override
	public byte getHeightAt(int x, int y) {
		return getTile(x, y).getHeight();
	}

	@Override
	public ELandscapeType getLandscapeTypeAt(int x, int y) {
		return getTile(x, y).getLandscapeType();
	}

	@Override
	public int getDebugColorAt(int x, int y) {
		return -1;
	}

	@Override
	public boolean isBorder(int x, int y) {
		return false;
	}

	@Override
	public byte getPlayerIdAt(int x, int y) {
		return 0;
	}

	@Override
	public byte getVisibleStatus(int x, int y) {
		return CommonConstants.FOG_OF_WAR_VISIBLE;
	}

	@Override
	public boolean isFogOfWarVisible(int x, int y) {
		return true;
	}

	@Override
	public void setBackgroundListener(IGraphicsBackgroundListener backgroundListener) {
	}

	@Override
	public int nextDrawableX(int x, int y, int maxX) {
		return x + 1;
	}
}
