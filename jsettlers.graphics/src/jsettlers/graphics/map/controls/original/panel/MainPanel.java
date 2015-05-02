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

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.ControlPanelLayoutProperties;
import jsettlers.graphics.map.controls.original.panel.content.EContentType;
import jsettlers.graphics.map.controls.original.panel.content.ESecondaryTabType;
import jsettlers.graphics.map.controls.original.panel.content.IContentProvider;
import jsettlers.graphics.map.controls.original.panel.content.MessageContent;
import jsettlers.graphics.utils.Button;
import jsettlers.graphics.utils.UIPanel;

/**
 * This class handles the contents of the main panel.
 * 
 * @author michael
 */
public class MainPanel extends UIPanel {
	public static final int BUTTONS_FILE = 3;

	private final UIPanel tabpanel = new UIPanel();

	private final Button button_build = new TabButton(EContentType.BUILD_NORMAL, BUTTONS_FILE, 51, 60, "");
	private final Button button_goods = new TabButton(EContentType.STOCK, BUTTONS_FILE, 54, 63, "");
	private final Button button_settlers = new TabButton(EContentType.SETTLERSTATISTIC, BUTTONS_FILE, 57, 66, "");

	private final TabButton[] buildButtons = new TabButton[] {
			new TabButton(EContentType.BUILD_NORMAL, BUTTONS_FILE, 69, 81, ""),
			new TabButton(EContentType.BUILD_FOOD, BUTTONS_FILE, 72, 84, ""),
			new TabButton(EContentType.BUILD_MILITARY, BUTTONS_FILE, 75, 87, ""),
			new TabButton(EContentType.BUILD_SOCIAL, BUTTONS_FILE, 78, 90, ""),
	};

	private final TabButton[] goodsButtons = new TabButton[] {
			new TabButton(EContentType.STOCK, BUTTONS_FILE, 258, 270, ""),
			new TabButton(EContentType.TOOLS, BUTTONS_FILE, 261, 273, ""),
			new TabButton(EContentType.GOODS_SPREAD, BUTTONS_FILE, 264, 276, ""),
			new TabButton(EContentType.GOODS_TRANSPORT, BUTTONS_FILE, 267, 279, ""),
	};

	private final TabButton[] settlerButtons = new TabButton[] {
			new TabButton(EContentType.SETTLERSTATISTIC, BUTTONS_FILE, 234, 246, ""),
			new TabButton(EContentType.PROFESSION, BUTTONS_FILE, 237, 249, ""),
			new TabButton(EContentType.WARRIORS, BUTTONS_FILE, 240, 252, ""),
			new TabButton(EContentType.PRODUCTION, BUTTONS_FILE, 243, 255, ""),
	};

	private final Button btnSystem = new TabButton(EContentType.EMPTY, BUTTONS_FILE, 93, 96, "");
	{
		btnSystem.setActive(true); // Show as inactive until the functionality has been implemented
	}

	private final Button btnScroll = new TabButton(EContentType.EMPTY, BUTTONS_FILE, 111, 99, "");
	private final Button btnSwords = new TabButton(EContentType.EMPTY, BUTTONS_FILE, 114, 102, "");
	private final Button btnSignPost = new TabButton(EContentType.EMPTY, BUTTONS_FILE, 117, 105, "");
	private final Button btnPots = new TabButton(EContentType.EMPTY, BUTTONS_FILE, 120, 108, "");
	{
		btnScroll.setActive(true);
		btnSwords.setActive(true);
		btnSignPost.setActive(true);
		btnPots.setActive(true);
	}

	private final UIPanel contentContainer = new UIPanel();

	private ControlPanelLayoutProperties constants;

	private IContentProvider activeContent = EContentType.BUILD_NORMAL;

	private EBuildingType activeBuilding;

	private IContentProvider goBackContent;

	/**
	 * The action type the next simple select action should be replaced with.
	 * <p>
	 * This field is reset on every content change.
	 */
	private EActionType selectAction;

	private IGraphicsGrid grid;

	private ShortPoint2D displayCenter;

	public MainPanel() {
		layoutPanel(ControlPanelLayoutProperties.getLayoutPropertiesFor(480));
	}

