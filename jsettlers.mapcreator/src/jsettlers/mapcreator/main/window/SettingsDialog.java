package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Show the map settings dialog
 * 
 * @author Andreas Butti
 *
 */
public abstract class SettingsDialog extends JDialog {
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
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout());

		this.header = header;
		headerEditor = new MapHeaderEditorPanel(header, false);
		add(headerEditor, BorderLayout.CENTER);

		initButton();

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

	/**
	 * Initiliaze buttons
	 */
	private void initButton() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton btOk = new JButton(EditorLabels.getLabel("OK"));
		btOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MapFileHeader nheader = headerEditor.getHeader();
				if (nheader.getWidth() != header.getWidth() || nheader.getHeight() != header.getHeight()) {
					JOptionPane.showMessageDialog(SettingsDialog.this, "Widh and height are fixed.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				applyNewHeader(nheader);
				dispose();
			}
		});

		JButton btCancel = new JButton(EditorLabels.getLabel("Cancel"));
		btOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});

		buttonPanel.add(btCancel);
		buttonPanel.add(btOk);

		Dimension size = btOk.getPreferredSize();
		if (btCancel.getPreferredSize().width > size.width) {
			size.width = btCancel.getPreferredSize().width;
		}
		btOk.setPreferredSize(size);
		btCancel.setPreferredSize(size);

		add(buttonPanel, BorderLayout.SOUTH);
	}

}
