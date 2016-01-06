package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Display new file dialog
 * 
 * @author Andreas Butti
 */
public class NewFileDialog extends AbstractOkCancelDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Panel with the editfield
	 */
	private NewFilePanel newFilePanel = new NewFilePanel();

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Parent to center on
	 */
	public NewFileDialog(JFrame parent) {
		super(parent);
		setTitle(EditorLabels.getLabel("newfile.header"));

		add(newFilePanel, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(parent);
		setModal(true);
	}

	/**
	 * @return The selected ground type
	 */
	public ELandscapeType getGroundTypes() {
		return newFilePanel.getGroundTypes();
	}

	/**
	 * @return The configured map header
	 */
	public MapFileHeader getHeader() {
		return newFilePanel.getHeader();
	}
}
