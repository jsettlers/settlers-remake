package jsettlers.algorithms.supergrid;

import jsettlers.TestWindow;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.MoveToAction;
import jsettlers.graphics.action.SelectAction;
import jsettlers.graphics.action.SelectAreaAction;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapInterfaceConnector;

public class SuperGridTestWindow {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final TestMapGrid grid = new TestMapGrid((short) 300, (short) 300);
		MapInterfaceConnector connector = TestWindow.openTestWindow(grid);

		final SuperGridAStar aStar = new SuperGridAStar(grid.getWidth(), grid.getHeight(), grid);

		connector.addListener(new IMapInterfaceListener() {
			ShortPoint2D first;

			@Override
			public void action(Action action) {
				switch (action.getActionType()) {
				case MOVE_TO: {
					ShortPoint2D pos = ((MoveToAction) action).getPosition();
					if (first == null) {
						first = pos;
					} else {
						aStar.findPath(first.getX(), first.getY(), pos.getX(), pos.getY());
						first = null;
					}
				}
					break;

				case SELECT_POINT:
					ShortPoint2D position = ((SelectAction) action).getPosition();
					grid.invertBlocked(position.getX(), position.getY());
					break;

				case SELECT_AREA:
					SelectAreaAction a = (SelectAreaAction) action;
					for (ShortPoint2D pos : a.getArea()) {
						grid.invertBlocked(pos.getX(), pos.getY());
					}
					break;

				}
			}
		});

	}
}
