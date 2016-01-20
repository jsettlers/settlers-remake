package jsettlers.main.swing.foldertree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jsettlers.graphics.localization.Labels;

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
	private final JLabel label = new JLabel(Labels.getString("select-valid-settlers-3-folder"));

	/**
	 * Gradient color top
	 */
	private Color background1 = new Color(0xFFD17C);

	/**
	 * Gradient color bottom
	 */
	private Color background2 = new Color(0xC06C4C);

	/**
	 * Folder to start
	 */
	private String startFolder;

	/**
	 * Start button
	 */
	private final JButton btContinue;

	/**
	 * Constructor
	 * 
	 * @param listener
	 *            Listener for start
	 */
	public FolderFoundPanel(ActionListener listener) {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout());
		setOpaque(true);

		add(label, BorderLayout.CENTER);

		this.btContinue = new JButton(Labels.getString("button-start"));
		btContinue.setEnabled(false);
		btContinue.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				listener.actionPerformed(new ActionEvent(this, 0, startFolder));
			}
		});
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
		startFolder = folder;
		btContinue.setEnabled(true);
		repaint();
	}

	@Override
	public void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		int w = getWidth();
		int h = getHeight();
		GradientPaint gp = new GradientPaint(0, 0, background1, 0, h, background2);
		g.setPaint(gp);
		g.fillRect(0, 0, w, h);
	}
}
