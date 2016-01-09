package jsettlers.lookandfeel.example;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import jsettlers.common.CommonConstants;
import jsettlers.common.ai.EWhatToDoAiType;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.utils.MainUtils;
import jsettlers.common.utils.OptionableProperties;
import jsettlers.graphics.localization.AbstractLabels;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.swing.resources.ConfigurationPropertiesFile;
import jsettlers.graphics.swing.resources.SwingResourceLoader;
import jsettlers.lookandfeel.LFStyle;
import jsettlers.lookandfeel.SettlerLookAndFeel;
import jsettlers.lookandfeel.StoneScrollbar;
import jsettlers.lookandfeel.components.BackgroundPanel;

/**
 * Sample to test Look and Feel - please leave this class until the new Swing GUI is working
 * 
 * (See https://github.com/jsettlers/settlers-remake/issues/268)
 * 
 * @author Andreas Butti
 *
 */
public class LookAndFeelSample extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public LookAndFeelSample() {
		setTitle("Example");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		BackgroundPanel backgroundPanel = new BackgroundPanel();
		JPanel contentPanel = new JPanel();
		backgroundPanel.add(contentPanel);

		// -------------------

		JButton button1 = new JButton("Button1");
		button1.putClientProperty(LFStyle.KEY, LFStyle.BUTTON_MENU);
		contentPanel.add(button1);
		button1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("button 1");
			}
		});

		// -------------------

		JButton button2 = new JButton("Button2");
		contentPanel.add(button2);

		// -------------------

		JLabel lbHeader = new JLabel("Titel label");
		lbHeader.putClientProperty(LFStyle.KEY, LFStyle.LABEL_HEADER);
		contentPanel.add(lbHeader);

		lbHeader = new JLabel("Titel Center", SwingConstants.CENTER);
		lbHeader.putClientProperty(LFStyle.KEY, LFStyle.LABEL_HEADER);
		contentPanel.add(lbHeader);

		// -------------------

		JLabel lbText = new JLabel("Label long");
		lbText.putClientProperty(LFStyle.KEY, LFStyle.LABEL_LONG);
		contentPanel.add(lbText);

		lbText = new JLabel("Label short");
		lbText.putClientProperty(LFStyle.KEY, LFStyle.LABEL_SHORT);
		contentPanel.add(lbText);

		// -------------------

		JTextField txt = new JTextField("JTextField");
		contentPanel.add(txt);

		contentPanel.add(new JTextField("JTextField"));
		contentPanel.add(new JTextField("JTextField"));
		contentPanel.add(new JTextField("JTextField"));
		contentPanel.add(new JTextField("JTextField"));
		contentPanel.add(new JTextField("JTextField"));
		contentPanel.add(new JTextField("JTextField"));
		contentPanel.add(new JTextField("JTextField"));
		contentPanel.add(new JTextField("JTextField"));
		contentPanel.add(new JTextField("JTextField"));
		contentPanel.add(new JTextField("JTextField"));
		contentPanel.add(new JTextField("JTextField"));
		contentPanel.add(new JTextField("JTextField"));
		contentPanel.add(new JTextField("JTextField"));

		JLabel p = new JLabel(
				"<html>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br>a<br></html>");
		JScrollPane scrol = new JScrollPane(p);
		scrol.setBorder(new TitledBorder("Scroll"));
		scrol.setPreferredSize(new Dimension(80, 120));

		contentPanel.add(scrol);

		JScrollBar sb = new StoneScrollbar(JScrollBar.VERTICAL, 15, 15, 0, 150);
		contentPanel.add(sb);

		// -------------------

		add(backgroundPanel);

		System.out.println("========");

		// ********************************************************
		// IMPORTANT
		// Update tree ui after the style of all components is set
		// ********************************************************
		SwingUtilities.updateComponentTreeUI(backgroundPanel);
		backgroundPanel.invalidate();
		// ********************************************************

		setSize(800, 600);
		setLocationRelativeTo(null);
	}

	public static ConfigurationPropertiesFile getConfigFile(Properties options, String defaultConfigFileName) throws IOException {
		String configFileName = defaultConfigFileName;
		if (options.containsKey("config")) {
			configFileName = options.getProperty("config");
		}
		return new ConfigurationPropertiesFile(new File(configFileName));
	}

	private static boolean trySettingUpResources(ConfigurationPropertiesFile configFile) {
		try {
			SwingResourceLoader.setupGraphicsAndSoundResources(configFile);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Sets up the {@link ResourceManager} by using a configuration file. <br>
	 * First it is checked, if the given argsMap contains a "configFile" parameter. If so, the path specified for this parameter is used to get the
	 * file. <br>
	 * If the parameter is not given, the defaultConfigFile is used.
	 * 
	 * @param options
	 *            Command line options
	 * @param defaultConfigFileName
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void setupResourceManagers(OptionableProperties options, String defaultConfigFileName) throws FileNotFoundException, IOException {
		ConfigurationPropertiesFile configFile = getConfigFile(options, defaultConfigFileName);
		SwingResourceLoader.setupResourcesManager(configFile);

		boolean firstRun = true;

		while (!configFile.isSettlersFolderSet() || !trySettingUpResources(configFile)) {
			if (!firstRun) {
				JOptionPane.showMessageDialog(null, Labels.getString("settlers-folder-still-invalid"));
			}
			firstRun = false;

			JFileChooser fileDialog = new JFileChooser();
			fileDialog.setAcceptAllFileFilterUsed(false);
			fileDialog.setFileFilter(new FileFilter() {
				@Override
				public String getDescription() {
					return null;
				}

				@Override
				public boolean accept(File f) {
					return f.isDirectory();
				}
			});
			fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileDialog.setDialogType(JFileChooser.SAVE_DIALOG);
			fileDialog.setMultiSelectionEnabled(false);
			fileDialog.setDialogTitle(Labels.getString("select-settlers-3-folder"));
			fileDialog.showOpenDialog(null);

			File selectedFolder = fileDialog.getSelectedFile();
			if (selectedFolder == null) {
				String noFolderSelctedMessage = Labels.getString("error-no-settlers-3-folder-selected");
				JOptionPane.showMessageDialog(null, noFolderSelctedMessage);
				System.err.println(noFolderSelctedMessage);
				System.exit(1);
			}

			System.out.println(selectedFolder);
			try {
				configFile.setSettlersFolder(selectedFolder);
			} catch (IOException ex) {
				String errorSavingSettingsMessage = Labels.getString("error-settings-not-saveable");
				System.err.println(errorSavingSettingsMessage);
				JOptionPane.showMessageDialog(null, errorSavingSettingsMessage);
				ex.printStackTrace();
			}
		}
	}

	public static void loadOptionalSettings(OptionableProperties options) {
		CommonConstants.CONTROL_ALL = options.isOptionSet("control-all");
		CommonConstants.ACTIVATE_ALL_PLAYERS = options.isOptionSet("activate-all-players");
		CommonConstants.ENABLE_CONSOLE_LOGGING = options.isOptionSet("console-output");
		CommonConstants.ENABLE_AI = !options.isOptionSet("disable-ai");
		CommonConstants.ALL_AI = options.isOptionSet("all-ai");

		if (options.containsKey("fixed-ai-type")) {
			CommonConstants.FIXED_AI_TYPE = EWhatToDoAiType.valueOf(options.getProperty("fixed-ai-type"));
		}

		if (options.isOptionSet("localhost")) {
			CommonConstants.DEFAULT_SERVER_ADDRESS = "localhost";
		}

		if (options.containsKey("locale")) {
			String localeString = options.getProperty("locale");
			String[] localeParts = localeString.split("_");
			if (localeParts.length == 2) {
				AbstractLabels.preferredLocale = new Locale(localeParts[0], localeParts[1]);
			} else {
				System.err.println("Please specify the locale with language and country. (For example: de_de or en_us)");
			}
		}
	}

	/**
	 * Main for testing
	 * 
	 * @param args
	 *            Arguments
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		OptionableProperties options = MainUtils.loadOptions(args);

		loadOptionalSettings(options);
		setupResourceManagers(options, "config.prp");

		try {
			SettlerLookAndFeel.install();
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new LookAndFeelSample().setVisible(true);
	}
}
