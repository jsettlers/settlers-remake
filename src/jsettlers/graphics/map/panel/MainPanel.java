package jsettlers.graphics.map.panel;

import jsettlers.graphics.map.panel.content.EContentType;
import jsettlers.graphics.map.panel.content.ESecondaryTabType;
import jsettlers.graphics.utils.Button;
import jsettlers.graphics.utils.UIPanel;

/**
 * This class handles the contents of the main panel.
 * 
 * @author michael
 */
public class MainPanel extends UIPanel {
	// relative to main content
	private static final float UI_TABS1_TOP = 1 - (float) 0 / 544;
	private static final float UI_TABS1_BOTTOM = UI_TABS1_TOP - (float) 55
	        / 544;
	private static final float UI_TABS1_SIDEMARGIN = (float) 25 / 216;

	private static final float UI_TABS2_TOP = UI_TABS1_BOTTOM;
	private static final float UI_TABS2_BOTTOM = UI_TABS2_TOP - (float) 40
	        / 544;
	private static final float UI_TABS2_SIDEMARGIN = (float) 25 / 216;

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

	public MainPanel() {
		initTabbar1();
		initTabbar2();
		this.addChild(contentContainer, 0, .1f, 1, .7f);
		setContent(EContentType.BUILD_NORMAL);
	}

	private void initTabbar2() {
		this.addChild(tabpanel, UI_TABS2_SIDEMARGIN, UI_TABS2_BOTTOM,
		        1 - UI_TABS2_SIDEMARGIN, UI_TABS2_TOP);
	}

	private void initTabbar1() {
		int i = 0;
		UIPanel tabbar1 = new UIPanel();
		this.addChild(tabbar1, UI_TABS1_SIDEMARGIN, UI_TABS1_BOTTOM,
		        1 - UI_TABS1_SIDEMARGIN, UI_TABS1_TOP);
		Button[] buttons = new Button[] {
		        button_build, button_settlers, button_goods
		};
		for (Button button : buttons) {
			tabbar1.addChild(button, (float) i / buttons.length, 0,
			        (float) (i + 1) / buttons.length, 1);
			i++;
		}

	}

	public void setContent(EContentType type) {
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
	}

	private void setButtonsActive(TabButton[] buttons, EContentType type) {
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
			tabpanel.addChild(button, (float) i / buttons.length, 0,
			        (float) (i + 1) / buttons.length, 1);
			i++;
		}
	}
}
