package go.graphics.event.command;

import go.graphics.UIPoint;
import go.graphics.event.GOEvent;

public interface GOCommandEvent extends GOEvent {
	UIPoint getCommandPosition();

	boolean isSelecting();
}
