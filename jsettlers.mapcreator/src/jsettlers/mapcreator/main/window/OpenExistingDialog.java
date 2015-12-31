package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Dialog to open an existing map
 * 
 * @author Andreas Butti
 */
public class OpenExistingDialog extends AbstractOkCancelDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Panel with the map list
	 */
	private OpenPanel openPanel = new OpenPanel();

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Parent to center on
	 */
	public OpenExistingDialog(JFrame parent) {
		super(parent);
		setTitle(EditorLabels.getLabel("openfile.header"));

		add(openPanel, BorderLayout.CENTER);

		pack();
		setLocationRelativeTo(parent);
		setModal(true);
	}

	/**
	 * @return The selected map ID
	 */
	public String getSelectedMapId() {
		return openPanel.getSelectedMap().getMapId();
	}
}
