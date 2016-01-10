package jsettlers.lookandfeel;

import jsettlers.lookandfeel.factory.*;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.*;

/**
 * Look and Feel for JSettlers
 * 
 * @author Andreas Butti
 *
 */
public class SettlerLookAndFeel {

	/**
	 * Install the look and feel components
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

		// Forward calls which are not handled by our UI
		// UIManager.addAuxiliaryLookAndFeel(laf);
		// doesn't work as expected...

		ButtonUiFactory.FORWARD.loadFromType("ButtonUI");
		TextFieldUiFactory.FORWARD.loadFromType("TextFieldUI");
		ButtonUiFactory.FORWARD.loadFromType("ButtonUI");
		LabelUiFactory.FORWARD.loadFromType("LabelUI");
		// Panel handles all UI types
		// PanelUiFactory.FORWARD.loadFromType("PanelUI");
		// ScrollPane handles all UI types
		// ScrollPaneUiFactory.FORWARD.loadFromType("ScrollPaneUI");

		Object[] scrollbar = {
				// "ScrollBarUI", ScrollBarUiFactory.class.getName()
				"BackgroundPanelUI", BackgroundPanelUiFactory.class.getName(),
				"TextFieldUI", TextFieldUiFactory.class.getName(),
				"ButtonUI", ButtonUiFactory.class.getName(),
				"LabelUI", LabelUiFactory.class.getName(),
				"PanelUI", PanelUiFactory.class.getName(),
				// "CheckBoxUI", metalPackageName + "MetalCheckBoxUI",
				// "ComboBoxUI", metalPackageName + "MetalComboBoxUI",
				// "LabelUI", metalPackageName + "MetalLabelUI",
				"ScrollPaneUI", ScrollPaneUiFactory.class.getName(),
		};
		UIManager.getDefaults().putDefaults(scrollbar);

		// Map Cell renderer
		UIManager.put("MapListCellRenderer.backgroundColor1", new Color(0xff, 0xff, 0xff, 40));
		UIManager.put("MapListCellRenderer.backgroundColor2", new Color(0, 0, 0, 60));
		UIManager.put("MapListCellRenderer.backgroundSelected", new Color(0xff, 0xff, 0, 80));
		UIManager.put("MapListCellRenderer.foregroundColor", Color.WHITE);

		// Search Field
		UIManager.put("ClearSearchIcon.foregroundColor", Color.WHITE);
		UIManager.put("ClearSearchIcon.backgroundColor", null);
		UIManager.put("ClearSearchIcon.backgroundColorHover", null);
	}

}
