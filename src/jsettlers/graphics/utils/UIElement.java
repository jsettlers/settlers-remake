package jsettlers.graphics.utils;

import javax.media.opengl.GL2;

import jsettlers.common.position.IntRectangle;
import jsettlers.graphics.action.Action;

public interface UIElement {
	void setPosition(IntRectangle position);

	void drawAt(GL2 gl);
	
	Action getAction(float relativex, float relativey);

	String getDescription(float relativex, float relativey);
}
