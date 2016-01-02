package jsettlers.mapcreator.main.window.sidebar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.shapes.EShapeProperty;
import jsettlers.mapcreator.tools.shapes.EShapeType;
import jsettlers.mapcreator.tools.shapes.ShapeProperty;
import jsettlers.mapcreator.tools.shapes.ShapeType;

/**
 * Panel to select shape
 * 
 * @author Andreas Butti
 */
public class ShapeSelectionPanel extends Box {
	private static final long serialVersionUID = 1L;

	/**
	 * Active shape
	 */
	private ShapeType activeShape = null;

	/**
	 * All buttons
	 */
	private Map<EShapeType, JToggleButton> buttons = new LinkedHashMap<>();

	/**
	 * All sliders
	 */
	private Map<EShapeProperty, StrokenSlider> properties = new HashMap<>();

	/**
	 * Listener for activated shape
	 */
	private final class ShapeActionListener implements ActionListener {
		private final ShapeType shape;

		private ShapeActionListener(ShapeType shape) {
			this.shape = shape;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			activeShape = shape;
			updateStrokeProperties();
		}
	}

	/**
	 * Constructor
	 */
	public ShapeSelectionPanel() {
		super(BoxLayout.Y_AXIS);
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);

		ButtonGroup group = new ButtonGroup();

		for (EShapeType type : EShapeType.values()) {
			JToggleButton bt = new JToggleButton(type.getIcon());
			bt.setDisabledIcon(type.getIcon().createDisabledIcon());
			bt.setSelectedIcon(type.getIcon().createSelectedIcon());
			bt.setToolTipText(type.getShape().getName());
			bt.addActionListener(new ShapeActionListener(type.getShape()));
			tb.add(bt);
			group.add(bt);
			buttons.put(type, bt);
		}

		add(tb);

		for (EShapeProperty p : EShapeProperty.values()) {
			StrokenSlider slider = new StrokenSlider(p);
			properties.put(p, slider);

			add(slider);
		}

		updateStrokeProperties();
	}

	/**
	 * @return The active shape
	 */
	public ShapeType getActiveShape() {
		return activeShape;
	}

	/**
	 * Update the property slider
	 */
	private void updateStrokeProperties() {
		for (StrokenSlider s : properties.values()) {
			s.setProperty(null);
		}

		if (activeShape == null) {
			return;
		}

		Hashtable<EShapeProperty, ShapeProperty> props = activeShape.getProperties();

		for (Entry<EShapeProperty, ShapeProperty> e : props.entrySet()) {
			StrokenSlider slider = properties.get(e.getKey());
			slider.setProperty(e.getValue());
		}
	}

	/**
	 * Enable / Disable tool buttons
	 * 
	 * @param tool
	 *            Tool
	 */
	public void updateShapeSettings(Tool tool) {
		if (tool == null) {
			for (JToggleButton b : buttons.values()) {
				b.setEnabled(false);
			}

			updateStrokeProperties();
			return;
		}

		Set<EShapeType> supportedShapes = tool.getSupportedShapes();

		for (Entry<EShapeType, JToggleButton> e : buttons.entrySet()) {
			EShapeType type = e.getKey();
			JToggleButton button = e.getValue();

			button.setEnabled(supportedShapes.contains(type));
		}

		for (JToggleButton b : buttons.values()) {
			if (b.isSelected() && b.isEnabled()) {
				// already a valid selection
				updateStrokeProperties();
				return;
			}
		}

		for (Entry<EShapeType, JToggleButton> e : buttons.entrySet()) {
			if (e.getValue().isEnabled()) {
				e.getValue().setSelected(true);
				this.activeShape = e.getKey().getShape();
				break;
			}
		}

		updateStrokeProperties();
	}

}
