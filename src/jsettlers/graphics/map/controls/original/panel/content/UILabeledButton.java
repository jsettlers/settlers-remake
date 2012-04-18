package jsettlers.graphics.map.controls.original.panel.content;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.utils.UIPanel;

public class UILabeledButton extends UIPanel {
	private final String text;
	private final Action action;

	private static final OriginalImageLink BUTTON = new OriginalImageLink(EImageLinkType.GUI,
	        3, 324, 0);
	private static final OriginalImageLink BUTTON_ACTIVE = new OriginalImageLink(
	        EImageLinkType.GUI, 3, 327, 0);
	private final EFontSize size;
	
	public UILabeledButton(String text, Action action, EFontSize size) {
		this.size = size;
		this.text = text;
		this.action = action;
		setActive(false);
	}
	
	public UILabeledButton(String text, Action action) {
		this(text, action, EFontSize.NORMAL);
	}
	
	@Override
	public void drawAt(GLDrawContext gl) {
	    super.drawAt(gl);
	    
	    TextDrawer drawer = gl.getTextDrawer(size);
	    drawer.renderCentered(getPosition().getCenterX(), getPosition().getCenterY(), text);
	}
	
	@Override
	public Action getAction(float relativex, float relativey) {
	    return action;
	}

	public void setActive(boolean b) {
	    if (b) {
			setBackground(BUTTON_ACTIVE);
	    } else {
			setBackground(BUTTON);
	    }
    }
}
