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
package jsettlers.mapcreator.main.window.sidebar;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jsettlers.mapcreator.tools.shapes.ShapeProperty;
import jsettlers.mapcreator.tools.shapes.ShapeType;

/**
 * Slider property
 * 
 * @author Andreas Butti
 */
public class ShapePropertyEditor extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Border
	 */
	private TitledBorder titleBorder;

	/**
	 * Constructor
	 * 
	 * @param shape
	 *            Shape
	 * @param property
	 *            Property to edit
	 */
	public ShapePropertyEditor(final ShapeType shape, final ShapeProperty property) {
		setLayout(new BorderLayout());
		titleBorder = BorderFactory.createTitledBorder(property.getName());
		setBorder(titleBorder);
		final JSlider slider = new JSlider(1, 50, shape.getProperty(property));
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent arg0) {
				int value = slider.getModel().getValue();
				shape.setProperty(property, value);

				titleBorder.setTitle(property.getName() + " [" + value + "]");
				ShapePropertyEditor.this.repaint();
			}
		});
		add(slider, BorderLayout.CENTER);
	}
}
