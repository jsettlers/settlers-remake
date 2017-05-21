/*******************************************************************************
 * Copyright (c) 2015 - 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.mapcreator.main.window;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jsettlers.exceptionhandler.ExceptionHandler;
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
	private final Properties menuconfig = new Properties();

	/**
	 * Shortcut configuration
	 */
	private final Properties shortcut = new Properties();

	/**
	 * Split editor / sidebar
	 */
	private final JSplitPane splitter;

	/**
	 * Display icon AND text in toolbar, default display only icon and text as tooltip
	 */
	public static final String DISPLAY_TEXT_IN_TOOLBAR = "display-text-in-toolbar";

	/**
	 * Display as Check menu
	 */
	public static final String DISPLAY_CHECKBOX = "display-checkbox";

	/**
	 * Value of the checkbox (for DISPLAY_CHECKBOX)
	 */
	public static final String CHECKBOX_VALUE = "checkbox-value";

	/**
	 * Logo for windows
	 */
	public static final Image APP_ICON = new ImageIcon(EditorFrame.class.getResource("icon.png")).getImage();

	/**
	 * Constructor
	 * 
	 * @param root
	 *            Root panel
	 * @param sidebar
	 *            Sidebar panel
	 */
	public EditorFrame(JComponent root, JComponent sidebar) {
		super();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setFilename("unnamed");

		setLayout(new BorderLayout());
		setIconImage(APP_ICON);

		registerActions();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ActionMap actionMap = ((JPanel) getContentPane()).getActionMap();
				Action quitAction = actionMap.get("quit");
				quitAction.actionPerformed(new ActionEvent(this, 0, "quit"));
			}
		});

		this.splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, root, sidebar);
		splitter.setResizeWeight(1);
		add(splitter, BorderLayout.CENTER);
		splitter.setDividerLocation(980);

	}

	/**
	 * @return Split editor / sidebar
	 */
	public JSplitPane getSplitter() {
		return splitter;
	}

	/**
	 * Initialize menubar and toolbar
	 */
	public void initMenubarAndToolbar() {
		createMenubar();
		createToolbar();
	}

	/**
	 * Register actions
	 */
	private void registerActions() {
		registerAction("manual", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("http://www.settlers-android-clone.com/the-map-editor/"));
				} catch (IOException | URISyntaxException e1) {
					ExceptionHandler.displayError(e1, "Could not open URL");
				}
			}
		});
		registerAction("about", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				AboutDialog dlg = new AboutDialog(EditorFrame.this);
				dlg.setVisible(true);
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
	private void createMenubar() {
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

		JMenuBar menuBar = new JMenuBar();

		for (String menuName : menuconfig.getProperty("menubar", "").split(",")) {
			menuName = menuName.trim();
			if (menuName.isEmpty()) {
				continue;
			}

			JMenu menu = createMenu(menuName);
			menuBar.add(menu);
		}
		setJMenuBar(menuBar);
	}

	/**
	 * Create a single menu in the Menubar
	 * 
	 * @param menuName
	 *            Name of the menu
	 * @return JMenu
	 */
	private JMenu createMenu(String menuName) {
		ActionMap actionMap = ((JPanel) this.getContentPane()).getActionMap();
		JMenu menu = new JMenu(EditorLabels.getLabel("menu." + menuName));
		// because of the open gl context
		menu.getPopupMenu().setLightWeightPopupEnabled(false);

		for (String menuActionName : menuconfig.getProperty("menu." + menuName, "").split(",")) {
			menuActionName = menuActionName.trim();
			if (menuActionName.isEmpty()) {
				continue;
			}

			if ("---".equals(menuActionName)) {
				menu.addSeparator();
			} else {
				final Action action = actionMap.get(menuActionName);
				if (action == null) {
					System.err.println("Action \"" + menuActionName + "\" not found!");
					continue;
				}

				createMenuItemForAction(action, menuActionName, menu);
			}
		}
		return menu;
	}

	/**
	 * Create a menu item for a specific action
	 * 
	 * @param action
	 *            The action
	 * @param menuActionName
	 *            The name of the action
	 * @param menu
	 *            The menu to add the action
	 */
	private void createMenuItemForAction(final Action action, String menuActionName, JMenu menu) {
		final JMenuItem it;
		Boolean displayAsCheckbox = (Boolean) action.getValue(EditorFrame.DISPLAY_CHECKBOX);
		if (displayAsCheckbox != null && displayAsCheckbox) {
			it = createCheckboxMenuItemForAction(action);
			menu.add(it);
		} else {
			it = menu.add(action);
		}

		action.addPropertyChangeListener(evt -> {
			if (Action.NAME.equals(evt.getPropertyName())) {
				it.setText((String) evt.getNewValue());
			}
		});
		it.setText((String) action.getValue(Action.NAME));

		String shortcut = this.shortcut.getProperty(menuActionName);
		if (shortcut != null) {
			it.setAccelerator(
					KeyStroke.getKeyStroke(shortcut));
		}
	}

	/**
	 * Create a checkbox menu item
	 * 
	 * @param action
	 *            The target action
	 * 
	 * @return JMenuItem
	 */
	private JMenuItem createCheckboxMenuItemForAction(final Action action) {
		final JCheckBoxMenuItem it = new JCheckBoxMenuItem();
		it.setAction(action);
		it.addChangeListener(e -> {
			Object oldValue = action.getValue(EditorFrame.CHECKBOX_VALUE);

			if (oldValue != null && oldValue.equals(it.isSelected())) {
				return;
			}

			action.putValue(EditorFrame.CHECKBOX_VALUE, it.isSelected());
			action.actionPerformed(new ActionEvent(it, 0, "changed"));
		});
		action.addPropertyChangeListener(evt -> {
			if (EditorFrame.CHECKBOX_VALUE.equals(evt.getPropertyName())) {
				Boolean checked = (Boolean) evt.getNewValue();
				if (it.isSelected() != checked) {
					it.setSelected(checked);
				}
			}
		});
		return it;
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
				tb.add(new JLabel(EditorLabels.getLabel("window.current-player")));
				JComponent playerSpinner = createPlayerSelectSelection();
				tb.add(playerSpinner);
			} else {
				final Action action = actionMap.get(toolName);
				if (action == null) {
					System.err.println("Action \"" + toolName + "\" not found!");
					continue;
				}
				final JButton bt = tb.add(action);

				action.addPropertyChangeListener(evt -> {
					if (Action.NAME.equals(evt.getPropertyName())) {
						setButtonText(bt, action);
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
	 * @return JComponent
	 */
	protected abstract JComponent createPlayerSelectSelection();

	/**
	 * @param filename
	 *            Filename to display in the header
	 */
	public void setFilename(String filename) {
		setTitle(String.format(EditorLabels.getLabel("window.header"), filename));
	}

}
