package jsettlers.logic.movable;

import jsettlers.TestWindow;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.movable.testmap.MovableTestsMap;
import jsettlers.logic.newmovable.NewMovable;
import synchronic.timer.NetworkTimer;

public class MovableTestWindow {
	public static void main(String args[]) {

		NetworkTimer.get().schedule();

		MovableTestsMap grid = new MovableTestsMap(100, 100);
		TestWindow.openTestWindow(grid);

		NewMovable movable = new NewMovable(grid.getMovableGrid(), EMovableType.TEST_MOVABLE);
		movable.positionAt(new ShortPoint2D(50, 50));

	}
}
