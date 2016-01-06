package jsettlers.mapcreator.main.window.sidebar;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JProgressBar;

import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.tools.shapes.EShapeProperty;
import jsettlers.mapcreator.tools.shapes.ShapeProperty;

/**
 * Slider based on JProgressBar, so it's possible to embedded text, which is not possible with JSlider
 * 
 * @author Andreas Butti
 *
 */
public class StrokenSlider extends JProgressBar {
	private static final long serialVersionUID = 1L;

	/**
	 * Translated name to display
	 */
	private String displayName = "";

	/**
	 * Property to update
	 */
	private ShapeProperty property = null;

	/**
	 * Constructor
	 * 
	 * @param type
	 *            Type of this slider
	 */
	public StrokenSlider(EShapeProperty type) {
		setStringPainted(true);

		setDisplayName(EditorLabels.getLabel("shape.property." + type.name()));

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

		setMinimum(0);
		setMaximum(100);
		setValue(50);
	}

	/**
	 * Handle mouse events to set value
	 * 
	 * @param e
	 *            Event
	 */
	private void handleMouseEvent(MouseEvent e) {
		if (!isEnabled()) {
			return;
		}

		// Retrieves the mouse position relative to the component origin.
		int mouseX = e.getX();

		// Computes how far along the mouse is relative to the component width then multiply it by the progress bar's maximum value.
		int progressBarVal = (int) Math.round((mouseX / (double) getWidth()) * getMaximum());

		setValue(progressBarVal);
		property.setValue(progressBarVal);
	}

	/**
	 * @param displayName
	 *            Translated name to display
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public void setValue(int n) {
		super.setValue(n);
		updateString();
	}

	/**
	 * Update the displayed string
	 */
	private void updateString() {
		setString(displayName + " " + getValue());
	}

	/**
	 * Set the Property to update
	 * 
	 * @param property
	 *            Property
	 */
	public void setProperty(ShapeProperty property) {
		this.property = property;

		if (property != null) {
			setEnabled(true);
			setMinimum(property.getMin());
			setMaximum(property.getMax());
			setValue(property.getValue());
		} else {
			setEnabled(false);
		}
	}

}
