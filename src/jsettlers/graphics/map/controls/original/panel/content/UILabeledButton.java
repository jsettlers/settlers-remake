package jsettlers.graphics.map.controls.original.panel.content;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import go.graphics.text.TextDrawer;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.utils.UIPanel;

public class UILabeledButton extends UIPanel {
	private final String text;
	private final Action action;

	public UILabeledButton(String text, Action action) {
		this.text = text;
		this.action = action;
		setBackground(new ImageLink(EImageLinkType.GUI, 3, 324, 0));
	}
	
	@Override
	public void drawAt(GLDrawContext gl) {
	    super.drawAt(gl);
	    
	    TextDrawer drawer = gl.getTextDrawer(EFontSize.NORMAL);
	    drawer.renderCentered(getPosition().getCenterX(), getPosition().getCenterY(), text);
	}
	
	@Override
	public Action getAction(float relativex, float relativey) {
	    return action;
	}
}
