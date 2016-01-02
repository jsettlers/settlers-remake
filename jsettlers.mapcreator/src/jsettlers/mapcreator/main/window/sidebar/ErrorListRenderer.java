package jsettlers.mapcreator.main.window.sidebar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;

import jsettlers.mapcreator.mapvalidator.result.AbstractErrorEntry;
import jsettlers.mapcreator.mapvalidator.result.ErrorHeader;
import jsettlers.mapcreator.mapvalidator.result.fix.IFix;

/**
 * Renderer for the error list in the sidebar
 * 
 * @author Andreas Butti
 *
 */
public class ErrorListRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = 1L;

	/**
	 * Default font
	 */
	private Font defaultFont = getFont();

	/**
	 * Header font
	 */
	private Font headerFont = getFont().deriveFont(Font.BOLD);

	/**
	 * Header background
	 */
	private final Color BACKGROUND_HEADER = new Color(0xE0E0E0);

	/**
	 * Drop down icon
	 */
	private Icon dropDownIcon = new ImageIcon(ErrorListRenderer.class.getResource("dropdown.png"));

	/**
	 * Drop down icon
	 */
	private Icon errorIcon = new ImageIcon(ErrorListRenderer.class.getResource("error.png"));

	/**
	 * Constructor
	 */
	public ErrorListRenderer() {
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		AbstractErrorEntry entry = (AbstractErrorEntry) value;
		setText(entry.getText());

		if (entry instanceof ErrorHeader) {
			setFont(headerFont);
			if (!isSelected) {
				setBackground(BACKGROUND_HEADER);
			}
			IFix fix = ((ErrorHeader) entry).getFix();
			if (fix != null && fix.isFixAvailable()) {
				setIcon(dropDownIcon);
			} else {
				setIcon(errorIcon);
			}
		} else {
			setIcon(null);
			setFont(defaultFont);
		}

		return this;
	}
}