package go.graphics.event.interpreter;


import go.graphics.event.mouse.GOHoverEvent;

import java.awt.Point;

/**
 * This class converts swing mouse movements to go events.
 * 
 * @author michael
 */
public class ConvertedHoverEvent extends AbstractMouseEvent implements
        GOHoverEvent {

	public ConvertedHoverEvent(Point start) {
		position = start;
	}

	@Override
	public Point getHoverPosition() {
		return position;
	}

}
