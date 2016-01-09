package jsettlers.lookandfeel;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import jsettlers.lookandfeel.factory.BackgroundPanelUiFactory;
import jsettlers.lookandfeel.factory.ButtonUiFactory;
import jsettlers.lookandfeel.factory.LabelUiFactory;
import jsettlers.lookandfeel.factory.PanelUiFactory;

/**
 * Look and Feel for JSettlers
 * 
 * @author Andreas Butti
 *
 */
public class SettlerLookAndFeel extends LookAndFeel {

	/**
	 * Table with the defaults
	 */
	private UIDefaults defaults = new UIDefaults() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void getUIError(String msg) {
			// ignore errors, not all UIs are implemented, the default are handled by Nimbus
		};
	};

	/**
	 * Constructor
	 */
	public SettlerLookAndFeel() {
		Object[] scrollbar = {
				// "ScrollBarUI", ScrollBarUiFactory.class.getName()
				"BackgroundPanelUI", BackgroundPanelUiFactory.class.getName(),
		};
		UIManager.getDefaults().putDefaults(scrollbar);

		Object[] uiDefaults = {
				"ButtonUI", ButtonUiFactory.class.getName(),
				"LabelUI", LabelUiFactory.class.getName(),
				"PanelUI", PanelUiFactory.class.getName(),
				// "CheckBoxUI", metalPackageName + "MetalCheckBoxUI",
				// "ComboBoxUI", metalPackageName + "MetalComboBoxUI",
				// "LabelUI", metalPackageName + "MetalLabelUI",
				// "ScrollPaneUI", metalPackageName + "MetalScrollPaneUI",
				// "TextFieldUI", metalPackageName + "MetalTextFieldUI",

		};
		defaults.putDefaults(uiDefaults);
	}

	@Override
	public UIDefaults getDefaults() {
		return defaults;
	}

	@Override
	public String getName() {
		return "Settler";
	}

	@Override
	public String getID() {
		return "Settler";
	}

	@Override
	public String getDescription() {
		return "JSettler Look and Feel";
	}

	@Override
	public boolean isNativeLookAndFeel() {
		return false;
	}

	@Override
	public boolean isSupportedLookAndFeel() {
		return true;
	}

}
