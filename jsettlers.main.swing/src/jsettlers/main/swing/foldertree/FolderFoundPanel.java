package jsettlers.main.swing.foldertree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel with the information if a settler folder was found
 * 
 * @author Andreas Butti
 *
 */
public class FolderFoundPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Information label
	 */
	private final JLabel label = new JLabel("Bitte gültigen Ordner wählen");

	/**
	 * Gradient color top
	 */
	private Color background1 = new Color(0xFFD17C);

	/**
	 * Gradient color bottom
	 */
	private Color background2 = new Color(0xC06C4C);

	/**
	 * Constructor
	 */
	public FolderFoundPanel() {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout());
		setOpaque(true);

		add(label, BorderLayout.CENTER);

		JButton btContinue = new JButton("Starten");
		add(btContinue, BorderLayout.EAST);
	}

	/**
	 * Set the folder to start with
	 * 
	 * @param folder
	 *            Absolute path
	 */
	public void setFolder(String folder) {
		background1 = new Color(0xA4FF92);
		background2 = new Color(0x4CC04E);
		label.setText(folder);
		repaint();
	}

	@Override
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		int w = getWidth();
		int h = getHeight();
		GradientPaint gp = new GradientPaint(
				0, 0, background1, 0, h, background2);
		g.setPaint(gp);
		g.fillRect(0, 0, w, h);
	}
}
