package jsettlers.graphics.map.controls.original.panel;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.controls.original.IOriginalConstants;
import jsettlers.graphics.map.controls.original.SmallOriginalConstants;
import jsettlers.graphics.map.controls.original.panel.content.EContentType;
import jsettlers.graphics.map.controls.original.panel.content.ESecondaryTabType;
import jsettlers.graphics.map.controls.original.panel.content.IContentProvider;
import jsettlers.graphics.utils.Button;
import jsettlers.graphics.utils.UIPanel;

/**
 * This class handles the contents of the main panel.
 * 
 * @author michael
 */
public class MainPanel extends UIPanel {

	private static final int BUTTONS_FILE = 3;

	private final UIPanel tabpanel = new UIPanel();

	private final Button button_build = new TabButton(
	        EContentType.BUILD_NORMAL, BUTTONS_FILE, 51, 60, "");
	private final Button button_settlers = new TabButton(EContentType.STOCK,
	        BUTTONS_FILE, 54, 63, "");
	private final Button button_goods = new TabButton(
	        EContentType.SETTLERSTATISTIC, BUTTONS_FILE, 57, 66, "");

	private final TabButton[] buildButtons =
	        new TabButton[] {
	        new TabButton(EContentType.BUILD_NORMAL, BUTTONS_FILE, 69, 81, ""),
	                new TabButton(EContentType.BUILD_FOOD, BUTTONS_FILE, 72,
	                        84, ""),
	                new TabButton(EContentType.BUILD_MILITARY, BUTTONS_FILE,
	                        75, 87, ""),
	                new TabButton(EContentType.BUILD_SOCIAL, BUTTONS_FILE, 78,
	                        90, ""),
	        };

	private final TabButton[] settlerButtons =
	        new TabButton[] {
	        new TabButton(EContentType.STOCK, BUTTONS_FILE, 234, 246, ""),
	                new TabButton(EContentType.TOOLS, BUTTONS_FILE, 237, 249,
	                        ""),
	                new TabButton(EContentType.GOODS_SPREAD, BUTTONS_FILE, 240,
	                        252, ""),
	                new TabButton(EContentType.GOODS_TRANSPORT, BUTTONS_FILE,
	                        243, 255, ""),
	        };

	private final TabButton[] goodsButtons = new TabButton[] {
	new TabButton(EContentType.SETTLERSTATISTIC, BUTTONS_FILE, 69, 81, ""),
	        new TabButton(EContentType.PROFESSION, BUTTONS_FILE, 72, 84, ""),
	        new TabButton(EContentType.WARRIORS, BUTTONS_FILE, 75, 87, ""),
	        new TabButton(EContentType.PRODUCTION, BUTTONS_FILE, 78, 90, ""),
	};

	private UIPanel contentContainer = new UIPanel();

	private IOriginalConstants constants;

	private IContentProvider activeContent;

	private EBuildingType activeBuilding;

	public MainPanel() {
		useConstants(new SmallOriginalConstants());
		setContent(EContentType.BUILD_NORMAL);
	}

	private void initTabbar2() {
		this.addChild(tabpanel, 0, constants.UI_TABS2_BOTTOM, 1,
		        constants.UI_TABS2_TOP);
	}

	private void initTabbar1() {
		int i = 0;
		UIPanel tabbar1 = new UIPanel();
		this.addChild(tabbar1, 0, constants.UI_TABS1_BOTTOM, 1,
		        constants.UI_TABS1_TOP);
		Button[] buttons = new Button[] {
		        button_build, button_settlers, button_goods
		};
		for (Button button : buttons) {
			float left =
			        constants.UI_TABS1_SIDEMARGIN
			                + i
			                * (constants.UI_TABS1_WIDTH + constants.UI_TABS1_SPACING);
			tabbar1.addChild(button, left, 0, left + constants.UI_TABS1_WIDTH,
			        1);
			i++;
		}

	}

	public void setContent(IContentProvider type) {
		showSecondaryTabs(type.getTabs());

		if (type.getTabs() == ESecondaryTabType.BUILD) {
			setButtonsActive(buildButtons, type);
		} else if (type.getTabs() == ESecondaryTabType.SETTLERS) {
			setButtonsActive(settlerButtons, type);
		} else if (type.getTabs() == ESecondaryTabType.GOODS) {
			setButtonsActive(goodsButtons, type);
		}

		contentContainer.removeAll();
		contentContainer.addChild(type.getPanel(), 0, 0, 1, 1);
		activeContent = type;

		IContextListener listener = activeContent.getContextListener();
		if (listener != null) {
			listener.displayBuildingBuild(activeBuilding);
		}
	}

	private void setButtonsActive(TabButton[] buttons, IContentProvider type) {
		for (TabButton button : buttons) {
			button.setActiveByContent(type);
		}
	}

	private void showSecondaryTabs(ESecondaryTabType tabs) {
		tabpanel.removeAll();
		if (tabs == ESecondaryTabType.BUILD) {
			addTabpanelButtons(buildButtons);
		} else if (tabs == ESecondaryTabType.SETTLERS) {
			addTabpanelButtons(settlerButtons);
		} else if (tabs == ESecondaryTabType.GOODS) {
			addTabpanelButtons(goodsButtons);
		}

		button_build.setActive(tabs == ESecondaryTabType.BUILD);
		button_settlers.setActive(tabs == ESecondaryTabType.SETTLERS);
		button_goods.setActive(tabs == ESecondaryTabType.GOODS);
	}

	private void addTabpanelButtons(Button[] buttons) {
		int i = 0;
		for (Button button : buttons) {
			float left =
			        constants.UI_TABS2_SIDEMARGIN
			                + i
			                * (constants.UI_TABS2_WIDTH + constants.UI_TABS2_SPACING);
			tabpanel.addChild(button, left, 0, left + constants.UI_TABS2_WIDTH,
			        1);
			i++;
		}
	}

	/**
	 * Resize everything according to constants.
	 * 
	 * @param constants
	 */
	public void useConstants(IOriginalConstants constants) {
		this.constants = constants;
		initTabbar1();
		initTabbar2();
		this.addChild(contentContainer, constants.CONTENT_LEFT,
		        constants.CONTENT_BOTTOM, constants.CONTENT_RIGHT,
		        constants.CONTENT_TOP);
	}

	public void displayBuildingBuild(EBuildingType type) {
		activeBuilding = type;
		IContextListener listener = activeContent.getContextListener();
		if (listener != null) {
			listener.displayBuildingBuild(type);
		}
	}

	public Action catchAction(Action action) {
	    return action;
    }
}
