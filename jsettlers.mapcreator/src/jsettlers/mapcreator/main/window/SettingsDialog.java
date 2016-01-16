package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Show the map settings dialog
 * 
 * @author Andreas Butti
 *
 */
public abstract class SettingsDialog extends AbstractOkCancelDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * The editor panel
	 */
	private final MapHeaderEditorPanel headerEditor;

	/**
	 * Original map header
	 */
	private MapFileHeader header;

	/**
	 * Constructor
	 * 
	 * @param header
	 *            Header to edit
	 */
	public SettingsDialog(JFrame parent, MapFileHeader header) {
		super(parent);
		setTitle(EditorLabels.getLabel("settings.header"));
		this.header = header;
		headerEditor = new MapHeaderEditorPanel(header, false);
		add(headerEditor, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(parent);
		setModal(true);
	}

	/**
	 * Apply the new header configuration
	 * 
	 * @param header
	 *            New header
	 */
	public abstract void applyNewHeader(MapFileHeader header);

	@Override
	protected boolean beforeOkAction() {
		MapFileHeader nheader = headerEditor.getHeader();
		if (nheader.getWidth() != header.getWidth() || nheader.getHeight() != header.getHeight()) {
			JOptionPane.showMessageDialog(SettingsDialog.this, "Widh and height are fixed.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		applyNewHeader(nheader);
		return true;
	}

}
