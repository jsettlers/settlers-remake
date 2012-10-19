package jsettlers.graphics.map.controls.small;

import go.graphics.GLDrawContext;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.utils.Button;

/**
 * This is a button that looks like a tab when beeing active and fades when
 * being inactive.
 * 
 * @author michael
 */
public class TabableButton extends Button {

	public TabableButton(Action action, OriginalImageLink image,
            String description) {
	    super(action, image, image, description);
    }
	
	@Override
	public void drawAt(GLDrawContext gl) {
	    super.drawAt(gl);
	}
	
}
