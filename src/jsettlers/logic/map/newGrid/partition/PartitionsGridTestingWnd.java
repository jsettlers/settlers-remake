package jsettlers.logic.map.newGrid.partition;

import jsettlers.TestWindow;
import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.UIState;

public class PartitionsGridTestingWnd {

	protected static final short HEIGHT = 400;
	protected static final short WIDTH = 400;

	public static void main(String args[]) throws InterruptedException {
		PartitionsGridTestingWnd testWnd = new PartitionsGridTestingWnd();

		// open the window
		MapInterfaceConnector connector = TestWindow.openTestWindow(testWnd.getGraphicsGrid());
		connector.loadUIState(new UIState(0, new ShortPoint2D(200, 200)));
		connector.fireAction(new Action(EActionType.TOGGLE_DEBUG));
		connector.fireAction(new Action(EActionType.ZOOM_OUT));
		connector.fireAction(new Action(EActionType.ZOOM_OUT));
		connector.fireAction(new Action(EActionType.ZOOM_OUT));
		connector.fireAction(new Action(EActionType.ZOOM_OUT));
		connector.fireAction(new Action(EActionType.ZOOM_OUT));
		connector.fireAction(new Action(EActionType.ZOOM_OUT));

		Thread.sleep(1500);
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

		MilliStopWatch watch = new MilliStopWatch();
		testWnd.startTest();
		watch.stop("The tests needed");
	}

	private final PartitionsGrid grid;

	private PartitionsGridTestingWnd() {
		this.grid = new PartitionsGrid(WIDTH, HEIGHT, (byte) 10);
	}

	private void startTest() {
		// occupyAreaByTower(80, 80, 40, (byte) 1);
		// occupyAreaByTower(153, 75, 40, (byte) 3);
		// occupyAreaByTower(130, 100, 40, (byte) 2);
		//
		// occupyAreaByTower(96, 130, 40, (byte) 4);
		//
		// occupyAreaByTower(150, 130, 40, (byte) 2);
		//
		// partitionsGrid.removeTowerAndFreeOccupiedArea(new ShortPoint2D(80, 80));

		addTower(0, 150, 200, 40);
		addTower(0, 250, 200, 40);

		addTower(0, 200, 200, 40);
		addTower(0, 220, 220, 40);

		changePlayerOfTower(150, 200, 1);
		changePlayerOfTower(200, 200, 2);

		// grid.removeTowerAndFreeOccupiedArea(new ShortPoint2D(150, 200));
	}

	private void changePlayerOfTower(int x, int y, int newPlayer) {
		ShortPoint2D pos = new ShortPoint2D(x, y);
		grid.changePlayerOfTower(pos, (byte) newPlayer, new FreeMapArea(pos, EBuildingType.TOWER.getProtectedTiles()));
	}

	private void addTower(int playerId, int x, int y, int radius) {
		grid.addTowerAndOccupyArea((byte) playerId, new MapCircle(new ShortPoint2D(x, y), radius));
	}

	private IGraphicsGrid getGraphicsGrid() {
		return new IGraphicsGrid() {
			@Override
			public void setBackgroundListener(IGraphicsBackgroundListener backgroundListener) {
			}

			@Override
			public int nextDrawableX(int x, int y, int maxX) {
				return x + 1;
			}

			@Override
			public boolean isFogOfWarVisible(int x, int y) {
				return true;
			}

			@Override
			public boolean isBorder(int x, int y) {
				return false;
			}

			@Override
			public short getWidth() {
				return WIDTH;
			}

			@Override
			public byte getVisibleStatus(int x, int y) {
				return CommonConstants.FOG_OF_WAR_VISIBLE;
			}

			@Override
			public byte getPlayerIdAt(int x, int y) {
				return 0;
			}

			@Override
			public IMovable getMovableAt(int x, int y) {
				return null;
			}

			@Override
			public IMapObject getMapObjectsAt(int x, int y) {
				return null;
			}

			@Override
			public ELandscapeType getLandscapeTypeAt(int x, int y) {
				return ELandscapeType.GRASS;
			}

			@Override
			public byte getHeightAt(int x, int y) {
				return 0;
			}

			@Override
			public short getHeight() {
				return HEIGHT;
			}

			@Override
			public int getDebugColorAt(int x, int y) {
				// int value = grid.getRealPartitionIdAt(x, y);
				int value = grid.getPartitionIdAt(x, y);
				// int value = grid.getTowerCountAt(x, y);
				// int value = grid.getPlayerIdAt(x, y) + 1; // +1 to get -1 player displayed as black

				return Color.getARGB((value % 3) * 0.33f, ((value / 3) % 3) * 0.33f, ((value / 9) % 3) * 0.33f, 1);
			}
		};
	}
}
