/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.map.controls.original.panel;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.AskSetTradingWaypointAction;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.action.SetTradingWaypointAction;
import jsettlers.graphics.action.SetTradingWaypointAction.WaypointType;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.ControlPanelLayoutProperties;
import jsettlers.graphics.map.controls.original.panel.content.AbstractContentProvider;
import jsettlers.graphics.map.controls.original.panel.content.ContentType;
import jsettlers.graphics.map.controls.original.panel.content.ESecondaryTabType;
import jsettlers.graphics.map.controls.original.panel.content.MessageContent;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.UIPanel;

/**
 * This class handles the contents of the main panel.
 *
 * @author michael
 */
public class MainPanel extends UIPanel {
	public static final int BUTTONS_FILE = 3;

	private final UIPanel tabpanel = new UIPanel();

	private final Button button_build = new TabButton(ContentType.BUILD_NORMAL, BUTTONS_FILE, 51, 60, "");
	private final Button button_goods = new TabButton(ContentType.STOCK, BUTTONS_FILE, 54, 63, "");
	private final Button button_settlers = new TabButton(ContentType.SETTLERSTATISTIC, BUTTONS_FILE, 57, 66, "");

	private final TabButton[] buildButtons = new TabButton[] {
			new TabButton(ContentType.BUILD_NORMAL, BUTTONS_FILE, 69, 81, ""),
			new TabButton(ContentType.BUILD_FOOD, BUTTONS_FILE, 72, 84, ""),
			new TabButton(ContentType.BUILD_MILITARY, BUTTONS_FILE, 75, 87, ""),
			new TabButton(ContentType.BUILD_SOCIAL, BUTTONS_FILE, 78, 90, ""),
	};

	private final TabButton[] goodsButtons = new TabButton[] {
			new TabButton(ContentType.STOCK, BUTTONS_FILE, 258, 270, ""),
			new TabButton(ContentType.TOOLS, BUTTONS_FILE, 261, 273, ""),
			new TabButton(ContentType.GOODS_SPREAD, BUTTONS_FILE, 264, 276, ""),
			new TabButton(ContentType.GOODS_TRANSPORT, BUTTONS_FILE, 267, 279, ""),
	};

	private final TabButton[] settlerButtons = new TabButton[] {
			new TabButton(ContentType.SETTLERSTATISTIC, BUTTONS_FILE, 234, 246, ""),
			new TabButton(ContentType.PROFESSION, BUTTONS_FILE, 237, 249, ""),
			new TabButton(ContentType.WARRIORS, BUTTONS_FILE, 240, 252, ""),
			new TabButton(ContentType.PRODUCTION, BUTTONS_FILE, 243, 255, ""),
	};

	private final MessageContent quitPrompt = new MessageContent(
			Labels.getString("game-quit"),
			Labels.getString("game-quit-cancel"),
			new ExecutableAction() {
				@Override
				public void execute() {
					setContent(ContentType.BUILD_NORMAL);
					btnSystem.setActive(false);
				}
			},
			Labels.getString("game-quit-ok"),
			new Action(EActionType.EXIT)) {
		@Override
		public void contentShowing(ActionFireable actionFireable) {
			btnSystem.setActive(true);
		}

		@Override
		public void contentHiding(ActionFireable actionFireable, AbstractContentProvider nextContent) {
			btnSystem.setActive(false);
		}
	};

	private final Button btnSystem = new TabButton(quitPrompt,
			new OriginalImageLink(EImageLinkType.GUI, BUTTONS_FILE, 93, 0),
			new OriginalImageLink(EImageLinkType.GUI, BUTTONS_FILE, 96, 0), "game-quit-description");

	private final Button btnScroll = new TabButton(ContentType.EMPTY, BUTTONS_FILE, 111, 99, "");
	private final Button btnSwords = new TabButton(ContentType.EMPTY, BUTTONS_FILE, 114, 102, "");
	private final Button btnSignPost = new TabButton(ContentType.EMPTY, BUTTONS_FILE, 117, 105, "");
	private final Button btnPots = new TabButton(ContentType.EMPTY, BUTTONS_FILE, 120, 108, "");

	{
		btnScroll.setActive(true);
		btnSwords.setActive(true);
		btnSignPost.setActive(true);
		btnPots.setActive(true);
	}

