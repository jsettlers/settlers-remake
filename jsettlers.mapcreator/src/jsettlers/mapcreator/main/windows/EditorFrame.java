package jsettlers.mapcreator.main.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Editor JFrame, Main Wndow
 * 
 * @author Andreas Butti
 */
public class EditorFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Menu / Toolbar configuration
	 */
	private Properties menuconfig = new Properties();

	/**
	 * Shortcut configuration
	 */
	private Properties shortcut = new Properties();

	/**
	 * Constructor
	 */
	public EditorFrame() {
		super();
		setFilename("unnamed");

		setLayout(new BorderLayout());

		registerActions();
	}

	/**
	 * Initialize menubar and toolbar
	 */
	public void initMenubarAndToolbar() {
		createMenu();
		createToolbar();
	}

	private void registerActions() {
		// TODO !!!!!!!!!!!!!
		registerAction("quit", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		registerAction("new", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		registerAction("open", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		registerAction("save", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		registerAction("save-as", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		registerAction("export-image", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		registerAction("undo", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		registerAction("redo", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		registerAction("map-settings", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		registerAction("zoom-in", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		registerAction("zoom-out", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		registerAction("zoom100", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		registerAction("statistic", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		registerAction("manual", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		registerAction("about", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
	}

	/**
	 * Register a menu / toolbar action
	 * 
	 * @param actionName
	 *            Name of the action
	 * @param action
	 *            Action to execute
	 */
	public void registerAction(String actionName, Action action) {
		ActionMap actionMap = ((JPanel) this.getContentPane()).getActionMap();

		// try to load icon, if any
		URL icon = getClass().getResource("icons/" + actionName + ".png");
		if (icon != null) {
			action.putValue(Action.SMALL_ICON, new ImageIcon(icon));
		}

		action.putValue(Action.NAME, EditorLabels.getLabel("action." + actionName));

		actionMap.put(actionName, action);
	}

	/**
	 * Create the menu from menu.properties
	 */
	private void createMenu() {
		try {
			menuconfig.load(EditorFrame.class.getResourceAsStream("menu.properties"));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not load menu.properties");
		}
		try {
			shortcut.load(EditorFrame.class.getResourceAsStream("shortcut.properties"));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not load shortcut.properties");
		}

		ActionMap actionMap = ((JPanel) this.getContentPane()).getActionMap();
		JMenuBar menuBar = new JMenuBar();

		for (String menuName : menuconfig.getProperty("menubar", "").split(",")) {
			menuName = menuName.trim();
			if (menuName.isEmpty()) {
				continue;
			}
			JMenu menu = new JMenu(EditorLabels.getLabel("menu." + menuName));

			for (String menuAction : menuconfig.getProperty("menu." + menuName, "").split(",")) {
				menuAction = menuAction.trim();
				if (menuAction.isEmpty()) {
					continue;
				}

				if ("---".equals(menuAction)) {
					menu.addSeparator();
				} else {
					Action action = actionMap.get(menuAction);
					if (action == null) {
						System.err.println("Action \"" + menuAction + "\" not found!");
						continue;
					}

					JMenuItem it = menu.add(action);
					it.setText((String) action.getValue(Action.NAME));

					String shortcut = this.shortcut.getProperty(menuAction);
					if (shortcut != null) {
						it.setAccelerator(
								KeyStroke.getKeyStroke(shortcut));
					}

					menu.add(it);
				}
			}

			menuBar.add(menu);
		}
		setJMenuBar(menuBar);
	}

	/**
	 * Create the toolbar from menu.properties
	 */
	private void createToolbar() {
		ActionMap actionMap = ((JPanel) this.getContentPane()).getActionMap();
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);

		for (String toolName : menuconfig.getProperty("toolbar", "").split(",")) {
			toolName = toolName.trim();
			if (toolName.isEmpty()) {
				continue;
			}

			if ("---".equals(toolName)) {
				tb.addSeparator();
			} else {
				Action action = actionMap.get(toolName);
				if (action == null) {
					System.err.println("Action \"" + toolName + "\" not found!");
					continue;
				}
				JButton bt = tb.add(action);
				bt.setName((String) action.getValue(Action.NAME));
			}
		}

		add(tb, BorderLayout.NORTH);
	}

	/**
	 * TODO Call with name
	 * 
	 * @param filename
	 *            Filename to display in the header
	 */
	public void setFilename(String filename) {
		setTitle(String.format(EditorLabels.getLabel("window.header"), filename));
	}

}
