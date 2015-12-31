package jsettlers.mapcreator.main.window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapFileHeader.MapType;
import jsettlers.mapcreator.localization.EditorLabels;

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

		// TODO Translate in renderer
		this.groundTypes = new JComboBox<>(GROUND_TYPES);
		ground.add(groundTypes);
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
