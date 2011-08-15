package jsettlers.graphics.utils;

import javax.media.opengl.GL2;

import jsettlers.common.position.IntRectangle;
import jsettlers.graphics.action.Action;

public interface UIButton {

	public abstract IntRectangle getPosition();

	public abstract void drawAt(GL2 gl);
	
	public Action getAction();

}
