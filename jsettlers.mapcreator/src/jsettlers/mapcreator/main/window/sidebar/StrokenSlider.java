/*******************************************************************************
 * Copyright (c) 2015 - 2016
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
