package jsettlers.main.components.openpanel;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;

import jsettlers.graphics.localization.Labels;

/**
 * Search Text field
 *
 * @author Andreas Butti
 */
public class SearchTextField extends JTextField {
	private static final long serialVersionUID = 1L;

	/**
	 * Search icon
	 */
	private static final SearchIcon SEARCH_ICON = new SearchIcon();

	/**
	 * Icon to clear search
	 */
	private static final ClearIcon CLEAR_ICON = new ClearIcon();

	/**
	 * Clear "Button"
	 */
	private JLabel lbClear = new JLabel(CLEAR_ICON);

	/**
	 * Search text
	 */
	private final String SEARCH = Labels.getString("general.search");

	/**
	 * Constructor
	 */
	public SearchTextField() {
		lbClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setText("");
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				CLEAR_ICON.setHover(true);
				lbClear.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				CLEAR_ICON.setHover(false);
				lbClear.repaint();
			}

		});
		lbClear.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		updateUI();
	}

	@Override
	public void updateUI() {
		super.updateUI();
		if (lbClear != null) {
			remove(lbClear);
			add(lbClear);
		}
		setMargin(new Insets(2, SEARCH_ICON.getIconWidth() + 4, 2, CLEAR_ICON.getIconWidth() + 4));
	}

	@Override
	public void layout() {
		int x = getWidth() - 4 - CLEAR_ICON.getIconWidth();
		int y = (this.getHeight() - SEARCH_ICON.getIconHeight()) / 2;
		lbClear.setBounds(x, y, CLEAR_ICON.getIconWidth(), CLEAR_ICON.getIconHeight());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int x = 4;
		SEARCH_ICON.paintIcon(this, g, x, (this.getHeight() - SEARCH_ICON.getIconHeight()) / 2);

		if (getText().isEmpty()) {
			x += SEARCH_ICON.getIconWidth() + 6;
			int y = this.getHeight() - (this.getHeight() - g.getFontMetrics().getHeight()) / 2 - g.getFontMetrics().getDescent();
			g.drawString(SEARCH, x, y);
		}
	}

}
