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
package jsettlers.mapcreator.main.window;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapFileHeader.MapType;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.main.window.sidebar.RectIcon;

/**
 * Panel to create a new file
 * 
 * @author Andreas Butti
 *
 */
public class NewFilePanel extends Box {
	private static final long serialVersionUID = 1L;

	/**
	 * Default header values
	 */
	private static final MapFileHeader DEFAULT = new MapFileHeader(MapType.NORMAL, "new map", null, "", (short) 300, (short) 300, (short) 1,
			(short) 10, null, new short[MapFileHeader.PREVIEW_IMAGE_SIZE * MapFileHeader.PREVIEW_IMAGE_SIZE]);

	/**
	 * Available ground types
	 */
	private static final ELandscapeType[] GROUND_TYPES = new ELandscapeType[] { ELandscapeType.WATER8, ELandscapeType.GRASS,
			ELandscapeType.DRY_GRASS, ELandscapeType.SNOW, ELandscapeType.DESERT };

	/**
	 * Selected ground type
	 */
	private final JComboBox<ELandscapeType> groundTypes;

	/**
	 * Header editor panel
	 */
	private final MapHeaderEditorPanel headerEditor;

	/**
	 * Constructor
	 */
	public NewFilePanel() {
		super(BoxLayout.Y_AXIS);
		this.headerEditor = new MapHeaderEditorPanel(DEFAULT, true);
		headerEditor.setBorder(BorderFactory.createTitledBorder(EditorLabels.getLabel("newfile.map-settings")));
		add(headerEditor);
		headerEditor.getNameField().selectAll();

		JPanel ground = new JPanel();
		ground.setBorder(BorderFactory.createTitledBorder(EditorLabels.getLabel("newfile.ground-type")));

		this.groundTypes = new JComboBox<>(GROUND_TYPES);
		ground.add(groundTypes);
		groundTypes.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				ELandscapeType type = (ELandscapeType) value;
				setIcon(new RectIcon(22, new Color(type.color.getARGB()), Color.GRAY));
				setText(EditorLabels.getLabel("landscape." + type.name()));

				return this;
			}
		});
		add(ground);

	}

	/**
	 * @return The selected ground type
	 */
	public ELandscapeType getGroundTypes() {
		return (ELandscapeType) groundTypes.getSelectedItem();
	}

	/**
	 * @return The configured map header
	 */
	public MapFileHeader getHeader() {
		return headerEditor.getHeader();
	}
}
