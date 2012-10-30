package jsettlers.logic.map.newGrid;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.logging.StopWatch;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.utils.collections.FilterIterator;
import jsettlers.common.utils.collections.IPredicate;
import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.UIState;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.swing.SwingResourceLoader;
import jsettlers.graphics.swing.SwingResourceProvider;
import jsettlers.logic.algorithms.borders.traversing.BorderTraversingAlgorithm;
import jsettlers.logic.algorithms.borders.traversing.IBorderVisitor;
import jsettlers.logic.algorithms.borders.traversing.IContainingProvider;
import jsettlers.logic.algorithms.partitions.PartitionCalculatorAlgorithm;
import jsettlers.main.swing.SwingManagedJSettlers;

public class TestNewPartitioner {
	static { // sets the native library path for the system dependent jogl libs
		SwingResourceLoader.setupSwingPaths();
	}

	private static final short HEIGHT = 400;
	private static final short WIDTH = 400;

	public static void main(String args[]) {
		ImageProvider.getInstance().startPreloading();
		ResourceManager.setProvider(new SwingResourceProvider());

		final MainGrid grid = new MainGrid(WIDTH, HEIGHT);

		grid.fogOfWar.toggleEnabled();

		// fill landscape
		for (short y = 0; y < HEIGHT; y++) {
			for (short x = 0; x < WIDTH; x++) {
				grid.setLandscapeTypeAt(x, y, ELandscapeType.GRASS);
				// grid.movablePathfinderGrid.changePlayerAt(getPos(x, y), DEFAULT_PLAYER);
			}
		}
		grid.bordersThread.start();

		// start GUI
		ISettlersGameDisplay gui = SwingManagedJSettlers.getGui();
		MapInterfaceConnector connector = gui.showGameMap(grid.graphicsGrid, null);
		connector.loadUIState(new UIState(0, getPos(200, 200)));
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

	private static void executeTests(final MainGrid grid) {
		initGrid(grid);

		StopWatch watch = new MilliStopWatch();

		final int radius = 130;
		final int xPos = 200;
		final int yPos = 200;
		final ShortPoint2D center = getPos(xPos, yPos);

		IPredicate<ShortPoint2D> predicate = new IPredicate<ShortPoint2D>() {
			MapCircle circle1 = new MapCircle(center, radius / 4);
			MapCircle circle2 = new MapCircle(getPos(xPos + radius / 2, yPos - radius / 4), radius / 2);
			MapCircle circle3 = new MapCircle(getPos(xPos - radius, yPos), radius);
			MapCircle circle4 = new MapCircle(getPos(xPos - radius / 2, yPos), radius / 4);

			@Override
			public boolean evaluate(ShortPoint2D pos) {
				return !circle3.contains(pos) && !circle2.contains(pos) && !circle1.contains(pos) || circle4.contains(pos)
						|| (pos.getX() == 230 && pos.getY() == 200);
			}
		};

		FilterIterator<ShortPoint2D> filtered = new FilterIterator<ShortPoint2D>(new MapCircle(center, radius), predicate);

		double xFactor = 1.2;
		double yFactor = 1.2;

		int minX = (int) (xPos - radius * xFactor);
		int minY = (int) (yPos - radius * yFactor);

		int maxX = (int) (xPos + radius * xFactor);
		int maxY = (int) (yPos + radius * yFactor);

		PartitionCalculatorAlgorithm partitioner = new PartitionCalculatorAlgorithm(filtered, minX, minY, maxX, maxY);
		partitioner.calculatePartitions();

		minX = partitioner.getMinX();
		minY = partitioner.getMinY();
		int width = partitioner.getWidth();
		int height = partitioner.getHeight();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				short partition = partitioner.getPartitionAt(x, y);
				if (partition > 0) {
					grid.partitionsGrid.setPartitionAndPlayerAt((short) (x + minX), (short) (y + minY), (byte) partition);
				}
			}
		}

		System.out.println("number of partitions: " + partitioner.getNumberOfPartitions());
		for (int i = 1; i <= partitioner.getNumberOfPartitions(); i++) {
			ShortPoint2D pos = partitioner.getPartitionBorderPos(i);
			grid.setLandscapeTypeAt(pos.getX(), pos.getY(), ELandscapeType.RIVER1);

			final byte partition = (byte) i;
			BorderTraversingAlgorithm.traverseBorder(new IContainingProvider() {
				@Override
				public boolean contains(int x, int y) {
					return grid.partitionsGrid.getPartitionAt((short) x, (short) y) == partition;
				}
			}, pos, getBorderVisitor(grid, partition));
		}

		watch.stop("the test needed");
	}

	private static void initGrid(MainGrid grid) {
		setCircleToGrid(grid, 150, 230, 50, (short) 7);
		setCircleToGrid(grid, 150, 150, 50, (short) 5);
		setCircleToGrid(grid, 228, 230, 20, (short) 6);
	}

	private static void setCircleToGrid(MainGrid grid, int x, int y, int radius, short partition) {
		MapCircle c1 = new MapCircle(getPos(x, y), radius);
		for (ShortPoint2D curr : c1) {
			grid.partitionsGrid.setPartitionAndPlayerAt(curr.getX(), curr.getY(), partition);
		}
	}

	private static IBorderVisitor getBorderVisitor(final MainGrid grid, final short innerPartition) {
		return new IBorderVisitor() {
			short lastPartititon;

			byte players[] = { 0, 1, 1, 1, 1, 2, 3, 1 };

			@Override
			public void visit(int x, int y) {
				grid.mapObjectsManager.addSimpleMapObject(getPos(x, y), EMapObjectType.BUILDINGSITE_POST, false, (byte) 0);

				short currPartition = grid.partitionsGrid.getPartition((short) x, (short) y);
				if (currPartition != lastPartititon && currPartition >= 0) {
					if (players[currPartition] == players[innerPartition]) {
						merge(currPartition, innerPartition);
					}
				}

				lastPartititon = currPartition;
			}

			private void merge(short partition1, short partition2) {
				System.out.println("merge partitions " + partition1 + " and " + partition2);
			}

		};
	}

	private static ShortPoint2D getPos(int x, int y) {
		return new ShortPoint2D(x, y);
	}

}
