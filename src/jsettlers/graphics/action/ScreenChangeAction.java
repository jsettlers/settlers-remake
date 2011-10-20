package jsettlers.graphics.action;

import jsettlers.common.map.shapes.MapRectangle;

/**
 * @see EActionType#SCREEN_CHANGE
 * @author michael
 */
public class ScreenChangeAction extends Action {

	private final MapRectangle screenArea;

	/**
	 * Creates a new screen change action
	 * 
	 * @param screenArea
	 *            the area
	 */
	public ScreenChangeAction(MapRectangle screenArea) {
		super(EActionType.SCREEN_CHANGE);
		this.screenArea = screenArea;
	}

	/**
	 * Gets the new area of the screen
	 * 
	 * @return The screen area.
	 */
	public MapRectangle getScreenArea() {
		return screenArea;
	}

}
