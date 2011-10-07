package go.graphics.event.command;

import go.graphics.event.GOEvent;

import java.awt.Point;

public interface GOCommandEvent extends GOEvent {
	Point getCommandPosition();
	
	boolean isSelecting();
}
