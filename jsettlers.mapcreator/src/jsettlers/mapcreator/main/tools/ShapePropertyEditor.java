package jsettlers.mapcreator.main.tools;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jsettlers.mapcreator.tools.shapes.ShapeProperty;
import jsettlers.mapcreator.tools.shapes.ShapeType;

public class ShapePropertyEditor extends JPanel {

	/**
     * 
     */
	private static final long serialVersionUID = -9178084228962216713L;

	public ShapePropertyEditor(final ShapeType shape, final ShapeProperty property) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		setBorder(BorderFactory.createTitledBorder(property.getName()));
		final JSlider slider = new JSlider(1, 50, shape.getProperty(property));
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent arg0) {
				shape.setProperty(property, slider.getModel().getValue());
			}
		});
		add(slider);
	}
}
