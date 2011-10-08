package jsettlers.graphics.map.controls;

import java.awt.Point;

import jsettlers.graphics.action.Action;

import go.graphics.GLDrawContext;

/**
 * Classes that implement this are capable of displaying the full game controls
 * (minimap, ...) on the screen.
 * 
 * @author michael
 */
public interface IControls {

	void drawAt(GLDrawContext gl);
	
	void resizeTo(int newWidth, int newHeight) ;

	boolean containsPoint(Point position);

	Action getActionFor(Point position);
}