	private final UIPanel contentContainer = new UIPanel();

	private ControlPanelLayoutProperties constants;

	private AbstractContentProvider activeContent = ContentType.EMPTY;

	private AbstractContentProvider goBackContent;

	private IGraphicsGrid grid;

	private ShortPoint2D displayCenter;

	/**
	 * Somewhere to fire actions to.
	 */
	private ActionFireable actionFireable;

	public MainPanel(ActionFireable actionFireable, IInGamePlayer player) {
		this.actionFireable = actionFireable;
		ContentType.WARRIORS.setPlayer(player);

		layoutPanel(ControlPanelLayoutProperties.getLayoutPropertiesFor(480));
	}

	public void setContent(AbstractContentProvider type) {
		activeContent.contentHiding(actionFireable, type);

		ESecondaryTabType tabs = type.getTabs();
		showSecondaryTabs(tabs);

		if (tabs != null) {
			switch (tabs) {
			case BUILD:
				setButtonsActive(buildButtons, type);
				break;
			case GOODS:
				setButtonsActive(goodsButtons, type);
				break;
			case SETTLERS:
				setButtonsActive(settlerButtons, type);
				break;
			case NONE:
			default:
				break;
			}
		}

		contentContainer.removeAll();
		contentContainer.addChild(type.getPanel(), 0, 0, 1, 1);
		activeContent = type;

		sendMapPositionChange();

		activeContent.contentShowing(actionFireable);
	}

	private void setButtonsActive(TabButton[] buttons, AbstractContentProvider type) {
		for (TabButton button : buttons) {
			button.setActiveByContent(type);
		}
	}

	private void showSecondaryTabs(ESecondaryTabType tabs) {
		tabpanel.removeAll();
		if (tabs != null) {
			switch (tabs) {
			case BUILD:
				addTabpanelButtons(buildButtons);
				break;
			case GOODS:
				addTabpanelButtons(goodsButtons);
				break;
			case SETTLERS:
				addTabpanelButtons(settlerButtons);
				break;
			case NONE:
			default:
				break;
			}
		}

		button_build.setActive(tabs == ESecondaryTabType.BUILD);
		button_goods.setActive(tabs == ESecondaryTabType.GOODS);
		button_settlers.setActive(tabs == ESecondaryTabType.SETTLERS);
	}

	private void addTabpanelButtons(Button[] buttons) {
		int i = 0;
		for (Button button : buttons) {
			float left = constants.SECONDARY_TABS_SIDEMARGIN + i * (constants.SECONDARY_TABS_WIDTH + constants.SECONDARY_TABS_SPACING);
			tabpanel.addChild(button, left, 0, left + constants.SECONDARY_TABS_WIDTH, 1);
			i++;
		}
	}

	/**
	 * Position the graphical components of this panel using the specified layout properties.
	 *
	 * @param layoutProperties
	 */
	public void layoutPanel(ControlPanelLayoutProperties layoutProperties) {
		this.constants = layoutProperties;
		this.removeAll();
		initTabbar1();
		initTabbar2();
		addSystemButton();
		addLowerTabBar();
		this.addChild(contentContainer, layoutProperties.CONTENT_LEFT, layoutProperties.CONTENT_BOTTOM, layoutProperties.CONTENT_RIGHT,
				layoutProperties.CONTENT_TOP);
		setContent(activeContent);
	}

	private void initTabbar1() {
		int i = 0;
		UIPanel tabbar1 = new UIPanel();
		this.addChild(tabbar1, 0, constants.PRIMARY_TABS_BOTTOM, 1, constants.PRIMARY_TABS_TOP);
		Button[] buttons = new Button[] { button_build, button_goods, button_settlers };
		for (Button button : buttons) {
			float left = constants.PRIMARY_TABS_SIDEMARGIN + i * (constants.PRIMARY_TABS_WIDTH + constants.PRIMARY_TABS_SPACING);
			tabbar1.addChild(button, left, 0, left + constants.PRIMARY_TABS_WIDTH, 1);
			i++;
		}
	}

	private void initTabbar2() {
		this.addChild(tabpanel, 0, constants.SECONDARY_TABS_BOTTOM, 1, constants.SECONDARY_TABS_TOP);
	}

