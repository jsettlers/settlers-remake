package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Display a dialog to create a new map or open an existing one, displayed at startup
 * 
 * @author Andreas Butti
 */
public class NewOrOpenDialog extends AbstractOkCancelDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Panel with the editfield
	 */
	private NewFilePanel newFilePanel = new NewFilePanel();

	/**
	 * Panel with the map list
	 */
	private OpenPanel openPanel = new OpenPanel();
	/**
	 * Panel with the map list
	 */
	private LastUsedPanel lastUsed = new LastUsedPanel();

	/**
	 * Main tabs
	 */
	private JTabbedPane tabs = new JTabbedPane();

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Parent to center on
	 */
	public NewOrOpenDialog(JFrame parent) {
		super(parent);
		setTitle(EditorLabels.getLabel("neworopen.header"));

		tabs.addTab(EditorLabels.getLabel("neworopen.lastused"), lastUsed);
		tabs.addTab(EditorLabels.getLabel("neworopen.open"), openPanel);
		tabs.addTab(EditorLabels.getLabel("neworopen.new"), newFilePanel);

		add(tabs, BorderLayout.CENTER);

		if (!lastUsed.hasFiles()) {
			tabs.setSelectedComponent(openPanel);
		}

		pack();
		setLocationRelativeTo(parent);
		setModal(true);
	}

	/**
	 * @return true for last used
	 */
	public boolean isLastUsed() {
		return tabs.getSelectedIndex() == 0;
	}

	/**
	 * @return true for open
	 */
	public boolean isOpenAction() {
		return tabs.getSelectedIndex() == 1;
	}

	/**
	 * @return Panel with the new file data
	 */
	public NewFilePanel getNewFilePanel() {
		return newFilePanel;
	}

	/**
	 * @return Panel with the map list
	 */
	public OpenPanel getLastUsed() {
		return lastUsed;
	}

	/**
	 * @return Panel with the map list
	 */
	public OpenPanel getOpenPanel() {
		return openPanel;
	}

}
