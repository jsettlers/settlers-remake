package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jsettlers.common.CommitInfo;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * About dialog for Map Editor
 * 
 * @author Andreas Butti
 *
 */
public class AboutDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            Parent JFrame, to center on
	 */
	public AboutDialog(JFrame parent) {
		super(parent);
		setTitle(EditorLabels.getLabel("about.header"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setLayout(new BorderLayout(0, 0));
		add(new JLabel(new ImageIcon(AboutDialog.class.getResource("about.png"))), BorderLayout.NORTH);

		Box info = Box.createVerticalBox();
		info.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		add(info, BorderLayout.CENTER);

		info.add(createHeaderLabel("about.version"));
		info.add(new JLabel(CommitInfo.COMMIT_HASH_SHORT));
		info.add(createSpacer());
		info.add(createHeaderLabel("about.developer"));
		info.add(createListLabelLabel("developer.txt"));
		info.add(createSpacer());
		info.add(createHeaderLabel("about.translator"));
		info.add(createListLabelLabel("translator.txt"));

		pack();
		setLocationRelativeTo(parent);
		setModal(true);
	}

	/**
	 * @return new spacer to separate sections
	 */
	private Component createSpacer() {
		JPanel p = new JPanel();
		p.setPreferredSize(new Dimension(1, 15));
		return p;
	}

	/**
	 * Create a bold header label
	 * 
	 * @param textId
	 *            Text id for translation
	 * @return JLabel
	 */
	private JLabel createHeaderLabel(String textId) {
		JLabel label = new JLabel(EditorLabels.getLabel(textId));
		label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
		return label;
	}

	/**
	 * @return Label with all developers
	 * @param file
	 *            File to read
	 */
	private JLabel createListLabelLabel(String file) {
		StringBuilder text = new StringBuilder();
		text.append("<html>");

		try (BufferedReader br = new BufferedReader(new InputStreamReader(AboutDialog.class.getResourceAsStream(file)))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				if (line.startsWith("#")) {
					continue;
				}

				text.append(line);
				text.append("<br>");
			}
		} catch (IOException e) {
			System.err.println("Could not read " + file);
			e.printStackTrace();
		}
		text.append("</html>");
		return new JLabel(text.toString());
	}
}
