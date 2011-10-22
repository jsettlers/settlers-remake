package jsettlers.graphics.map.controls;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
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

	boolean containsPoint(UIPoint position);

	String getDescriptionFor(UIPoint position);

	void setMapViewport(MapRectangle screenArea);

	Action getActionFor(UIPoint position);

}
