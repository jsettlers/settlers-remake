package jsettlers.lookandfeel;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;

import jsettlers.lookandfeel.factory.ButtonUiFactory;
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
		Object[] uiDefaults = {
				"ButtonUI", ButtonUiFactory.class.getName(),
				"PanelUI", PanelUiFactory.class.getName(),
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
