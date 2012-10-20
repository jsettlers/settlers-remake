package jsettlers.graphics.androidui;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.utils.Button;
import jsettlers.graphics.utils.UIPanel;

public class NormalMobileMenu extends UIPanel implements MobileMenu {

	private String name;

	protected NormalMobileMenu(String name) {
		this.name = name;
	}

	@Override
	public Action getActionFor(UIPoint position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void drawBackground(GLDrawContext gl) {
		FloatRectangle pos = getPosition();

		gl.fillQuad(pos.getMinX(), pos.getMinY(), pos.getMaxX(), pos.getMaxY());
	}

	/**
	 * 
	 * @param image The image to use
	 * @param width The width in percent
	 * @param height The height to use.
	 */
	public void addButton(Button button, float width, float height) {
		
	}

	@Override
    public void show() {
    }

	@Override
    public void hide() {
    }
	
}
