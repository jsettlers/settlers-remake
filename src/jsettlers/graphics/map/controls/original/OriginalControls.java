package jsettlers.graphics.map.controls.original;

import go.graphics.GLDrawContext;

import java.awt.Point;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.position.IntRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ChangePanelAction;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.controls.IControls;
import jsettlers.graphics.map.controls.original.panel.MainPanel;
import jsettlers.graphics.utils.UIPanel;

public class OriginalControls implements IControls {

	private final IGraphicsGrid map;

	private UIPanel uiBase;

	private MainPanel mainPanel = new MainPanel();

	private IOriginalConstants constants;

	public OriginalControls(IGraphicsGrid map) {
		this.map = map;
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
			//TODO...
			newConstants = new SmallOriginalConstants();
		}
		if (!newConstants.equals(constants)) {
			constants = newConstants;
			uiBase = createInterface();
			mainPanel.useConstants(constants);
		}
		this.uiBase.setPosition(new IntRectangle(0, 0,
		        (int) (newHeight / constants.UI_RATIO), newHeight));
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		uiBase.drawAt(gl);
	}

	@Override
	public boolean containsPoint(Point position) {
		float uicenter = uiBase.getPosition().getWidth() * constants.UI_CENTERX;
		return position.getX() < uicenter
		        && position.getY() < uiBase.getPosition().getHeight();
	}

	@Override
	public Action getActionFor(Point position) {
		float relativex =
		        (float) position.getX() / this.uiBase.getPosition().getWidth();
		float relativey =
		        (float) position.getY() / this.uiBase.getPosition().getHeight();
		Action action = uiBase.getAction(relativex, relativey);
		if (action.getActionType() == EActionType.CHANGE_PANEL) {
			// TODO. can we fire the action and catch it later?
			mainPanel.setContent(((ChangePanelAction) action).getContent());
			return null;
		} else {
			return action;
		}
	}
}
