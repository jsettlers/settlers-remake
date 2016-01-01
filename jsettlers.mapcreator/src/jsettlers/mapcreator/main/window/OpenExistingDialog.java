package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	 * Listener for Double click
	 */
	private ActionListener doubleClickListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			confirmed = true;
			doOkAction();
			dispose();
		}
	};

	/**
	 * Panel with the map list
	 */
	private OpenPanel openPanel = new OpenPanel(doubleClickListener);

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
