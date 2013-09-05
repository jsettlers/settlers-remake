package jsettlers.logic.movable;

import jsettlers.TestUtils;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.input.SelectionSet;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.testmap.MovableTestsMap;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.player.Player;
import jsettlers.logic.player.Team;
import networklib.synchronic.random.RandomSingleton;
import networklib.synchronic.timer.NetworkTimer;

public class MovableTestWindow {
	private static final Player PLAYER_0 = new Player((byte) 0, new Team((byte) 0));
	private NewMovable movable;

	public static void main(String args[]) throws InterruptedException {
		new MovableTestWindow();
	}

	private MovableTestWindow() throws InterruptedException {

		MatchConstants.clock = new NetworkTimer(true);
		MatchConstants.clock.startExecution();
		RandomSingleton.load(1000);

		MovableTestsMap grid = new MovableTestsMap(100, 100, PLAYER_0);
		MapInterfaceConnector connector = TestUtils.openTestWindow(grid);

		movable = new NewMovable(grid.getMovableGrid(), EMovableType.PIONEER, new ShortPoint2D(49, 50), PLAYER_0);
		movable.setSelected(true);

		connector.setSelection(new SelectionSet(movable));

		connector.addListener(new IMapInterfaceListener() {
			@Override
			public void action(Action action) {
				switch (action.getActionType()) {
				case MOVE_TO:
					movable.moveTo(((PointAction) action).getPosition());
					break;
				case SPEED_FASTER:
					MatchConstants.clock.multiplyGameSpeed(1.2f);
					break;
				case SPEED_SLOWER:
					MatchConstants.clock.multiplyGameSpeed(1 / 1.2f);
					break;
				case FAST_FORWARD:
					MatchConstants.clock.fastForward();
					break;
				default:
					break;
				}
			}
		});

		grid.getMovableGrid().dropMaterial(new ShortPoint2D(40, 40), EMaterialType.PLANK, true);
		grid.getMovableGrid().dropMaterial(new ShortPoint2D(60, 60), EMaterialType.STONE, true);

		new NewMovable(grid.getMovableGrid(), EMovableType.BEARER, new ShortPoint2D(30, 30), PLAYER_0);
		new NewMovable(grid.getMovableGrid(), EMovableType.BEARER, new ShortPoint2D(31, 31), PLAYER_0);
		new NewMovable(grid.getMovableGrid(), EMovableType.BEARER, new ShortPoint2D(32, 32), PLAYER_0);
		new NewMovable(grid.getMovableGrid(), EMovableType.BEARER, new ShortPoint2D(33, 33), PLAYER_0);

		new NewMovable(grid.getMovableGrid(), EMovableType.BEARER, new ShortPoint2D(50, 50), PLAYER_0);

		{// test automatic distribution of many movables next to each other
			for (int x = 30; x < 40; x++) {
				for (int y = 80; y < 90; y++) {
					new NewMovable(grid.getMovableGrid(), EMovableType.BEARER, new ShortPoint2D(x, y), PLAYER_0);
				}
			}
		}

		{
			Thread.sleep(3000);
			// circle of three movables blocking each others path
			NewMovable m1 = new NewMovable(grid.getMovableGrid(), EMovableType.PIONEER, new ShortPoint2D(50, 65), PLAYER_0);
			NewMovable m2 = new NewMovable(grid.getMovableGrid(), EMovableType.PIONEER, new ShortPoint2D(51, 65), PLAYER_0);
			NewMovable m3 = new NewMovable(grid.getMovableGrid(), EMovableType.PIONEER, new ShortPoint2D(50, 64), PLAYER_0);

			m1.moveTo(new ShortPoint2D(52, 65));
			m2.moveTo(new ShortPoint2D(49, 63));
			m3.moveTo(new ShortPoint2D(50, 66));
		}
	}
}
