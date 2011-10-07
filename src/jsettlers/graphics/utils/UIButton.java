package jsettlers.graphics.utils;

import go.graphics.GLDrawContext;
import jsettlers.common.position.IntRectangle;
import jsettlers.graphics.action.Action;

public interface UIButton {

	public abstract IntRectangle getPosition();

	public abstract void drawAt(GLDrawContext gl);
	
	public Action getAction();

}
