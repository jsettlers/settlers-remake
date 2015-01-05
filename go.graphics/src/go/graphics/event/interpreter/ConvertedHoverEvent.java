package go.graphics.event.interpreter;

import go.graphics.UIPoint;
import go.graphics.event.mouse.GOHoverEvent;

/**
 * This class converts swing mouse movements to go events.
 * 
 * @author michael
 */
public class ConvertedHoverEvent extends AbstractMouseEvent implements
		GOHoverEvent {

	public ConvertedHoverEvent(UIPoint start) {
		position = start;
	}

	@Override
	public UIPoint getHoverPosition() {
		return position;
	}

}
