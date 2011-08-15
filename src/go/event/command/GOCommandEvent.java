package go.event.command;

import go.event.GOEvent;

import java.awt.Point;

public interface GOCommandEvent extends GOEvent {
	Point getCommandPosition();
	
	boolean isSelecting();
}
