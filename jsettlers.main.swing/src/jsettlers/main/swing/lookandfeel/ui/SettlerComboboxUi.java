package jsettlers.main.swing.lookandfeel.ui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;

/**
 * Settler Combobox UI
 * 
 * @author Andreas Butti
 */
public class SettlerComboboxUi extends BasicComboBoxUI {

	/**
	 * Renderer
	 */
	private static class SettlerListCellRenderer extends BasicSettlerListCellRenderer<Object> {
		private static final long serialVersionUID = 1L;

		@Override
		protected void setValue(JList<? extends Object> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			setText(String.valueOf(value));
		}

	}

	/**
	 * Constructor
	 */
	public SettlerComboboxUi() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setOpaque(false);

		((JComboBox<Object>) c).setRenderer(new SettlerListCellRenderer());
		c.setBorder(BorderFactory.createLineBorder(Color.WHITE));
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.setOpaque(true);
		c.setBorder(null);
	}

	@Override
	protected JButton createArrowButton() {
		JButton button = new ScrollbarUiButton(BasicArrowButton.SOUTH, UIDefaults.ARROW_COLOR);
		button.setName("ComboBox.arrowButton");
		return button;
	}
}