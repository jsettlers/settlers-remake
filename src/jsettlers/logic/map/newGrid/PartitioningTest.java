package jsettlers.logic.map.newGrid;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.logging.StopWatch;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.UIState;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.swing.SwingResourceLoader;
import jsettlers.graphics.swing.SwingResourceProvider;
import jsettlers.logic.player.Player;
import jsettlers.main.swing.SwingManagedJSettlers;

public class PartitioningTest {
	static { // sets the native library path for the system dependent jogl libs
		SwingResourceLoader.setupSwingPaths();
	}

	private static final short HEIGHT = 400;
	private static final short WIDTH = 400;
	private static final byte DEFAULT_PLAYER_ID = 3;

	private static List<TestOccupyingBuilding> buildings = new LinkedList<TestOccupyingBuilding>();

	public static void main(String args[]) {
		ImageProvider.getInstance().startPreloading();
		ResourceManager.setProvider(new SwingResourceProvider());

		final MainGrid grid = new MainGrid(WIDTH, HEIGHT, (byte) 10);

		grid.fogOfWar.toggleEnabled();

		// fill landscape
		for (short y = 0; y < HEIGHT; y++) {
			for (short x = 0; x < WIDTH; x++) {
				grid.setLandscapeTypeAt(x, y, ELandscapeType.GRASS);
				grid.movablePathfinderGrid.changePlayerAt(getPos(x, y), grid.partitionsGrid.getPlayerForId(DEFAULT_PLAYER_ID));
			}
		}
		grid.bordersThread.start();

		// start GUI
		ISettlersGameDisplay gui = SwingManagedJSettlers.getGui();
		MapInterfaceConnector connector = gui.showGameMap(grid.graphicsGrid, null);
		connector.loadUIState(new UIState(0, getPos(140, 100)));
		connector.fireAction(new Action(EActionType.TOGGLE_DEBUG));
		connector.fireAction(new Action(EActionType.ZOOM_OUT));
		connector.fireAction(new Action(EActionType.ZOOM_OUT));
		connector.fireAction(new Action(EActionType.ZOOM_OUT));
		connector.fireAction(new Action(EActionType.ZOOM_OUT));
		connector.fireAction(new Action(EActionType.ZOOM_OUT));
		connector.fireAction(new Action(EActionType.ZOOM_OUT));

		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}

				// execute tests
				executeTests(grid);
			}
		}.start();
	}

	private static void executeTests(MainGrid grid) {
		StopWatch watch = new MilliStopWatch();

		createTowerAreaAt(grid, 100, 100, 0);
		createTowerAreaAt(grid, 180, 100, 1);
		createTowerAreaAt(grid, 140, 120, 2);
		createTowerAreaAt(grid, 190, 170, 3);

		realeaseTowerAreaAt(grid, 100, 100);
		changeTowerPlayerAt(grid, 180, 100, 3);

		realeaseTowerAreaAt(grid, 140, 120);

		createTowerAreaAt(grid, 90, 100, 0);

		createTowerAreaAt(grid, 140, 110, 2);

		realeaseTowerAreaAt(grid, 90, 100);
		realeaseTowerAreaAt(grid, 180, 100);
		realeaseTowerAreaAt(grid, 190, 170);

		watch.stop("the test needed");
	}

	private static void changeTowerPlayerAt(MainGrid grid, int x, int y, int newPlayerId) {
		ShortPoint2D pos = getPos(x, y);
		TestOccupyingBuilding building = getBuildingAt(pos);

		building.setPlayer(getPlayerForId(grid, newPlayerId));
		grid.buildingsGrid.freeOccupiedArea(building.getOccupyablePositions(), pos, buildings);
		grid.buildingsGrid.occupyArea(building.getOccupyablePositions(), new FreeMapArea(pos, EBuildingType.TOWER.getProtectedTiles()),
				grid.partitionsGrid.getPlayerForId((byte) newPlayerId));
	}

	private static void realeaseTowerAreaAt(MainGrid grid, int x, int y) {
		ShortPoint2D pos = getPos(x, y);
		TestOccupyingBuilding building = removeBuildingAt(pos);
		grid.buildingsGrid.freeOccupiedArea(building.getOccupyablePositions(), pos, buildings);
	}

	private static TestOccupyingBuilding getBuildingAt(ShortPoint2D pos) {
		for (TestOccupyingBuilding curr : buildings) {
			if (curr.getPos().equals(pos)) {
				return curr;
			}
		}
		return null;
	}

	private static TestOccupyingBuilding removeBuildingAt(ShortPoint2D pos) {
		Iterator<TestOccupyingBuilding> iter = buildings.iterator();
		while (iter.hasNext()) {
			TestOccupyingBuilding curr = iter.next();
			if (curr.getPos().equals(pos)) {
				iter.remove();
				return curr;
			}
		}
		return null;
	}

	private static void createTowerAreaAt(MainGrid grid, int x, int y, int playerId) {
		ShortPoint2D pos = getPos(x, y);
		TestOccupyingBuilding building = new TestOccupyingBuilding(pos, getPlayerForId(grid, playerId));
		buildings.add(building);
		grid.buildingsGrid.occupyArea(building.getOccupyablePositions(), new FreeMapArea(pos, EBuildingType.TOWER.getProtectedTiles()),
				getPlayerForId(grid, (byte) playerId));
	}

	private static Player getPlayerForId(MainGrid grid, int playerId) {
		return grid.partitionsGrid.getPlayerForId((byte) playerId);
	}

	private static ShortPoint2D getPos(int x, int y) {
		return new ShortPoint2D(x, y);
	}

}
