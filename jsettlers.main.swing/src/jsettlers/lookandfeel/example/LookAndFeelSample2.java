package jsettlers.lookandfeel.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
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
import jsettlers.logic.map.save.MapList;
import jsettlers.lookandfeel.LFStyle;
import jsettlers.lookandfeel.SettlerLookAndFeel;
import jsettlers.lookandfeel.components.SplitedBackgroundPanel;
import jsettlers.lookandfeel.ui.UIDefaults;
import jsettlers.main.components.openpanel.OpenPanel;

/**
 * Sample to test Look and Feel - please leave this class until the new Swing GUI is working
 * 
 * (See https://github.com/jsettlers/settlers-remake/issues/268)
 * 
 * @author Andreas Butti
 *
 */
public class LookAndFeelSample2 extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel pButtons = new JPanel();

	private JPanel pContents = new JPanel();

	private JLabel lbInfo = new JLabel(
			"<html>This project intends to create a remake of the famous strategy game \"The Settlers 3\" published by Blue Byte in 1998. The project is developed in Java and runs on PC (Windows/Linux), Mac and Android. More information can be found on the project's website at www.settlers-android-clone.com</html>");

	private JPanel pInfo = new JPanel();

	private JPanel openPanel;

	public LookAndFeelSample2() {
		setTitle(getClass().getName());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		pInfo.setLayout(new BorderLayout());
		pInfo.add(lbInfo, BorderLayout.NORTH);
		pInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pInfo.putClientProperty(LFStyle.KEY, LFStyle.PANEL_DARK);
		lbInfo.setFont(UIDefaults.FONT);
		lbInfo.setForeground(Color.WHITE);

		openPanel = new OpenPanel(MapList.getDefaultList().getFreshMaps().getItems(), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		createButton("Neues Einzelspiel", openPanel);
		createButton("Spiel laden", null);
		createButton("Neues Netzwerkspiel", null);
		createButton("Netzwerkspiel beitreten", pInfo);
		createButton("Einstellungen", null);
		createButton("Beenden", null);

		contents.add(pButtons);
		contents.add(pContents);
		pContents.setLayout(new BorderLayout());
		pContents.add(pInfo, BorderLayout.CENTER);
		pButtons.setLayout(new GridLayout(0, 1, 20, 20));
		add(contents);

		// ********************************************************
		// IMPORTANT
		// Update tree ui after the style of all components is set
		// ********************************************************
		SwingUtilities.updateComponentTreeUI(contents);
		contents.invalidate();
		// ********************************************************

		setSize(800, 600);
		setLocationRelativeTo(null);
	}

	protected SplitedBackgroundPanel contents = new SplitedBackgroundPanel();

	private void createButton(final String text, final JComponent panel) {
		JButton bt = new JButton(text);
		bt.putClientProperty(LFStyle.KEY, LFStyle.BUTTON_MENU);
		bt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				contents.getTitleLabel().setText(text);
				pContents.removeAll();
				if (panel != null) {
					pContents.add(panel, BorderLayout.CENTER);
				}

				pContents.revalidate();
				repaint();
			}
		});

		pButtons.add(bt);
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
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}

			UIManager.addAuxiliaryLookAndFeel(new SettlerLookAndFeel());
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new LookAndFeelSample2().setVisible(true);
	}
}
