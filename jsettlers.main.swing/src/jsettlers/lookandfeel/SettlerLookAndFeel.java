package jsettlers.lookandfeel;

import java.awt.Color;

import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import jsettlers.lookandfeel.factory.BackgroundPanelUiFactory;
import jsettlers.lookandfeel.factory.ButtonUiFactory;
import jsettlers.lookandfeel.factory.LabelUiFactory;
import jsettlers.lookandfeel.factory.PanelUiFactory;
import jsettlers.lookandfeel.factory.ScrollPaneUiFactory;

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
	 * Install the look and feel
	 * 
	 * @throws UnsupportedLookAndFeelException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public static void install() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				UIManager.setLookAndFeel(info.getClassName());
				break;
			}
		}

		UIManager.addAuxiliaryLookAndFeel(new SettlerLookAndFeel());

		UIManager.put("MapListCellRenderer.backgroundColor1", new Color(0xff, 0xff, 0xff, 40));
		UIManager.put("MapListCellRenderer.backgroundColor2", new Color(0, 0, 0, 60));
		UIManager.put("MapListCellRenderer.backgroundSelected", new Color(0xff, 0xff, 0, 80));
		UIManager.put("MapListCellRenderer.foregroundColor", Color.WHITE);
	}

	/**
	 * Constructor, use <code>install</code> to install
	 */
	protected SettlerLookAndFeel() {
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
				"ScrollPaneUI", ScrollPaneUiFactory.class.getName(),
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
