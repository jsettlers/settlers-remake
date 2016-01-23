package jsettlers.lookandfeel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalScrollBarUI;

import jsettlers.lookandfeel.factory.BackgroundPanelUiFactory;
import jsettlers.lookandfeel.factory.ButtonUiFactory;
import jsettlers.lookandfeel.factory.LabelUiFactory;
import jsettlers.lookandfeel.factory.PanelUiFactory;
import jsettlers.lookandfeel.factory.ProgressBarUiFactory;
import jsettlers.lookandfeel.factory.ScrollPaneUiFactory;
import jsettlers.lookandfeel.factory.TextFieldUiFactory;
import jsettlers.lookandfeel.factory.ToggleButtonUiFactory;

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
		ToggleButtonUiFactory.FORWARD.loadFromType("ToggleButtonUI");
		ProgressBarUiFactory.FORWARD.loadFromType("ProgressBarUI");

		UIManager.put("ScrollBar.width", 20);
		UIManager.put("ScrollBar.shadow", new Color(0x394048));
		UIManager.put("ScrollBar.highlight", new Color(0x889CB2));
		UIManager.put("ScrollBar.darkShadow", new Color(0x394048));
		UIManager.put("ScrollBar.thumb", new Color(0x394048));
		UIManager.put("ScrollBar.thumbShadow", new Color(0x142333));
		UIManager.put("ScrollBar.thumbHighlight", new Color(0x6383A5));
		UIManager.put("ScrollBar.background", new Color(0x151E27));

		List<Object> scrollbarGradient = new ArrayList<>();
		scrollbarGradient.add(0.2f);
		scrollbarGradient.add(0.5f);
		scrollbarGradient.add(new Color(0x394048));
		scrollbarGradient.add(new Color(0x516377));
		scrollbarGradient.add(new Color(0x394048));
		UIManager.put("ScrollBar.gradient", scrollbarGradient);

		Object[] scrollbar = {
				"ScrollBarUI", MetalScrollBarUI.class.getName(),
				"BackgroundPanelUI", BackgroundPanelUiFactory.class.getName(),
				"TextFieldUI", TextFieldUiFactory.class.getName(),
				"ButtonUI", ButtonUiFactory.class.getName(),
				"LabelUI", LabelUiFactory.class.getName(),
				"PanelUI", PanelUiFactory.class.getName(),
				"ScrollPaneUI", ScrollPaneUiFactory.class.getName(),
				"ToggleButtonUI", ToggleButtonUiFactory.class.getName(),
				"ProgressBarUI", ProgressBarUiFactory.class.getName(),
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