	private void initTabbar1() {
		int i = 0;
		UIPanel tabbar1 = new UIPanel();
		this.addChild(tabbar1, 0, constants.UI_TABS1_BOTTOM, 1, constants.UI_TABS1_TOP);
		Button[] buttons = new Button[] { button_build, button_goods, button_settlers };
		for (Button button : buttons) {
			float left = constants.UI_TABS1_SIDEMARGIN + i * (constants.UI_TABS1_WIDTH + constants.UI_TABS1_SPACING);
			tabbar1.addChild(button, left, 0, left + constants.UI_TABS1_WIDTH, 1);
			i++;
		}
	}

	private void initTabbar2() {
		this.addChild(tabpanel, 0, constants.UI_TABS2_BOTTOM, 1, constants.UI_TABS2_TOP);
	}

	private void addSystemButton() {
		this.addChild(
				btnSystem,
				constants.SYSTEM_BUTTON_LEFT,
				constants.SYSTEM_BUTTON_BOTTOM,
				constants.SYSTEM_BUTTON_RIGHT,
				constants.SYSTEM_BUTTON_TOP
				);
	}

	private void addLowerTabBar()
	{
		UIPanel lowerTabBar = new UIPanel();
		Button[] buttons = new Button[] { btnScroll, btnSwords, btnSignPost, btnPots };
		int i = 0;
		for (Button button : buttons) {
			float left = constants.LOWER_TABS_LEFT + (i++ * constants.LOWER_TABS_WIDTH);
			lowerTabBar.addChild(button, left, 0, left + constants.LOWER_TABS_WIDTH, 1);
		}
		this.addChild(lowerTabBar, 0, constants.LOWER_TABS_BOTTOM, 1, constants.LOWER_TABS_TOP);
	}

	public void setContent(IContentProvider type) {
		showSecondaryTabs(type.getTabs());

		switch (type.getTabs()) {
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

		contentContainer.removeAll();
		contentContainer.addChild(type.getPanel(), 0, 0, 1, 1);
		activeContent = type;

		activeContent.displayBuildingBuild(activeBuilding);
		selectAction = null;
		sendMapPositionChange();
	}

	private void setButtonsActive(TabButton[] buttons, IContentProvider type) {
		for (TabButton button : buttons) {
			button.setActiveByContent(type);
		}
	}

	private void showSecondaryTabs(ESecondaryTabType tabs) {
		tabpanel.removeAll();
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

		button_build.setActive(tabs == ESecondaryTabType.BUILD);
		button_goods.setActive(tabs == ESecondaryTabType.GOODS);
		button_settlers.setActive(tabs == ESecondaryTabType.SETTLERS);
	}

	private void addTabpanelButtons(Button[] buttons) {
		int i = 0;
		for (Button button : buttons) {
			float left = constants.UI_TABS2_SIDEMARGIN + i * (constants.UI_TABS2_WIDTH + constants.UI_TABS2_SPACING);
			tabpanel.addChild(button, left, 0, left + constants.UI_TABS2_WIDTH, 1);
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

	public void displayBuildingBuild(EBuildingType type) {
		activeBuilding = type;
		activeContent.displayBuildingBuild(type);
	}

	public Action catchAction(Action action) {
		if (action.getActionType() == EActionType.ASK_SET_WORK_AREA) {
			goBackContent = activeContent;
			setContent(new MessageContent(
					Labels.getString("click_set_workcenter"), null, null,
					Labels.getString("abort"), new Action(EActionType.ABORT)));
			selectAction = EActionType.SET_WORK_AREA;
			return null;
		} else if (action.getActionType() == EActionType.ASK_DESTROY) {
			goBackContent = activeContent;
			setContent(new MessageContent(
					Labels.getString("really_destroy_building"),
					Labels.getName(EActionType.DESTROY), new Action(
							EActionType.DESTROY), Labels.getString("abort"),
					new Action(EActionType.ABORT)));
			return null;
		} else if (action.getActionType() == EActionType.SELECT_POINT
				&& selectAction != null) {
			ShortPoint2D position = ((PointAction) action).getPosition();
			PointAction replaced = new PointAction(selectAction, position);
			goBack();
			return replaced;
		} else if (action.getActionType() == EActionType.ABORT) {
			goBack();
			return action;
		} else if (action.getActionType() == EActionType.EXECUTABLE) {
			((ExecutableAction) action).execute();
			return null;
		} else {
			return action;
		}
	}

	private void goBack() {
		selectAction = null;
		if (goBackContent != null) {
			setContent(goBackContent);
			goBackContent = null;
		} else {
			setContent(EContentType.EMPTY);
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
}
