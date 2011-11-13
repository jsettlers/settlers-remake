package jsettlers.graphics.utils;

import go.graphics.GLDrawContext;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;

public interface UIButton {

	public abstract FloatRectangle getPosition();

	public abstract void drawAt(GLDrawContext gl);
	
	public Action getAction();

}