	private void addSystemButton() {
		this.addChild(
				btnSystem,
				constants.SYSTEM_BUTTON_LEFT,
				constants.SYSTEM_BUTTON_BOTTOM,
				constants.SYSTEM_BUTTON_RIGHT,
				constants.SYSTEM_BUTTON_TOP);
	}

	private void addLowerTabBar() {
		UIPanel lowerTabBar = new UIPanel();
		Button[] buttons = new Button[] { btnScroll, btnSwords, btnSignPost, btnPots };
		int i = 0;
		for (Button button : buttons) {
			float left = constants.LOWER_TABS_LEFT + (i++ * constants.LOWER_TABS_WIDTH);
			lowerTabBar.addChild(button, left, 0, left + constants.LOWER_TABS_WIDTH, 1);
		}
		this.addChild(lowerTabBar, 0, constants.LOWER_TABS_BOTTOM, 1, constants.LOWER_TABS_TOP);
	}

	/**
	 * A special panel that si displayed to the user while the user should select a point.
	 * 
	 * @author Michael Zangl
	 */
	public static class SelectPointMessage extends MessageContent {
		public SelectPointMessage(String message) {
			super(message, null, null,
					Labels.getString("abort"), new Action(EActionType.ABORT));
		}

		@Override
		public boolean isForSelection() {
			return true;
		}
	}

	public Action catchAction(Action action) {
		action = activeContent.catchAction(action);
		// TODO: Abort on MOVE_TO-action.
		EActionType type = action.getActionType();
		switch (type) {
		case MOVE_TO:
		case SET_DOCK:
		case SET_TRADING_WAYPOINT:
		case SET_WORK_AREA:
			if (activeContent instanceof SelectPointMessage) {
				goBack();
			}
			return action;
		case ASK_SET_DOCK:
			goBackContent = activeContent;
			setContent(new SelectPointMessage(
					Labels.getString("click_set_dock")) {
				@Override
				public PointAction getSelectAction(ShortPoint2D position) {
					return new PointAction(EActionType.SET_DOCK, position);
				}
			});
			return null;
		case ASK_SET_WORK_AREA:
			goBackContent = activeContent;
			setContent(new SelectPointMessage(
					Labels.getString("click_set_workcenter")) {
				@Override
				public PointAction getSelectAction(ShortPoint2D position) {
					return new PointAction(EActionType.SET_WORK_AREA, position);
				}
			});
			return null;
		case ASK_SET_TRADING_WAYPOINT:
			goBackContent = activeContent;
			final WaypointType wp = ((AskSetTradingWaypointAction) action).getWaypoint();
			setContent(new SelectPointMessage(
					Labels.getString("click_set_trading_waypoint_" + wp)) {
				@Override
				public PointAction getSelectAction(ShortPoint2D position) {
					return new SetTradingWaypointAction(wp, position);
				}
			});
			return null;
		case ASK_DESTROY:
			goBackContent = activeContent;
			setContent(new MessageContent(
					Labels.getString("really_destroy_building"),
					Labels.getName(EActionType.DESTROY), new Action(
							EActionType.DESTROY),
					Labels.getString("abort"),
					new Action(EActionType.ABORT)) {
				@Override
				public boolean isForSelection() {
					return true;
				}
			});
			return null;
		case ABORT:
			goBack();
			return action;
		case EXECUTABLE:
			((ExecutableAction) action).execute();
			return null;
		default:
			return action;
		}
	}

	private void goBack() {
		if (goBackContent != null) {
			setContent(goBackContent);
			goBackContent = null;
		} else {
			setContent(ContentType.EMPTY);
		}
	}

	public void setMapViewport(MapRectangle screenArea, IGraphicsGrid grid) {
		this.grid = grid;
		displayCenter = new ShortPoint2D(screenArea.getLineStartX(screenArea.getLines() / 2)
				+ screenArea.getLineLength() / 2, screenArea
						.getLineY(screenArea.getLines() / 2));
		sendMapPositionChange();
	}

	private void sendMapPositionChange() {
		if (displayCenter != null) {
			activeContent.showMapPosition(displayCenter, grid);
		}
	}

	public boolean isSelectionActive() {
		return activeContent.isForSelection();
	}
}
