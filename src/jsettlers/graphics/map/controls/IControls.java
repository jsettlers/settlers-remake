package jsettlers.graphics.map.controls;

import go.graphics.GLDrawContext;

import java.awt.Point;

import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.graphics.action.Action;

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

	String getDescriptionFor(Point position);

	void setMapViewport(MapRectangle screenArea);
}
