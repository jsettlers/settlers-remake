package jsettlers.graphics.map.controls.original;

import go.graphics.GLDrawContext;

import java.awt.Point;

import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.IntRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ChangePanelAction;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PanToAction;
import jsettlers.graphics.map.MapDrawContext;
import jsettlers.graphics.map.controls.IControls;
import jsettlers.graphics.map.controls.original.panel.MainPanel;
import jsettlers.graphics.map.minimap.Minimap;
import jsettlers.graphics.utils.UIPanel;

public class OriginalControls implements IControls {

	private UIPanel uiBase;

	private Minimap minimap;

	private MainPanel mainPanel = new MainPanel();

	private IOriginalConstants constants;

	public OriginalControls(MapDrawContext context) {
		minimap = new Minimap(context);
		constants = new SmallOriginalConstants();
		uiBase = createInterface();
		mainPanel.useConstants(constants);
	}

	private UIPanel createInterface() {
		UIPanel panel = new UIPanel();

		UIPanel minimapbg_left = new UIPanel();
		minimapbg_left.setBackground(constants.UI_BG_SEQ_MINIMAPL);
		panel.addChild(minimapbg_left, 0, constants.UI_CENTERY,
		        constants.UI_CENTERX, 1);

		UIPanel minimapbg_right = new UIPanel();
		minimapbg_right.setBackground(constants.UI_BG_SEQ_MINIMAPR);
		panel.addChild(minimapbg_right, constants.UI_CENTERX,
		        constants.UI_CENTERY, 1, 1);

		mainPanel.setBackground(constants.UI_BG_SEQ_MAIN);
		panel.addChild(mainPanel, 0, 0, constants.UI_CENTERX,
		        constants.UI_CENTERY);

		UIPanel rightDecoration = new UIPanel();
		rightDecoration.setBackground(constants.UI_BG_SEQ_RIGHT);
		panel.addChild(rightDecoration, constants.UI_CENTERX, 0,
		        constants.UI_DECORATIONRIGHT, constants.UI_CENTERY);

		return panel;
	}

	@Override
	public void resizeTo(int newWidth, int newHeight) {
		IOriginalConstants newConstants;
		if (newHeight <= 480) {
			newConstants = new SmallOriginalConstants();
		} else {
			// TODO...
			newConstants = new SmallOriginalConstants();
		}
		if (!newConstants.equals(constants)) {
			constants = newConstants;
			uiBase = createInterface();
			mainPanel.useConstants(constants);
		}
		int width = (int) (newHeight / constants.UI_RATIO);
		this.uiBase.setPosition(new IntRectangle(0, 0, width, newHeight));

		minimap.setSize(
		        (int) (constants.MINIMAP_WIDTH * width),
		        (int) (constants.MINIMAP_HEIGHT * (1 - constants.UI_CENTERY) * newHeight));
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		uiBase.drawAt(gl);
		gl.glPushMatrix();
		gl.glTranslatef(getMinimapLeft(), getMinimapBottom(), 0);
		minimap.draw(gl);
		gl.glPopMatrix();
	}

	private float getMinimapLeft() {
		return constants.MINIMAP_BOTTOMLEFT_X * uiBase.getPosition().getWidth();
	}

	private float getMinimapBottom() {
		return (constants.UI_CENTERY + (1 - constants.UI_CENTERY)
		        * constants.MINIMAP_BOTTOM_Y)
		        * uiBase.getPosition().getHeight();
	}

	@Override
	public boolean containsPoint(Point position) {
		int width = uiBase.getPosition().getWidth();
		int height = uiBase.getPosition().getHeight();
		float uicenter = width * constants.UI_CENTERX;
		float m =
		        1 / (1 - constants.UI_CENTERX) / width
		                * (1 - constants.UI_CENTERY) * height;
		return position.getX() < uicenter
		        || position.getY() > position.getX() * m + constants.UI_CENTERY
		                * height - m * constants.UI_CENTERX * width;
	}

	@Override
	public Action getActionFor(Point position) {
		float relativex =
		        (float) position.getX() / this.uiBase.getPosition().getWidth();
		float relativey =
		        (float) position.getY() / this.uiBase.getPosition().getHeight();
		Action action;
		if (relativey > constants.UI_CENTERY) {
			action = getForMinimap(relativex, relativey);
		} else {
			action = uiBase.getAction(relativex, relativey);
		}
		if (action != null
		        && action.getActionType() == EActionType.CHANGE_PANEL) {
			// TODO; can we fire the action and catch it later?
			mainPanel.setContent(((ChangePanelAction) action).getContent());
			return null;
		} else {
			return action;
		}
	}

	private Action getForMinimap(float relativex, float relativey) {
		float minimapx =
		        (relativex - constants.MINIMAP_BOTTOMLEFT_X)
		                / constants.MINIMAP_WIDTH;
		float minimapy =
		        ((relativey - constants.UI_CENTERY)
		                / (1 - constants.UI_CENTERY) - constants.MINIMAP_BOTTOM_Y)
		                / constants.MINIMAP_HEIGHT;
		ISPosition2D clickPosition = minimap.getClickPosition(minimapx, minimapy);
		return new PanToAction(clickPosition);
	}

	@Override
	public String getDescriptionFor(Point position) {
		float relativex =
		        (float) position.getX() / this.uiBase.getPosition().getWidth();
		float relativey =
		        (float) position.getY() / this.uiBase.getPosition().getHeight();
		return uiBase.getDescription(relativex, relativey);
	}

	@Override
	public void setMapViewport(MapRectangle screenArea) {
		minimap.setMapViewport(screenArea);
	}
}
