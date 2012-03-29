package jsettlers.logic.movable;

import jsettlers.TestWindow;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.MoveToAction;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.input.SelectionSet;
import jsettlers.logic.movable.testmap.MovableTestsMap;
import jsettlers.logic.newmovable.NewMovable;
import random.RandomSingleton;
import synchronic.timer.NetworkTimer;

public class MovableTestWindow {
	public static void main(String args[]) {
		new MovableTestWindow();
	}

	private NewMovable movable;

	private MovableTestWindow() {
		NetworkTimer.get().schedule();
		RandomSingleton.load(1000);

		MovableTestsMap grid = new MovableTestsMap(100, 100);
		MapInterfaceConnector connector = TestWindow.openTestWindow(grid);

		movable = new NewMovable(grid.getMovableGrid(), EMovableType.TEST_MOVABLE, (byte) 0);
		movable.positionAt(new ShortPoint2D(50, 50));

		connector.setSelection(new SelectionSet(movable));

		connector.addListener(new IMapInterfaceListener() {
			@Override
			public void action(Action action) {
				switch (action.getActionType()) {
				case MOVE_TO:
					movable.moveTo(((MoveToAction) action).getPosition());
					break;
				case SPEED_FASTER:
					NetworkTimer.multiplyGameSpeed(1.2f);
					break;
				case SPEED_SLOWER:
					NetworkTimer.multiplyGameSpeed(1 / 1.2f);
					break;
				}
			}
		});

		grid.getMovableGrid().dropMaterial(new ShortPoint2D(40, 40), EMaterialType.PLANK);
		grid.getMovableGrid().dropMaterial(new ShortPoint2D(60, 60), EMaterialType.STONE);

		new NewMovable(grid.getMovableGrid(), EMovableType.BEARER, (byte) 0).positionAt(new ShortPoint2D(30, 30));
		new NewMovable(grid.getMovableGrid(), EMovableType.BEARER, (byte) 0).positionAt(new ShortPoint2D(31, 31));
		new NewMovable(grid.getMovableGrid(), EMovableType.BEARER, (byte) 0).positionAt(new ShortPoint2D(32, 32));
		new NewMovable(grid.getMovableGrid(), EMovableType.BEARER, (byte) 0).positionAt(new ShortPoint2D(33, 33));
		;

	}
}
