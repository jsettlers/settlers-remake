package jsettlers.main.components.general;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Slider based on a progressbar, looks more like the original in the settler game
 * 
 * @author Andreas Butti
 */
public class SettlerSlider extends JProgressBar {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public SettlerSlider() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleMouseEvent(e);
			}
		});

		addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				handleMouseEvent(e);
			}
		});

	}

	/**
	 * Handle mouse events to set value
	 * 
	 * @param e
	 *            Event
	 */
	protected void handleMouseEvent(MouseEvent e) {
		if (!isEnabled()) {
			return;
		}

		// Retrieves the mouse position relative to the component origin.
		int mouseX = e.getX();

		// Computes how far along the mouse is relative to the component width then multiply it by the progress bar's maximum value.
		int progressBarVal = (int) Math.round((mouseX / (double) getWidth()) * (getMaximum() - getMinimum()) + getMinimum());

		if (progressBarVal < getMinimum()) {
			progressBarVal = getMinimum();
		}

		if (progressBarVal > getMaximum()) {
			progressBarVal = getMaximum();
		}

		setValue(progressBarVal);
	}

}
