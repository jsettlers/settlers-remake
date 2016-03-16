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
package jsettlers.lookandfeel;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import jsettlers.lookandfeel.factory.BackgroundPanelUiFactory;
import jsettlers.lookandfeel.factory.ButtonUiFactory;
import jsettlers.lookandfeel.factory.ComboboxUiFactory;
import jsettlers.lookandfeel.factory.LabelUiFactory;
import jsettlers.lookandfeel.factory.PanelUiFactory;
import jsettlers.lookandfeel.factory.ProgressBarUiFactory;
import jsettlers.lookandfeel.factory.ScrollPaneUiFactory;
import jsettlers.lookandfeel.factory.ScrollbarUiFactory;
import jsettlers.lookandfeel.factory.TextAreaUiFactory;
import jsettlers.lookandfeel.factory.TextFieldUiFactory;
import jsettlers.lookandfeel.factory.ToggleButtonUiFactory;
import jsettlers.lookandfeel.ui.UIDefaults;

/**
 * Look and Feel for JSettlers
 * 
 * @author Andreas Butti
 *
 */
public class SettlersLookAndFeel {

	/**
	 * Install the look and feel components
	 * 
	 * @throws UnsupportedLookAndFeelException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public static void install() throws JSettlersLookAndFeelExecption {
		try {
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
			TextAreaUiFactory.FORWARD.loadFromType("TextAreaUI");
			ComboboxUiFactory.FORWARD.loadFromType("ComboBoxUI");

			// Scrollbar
			UIManager.put("ScrollBar.width", 20);

			// ComboBox
			UIManager.put("ComboBox.background", Color.BLACK);
			UIManager.put("ComboBox.foreground", UIDefaults.LABEL_TEXT_COLOR);
			UIManager.put("ComboBox.disabledForeground", Color.WHITE);
			UIManager.put("ComboBox.disabledBackground", Color.GRAY);
			// UIManager.put("ComboBox.font", Color.YELLOW);
			UIManager.put("ComboBox.border", Color.YELLOW);
			UIManager.put("opaque", false);
			UIManager.put("ComboBox.squareButton", true);
			UIManager.put("ComboBox.padding", new Insets(2, 2, 2, 2));

			Object[] scrollbar = {
					"ScrollBarUI", ScrollbarUiFactory.class.getName(),
					"BackgroundPanelUI", BackgroundPanelUiFactory.class.getName(),
					"TextFieldUI", TextFieldUiFactory.class.getName(),
					"ButtonUI", ButtonUiFactory.class.getName(),
					"LabelUI", LabelUiFactory.class.getName(),
					"PanelUI", PanelUiFactory.class.getName(),
					"ScrollPaneUI", ScrollPaneUiFactory.class.getName(),
					"ToggleButtonUI", ToggleButtonUiFactory.class.getName(),
					"ProgressBarUI", ProgressBarUiFactory.class.getName(),
					"TextAreaUI", TextAreaUiFactory.class.getName(),
					"ComboBoxUI", ComboboxUiFactory.class.getName(),
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

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			throw new JSettlersLookAndFeelExecption(e);
		}
	}
}
