package jsettlers.graphics.map.controls.mobile;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;

public interface MobileMenu {

	void drawAt(GLDrawContext gl);

	Action getActionFor(UIPoint position);

	void setPosition(FloatRectangle floatRectangle);

}
