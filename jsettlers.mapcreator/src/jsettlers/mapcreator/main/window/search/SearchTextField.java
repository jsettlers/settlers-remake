package jsettlers.mapcreator.main.window.search;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;

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
	private SearchIcon searchIcon = new SearchIcon();

	/**
	 * Icon to clear search
	 */
	private ClearIcon clearIcon = new ClearIcon();

	/**
	 * Clear "Button"
	 */
	private JLabel lbClear = new JLabel(clearIcon);

	/**
	 * Constructor
	 */
	public SearchTextField() {
		setMargin(new Insets(2, searchIcon.getIconWidth() + 4, 2, clearIcon.getIconWidth() + 4));

		lbClear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setText("");
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				clearIcon.setHover(true);
				lbClear.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				clearIcon.setHover(false);
				lbClear.repaint();
			}

		});
		add(lbClear);
		lbClear.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void layout() {
		int x = getWidth() - 4 - clearIcon.getIconWidth();
		int y = (this.getHeight() - searchIcon.getIconHeight()) / 2;
		lbClear.setBounds(x, y, clearIcon.getIconWidth(), clearIcon.getIconHeight());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int x = 4;
		searchIcon.paintIcon(this, g, x, (this.getHeight() - searchIcon.getIconHeight()) / 2);

		// color already set by search icon...
		if (getText().isEmpty()) {
			x += searchIcon.getIconWidth() + 6;
			int y = this.getHeight() - (this.getHeight() - g.getFontMetrics().getHeight()) / 2 - g.getFontMetrics().getDescent();
			g.drawString("Search...", x, y);
		}

		// x = getWidth() - 4 - clearIcon.getIconWidth();
		// int y = (this.getHeight() - searchIcon.getIconHeight()) / 2;
		// clearIcon.paintIcon(this, g, x, y);
	}

}
