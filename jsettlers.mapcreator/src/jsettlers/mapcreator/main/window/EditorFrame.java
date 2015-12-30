package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Editor JFrame, Main Wndow
 * 
 * @author Andreas Butti
 */
public abstract class EditorFrame extends JFrame {
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
	 * Display icon AND text in toolbar, default display only icon and text as tooltip
	 */
	public static final String DISPLAY_TEXT_IN_TOOLBAR = "display-text-in-toolbar";

	/**
	 * Constructor
	 */
	public EditorFrame() {
		super();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setFilename("unnamed");

		setLayout(new BorderLayout());

		registerActions();

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				ActionMap actionMap = ((JPanel) getContentPane()).getActionMap();
				Action quitAction = actionMap.get("quit");
				quitAction.actionPerformed(new ActionEvent(this, 0, "quit"));
			}
		});
	}

	/**
	 * Initialize menubar and toolbar
	 */
	public void initMenubarAndToolbar() {
		createMenu();
		createToolbar();
	}

	/**
	 * Register actions
	 */
	private void registerActions() {
		registerAction("new", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showConfirmDialog(null, "Action not implemented");
			}
		});
		registerAction("open", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showConfirmDialog(null, "Action not implemented");
			}
		});
		registerAction("save-as", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showConfirmDialog(null, "Action not implemented");
			}
		});
		registerAction("export-image", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showConfirmDialog(null, "Action not implemented");
			}
		});
		registerAction("map-settings", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showConfirmDialog(null, "Action not implemented");
			}
		});
		registerAction("manual", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showConfirmDialog(null, "Action not implemented");
			}
		});
		registerAction("about", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showConfirmDialog(null, "Action not implemented");
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
		URL icon = EditorFrame.class.getResource("icons/" + actionName + ".png");
		if (icon != null) {
			action.putValue(Action.SMALL_ICON, new ImageIcon(icon));
		}

		action.putValue(Action.NAME, EditorLabels.getLabel("action." + actionName));

		actionMap.put(actionName, action);
	}

	/**
	 * Enable / disable an action
	 * 
	 * @param actionName
	 *            Action name
	 * @param enable
	 *            enabled / disabled
	 */
	public void enableAction(String actionName, boolean enable) {
		ActionMap actionMap = ((JPanel) this.getContentPane()).getActionMap();

		Action action = actionMap.get(actionName);
		if (action == null) {
			System.err.println("Could not find action \"" + action + "\" to enable / disable");
			return;
		}

		action.setEnabled(enable);
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

			// because of the open gl context
			menu.getPopupMenu().setLightWeightPopupEnabled(false);

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

					final JMenuItem it = menu.add(action);

					action.addPropertyChangeListener(new PropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if (Action.NAME.equals(evt.getPropertyName())) {
								it.setText((String) evt.getNewValue());
							}
						}
					});
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
			} else if ("player-spinner".equals(toolName)) {
				tb.add(new JLabel(EditorLabels.getLabel("current-player")));
				JSpinner playerSpinner = createPlayerSelectSpinner();
				tb.add(playerSpinner);
			} else {
				final Action action = actionMap.get(toolName);
				if (action == null) {
					System.err.println("Action \"" + toolName + "\" not found!");
					continue;
				}
				final JButton bt = tb.add(action);

				action.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if (Action.NAME.equals(evt.getPropertyName())) {
							setButtonText(bt, action);
						}
					}
				});

				setButtonText(bt, action);

				bt.setVerticalTextPosition(SwingConstants.CENTER);
				bt.setHorizontalTextPosition(SwingConstants.RIGHT);
			}
		}

		add(tb, BorderLayout.NORTH);
	}

	/**
	 * Set the text of a button
	 * 
	 * @param bt
	 *            Button
	 * @param action
	 *            Action
	 */
	private void setButtonText(JButton bt, Action action) {

		Boolean displayTextInToolbar = (Boolean) action.getValue(EditorFrame.DISPLAY_TEXT_IN_TOOLBAR);
		if (displayTextInToolbar != null && displayTextInToolbar) {
			bt.setText((String) action.getValue(Action.NAME));
		} else {
			bt.setToolTipText((String) action.getValue(Action.NAME));
		}

		bt.setName((String) action.getValue(Action.NAME));
	}

	/**
	 * Create the player selection
	 * 
	 * @return JSpinner
	 */
	protected abstract JSpinner createPlayerSelectSpinner();

	/**
	 * @param filename
	 *            Filename to display in the header
	 */
	public void setFilename(String filename) {
		setTitle(String.format(EditorLabels.getLabel("window.header"), filename));
	}

}
