package jsettlers.graphics.map;

import jsettlers.common.position.ISPosition2D;

public class UIState {

	/**
	 * The player that uses the ui.
	 */
	private final int player;
	
	/**
	 * The center point of the screen.
	 */
	private final ISPosition2D screenCenter;

	public UIState(int player, ISPosition2D startPoint) {
		this.player = player;
		this.screenCenter = startPoint;
    }
	
	public ISPosition2D getScreenCenter() {
	    return screenCenter;
    }
	
	public int getPlayer() {
	    return player;
    }

}
