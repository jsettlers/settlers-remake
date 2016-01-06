/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.mapcreator.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import go.graphics.area.Area;
import go.graphics.region.Region;
import go.graphics.swing.AreaContainer;
import go.graphics.swing.sound.SwingSoundPlayer;
import jsettlers.algorithms.previewimage.PreviewImageCreator;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.resources.ResourceManager;
import jsettlers.exceptionhandler.ExceptionHandler;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.startscreen.interfaces.FakeMapGame;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.main.swing.SwingManagedJSettlers;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.data.MapDataDelta;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.main.MapCreatorApp;
import jsettlers.mapcreator.main.action.AbortDrawingAction;
import jsettlers.mapcreator.main.action.CombiningActionFirerer;
import jsettlers.mapcreator.main.action.DrawLineAction;
import jsettlers.mapcreator.main.action.EndDrawingAction;
import jsettlers.mapcreator.main.action.StartDrawingAction;
import jsettlers.mapcreator.main.map.MapEditorControls;
import jsettlers.mapcreator.main.window.EditorFrame;
import jsettlers.mapcreator.main.window.LastUsedHandler;
import jsettlers.mapcreator.main.window.NewFileDialog;
import jsettlers.mapcreator.main.window.OpenExistingDialog;
import jsettlers.mapcreator.main.window.SettingsDialog;
import jsettlers.mapcreator.main.window.sidebar.RectIcon;
import jsettlers.mapcreator.main.window.sidebar.Sidebar;
import jsettlers.mapcreator.main.window.sidebar.ToolSidebar;
import jsettlers.mapcreator.mapvalidator.AutoFixErrorAction;
import jsettlers.mapcreator.mapvalidator.GotoNextErrorAction;
import jsettlers.mapcreator.mapvalidator.IScrollToAble;
import jsettlers.mapcreator.mapvalidator.MapValidator;
import jsettlers.mapcreator.mapvalidator.ShowErrorsAction;
import jsettlers.mapcreator.mapvalidator.ValidationResultListener;
import jsettlers.mapcreator.mapvalidator.result.ValidationListModel;
import jsettlers.mapcreator.mapvalidator.result.fix.FixData;
import jsettlers.mapcreator.mapview.MapGraphics;
import jsettlers.mapcreator.stat.StatisticsDialog;
import jsettlers.mapcreator.tools.SetStartpointTool;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.landscape.ResourceTool;
import jsettlers.mapcreator.tools.objects.PlaceBuildingTool;
import jsettlers.mapcreator.tools.shapes.ShapeType;

/**
 * Controller for map editing
 * 
 * @author Andreas Butti
 */
public class EditorControl implements IMapInterfaceListener, ActionFireable, IPlayerSetter, IScrollToAble {

	/**
	 * Map data
	 */
	private MapData data;

	/**
	 * Map drawing
	 */
	private MapGraphics map;

	/**
	 * Currently active tool
	 */
	private Tool tool = null;

	/**
	 * Currently selected player
	 */
	private int currentPlayer = 0;

	/**
	 * Undo / Redo stack
	 */
	private UndoRedoHandler undoRedo;

	/**
	 * To scrol to positions
	 */
	private MapInterfaceConnector connector;

	/**
	 * Header of the current open map
	 */
	private MapFileHeader header;

	/**
	 * Window displayed
	 */
	private EditorFrame window;

	/**
	 * Open GL Contents (Drawing)
	 */
	private MapContent mapContent;

	/**
	 * Sidebar with the tools
	 */
	private ToolSidebar toolSidebar = new ToolSidebar(this) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void changeTool(Tool lastPathComponent) {
			EditorControl.this.changeTool(lastPathComponent);
		}

	};

	/**
	 * Sidebar with all tabs
	 */
	private Sidebar sidebar = new Sidebar(toolSidebar, this);

	/**
	 * Validates the map for errors
	 */
	private MapValidator validator = new MapValidator();

	/**
	 * Timer for redrawing
	 */
	private final Timer redrawTimer = new Timer(true);

	/**
	 * Action to fix all errors automatically, if clear what to do
	 */
	private AutoFixErrorAction autoFixErrorAction;

	/**
	 * Always display resources
	 */
	private boolean showResourcesAlways = false;

	/**
	 * Show the resources because the current tool requests it
	 */
	private boolean showResourcesBecauseOfTool = false;

	/**
	 * Constructor
	 * 
	 * @param header
	 *            Header of the file to open
	 * @param ground
	 *            Ground to use for the new map
	 */
	public EditorControl(MapFileHeader header, ELandscapeType ground) {
		init(header, new MapData(header.getWidth(), header.getHeight(), header.getMaxPlayer(), ground));
	}

	/**
	 * Constructor
	 * 
	 * @param loader
	 *            Map to load
	 * @throws MapLoadException
	 */
	public EditorControl(MapLoader loader) throws MapLoadException {
		MapData data = new MapData(loader.getMapData());
		MapFileHeader header = loader.getFileHeader();
		init(header, data);
	}

	/**
	 * Initialize editor, called from constructor
	 * 
	 * @param header
	 *            Header to use
	 * @param mapData
	 *            Map to use
	 */
	private void init(MapFileHeader header, MapData mapData) {
		this.header = header;
		this.data = mapData;

		map = new MapGraphics(data);
		validator.setData(data);
		validator.addListener(sidebar.getErrorSidebar());
		buildMapEditingWindow();

		new LastUsedHandler().saveUsedMapId(header.getUniqueId());

		undoRedo = new UndoRedoHandler(window, data);
		FixData fixData = new FixData(data, undoRedo, validator);
		sidebar.setFixData(fixData);
		autoFixErrorAction.setFixData(fixData);
	}

	/**
	 * Build the Main Window
	 */
	public void buildMapEditingWindow() {
		JPanel root = new JPanel();
		root.setLayout(new BorderLayout(10, 10));

		// map display
		Area area = new Area();
		final Region region = new Region(Region.POSITION_CENTER);
		area.add(region);
		AreaContainer displayPanel = new AreaContainer(area);
		displayPanel.setMinimumSize(new Dimension(640, 480));
		displayPanel.setFocusable(true);
		root.add(displayPanel, BorderLayout.CENTER);

		window = new EditorFrame(root, sidebar) {
			private static final long serialVersionUID = 1L;

			@Override
			protected JComponent createPlayerSelectSelection() {
				Integer[] playerArray = new Integer[data.getPlayerCount()];
				for (int i = 0; i < data.getPlayerCount(); i++) {
					playerArray[i] = i;
				}
				final JComboBox<Integer> playerCombobox = new JComboBox<>(playerArray);
				// use heavyweight component
				playerCombobox.setLightWeightPopupEnabled(false);
				playerCombobox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						currentPlayer = (Integer) playerCombobox.getSelectedItem();
					}
				});
				playerCombobox.setRenderer(new DefaultListCellRenderer() {
					private static final long serialVersionUID = 1L;

					@Override
					public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
						super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

						Integer player = (Integer) value;
						setIcon(new RectIcon(22, new Color(mapContent.getPlayerColor(player.byteValue()).getARGB()), Color.GRAY));
						setText(String.format(EditorLabels.getLabel("general.player_x"), player));

						return this;
					}
				});

				return playerCombobox;
			}

		};
		registerActions();
		window.initMenubarAndToolbar();
		initActions();

		validator.reValidate();

		// window.pack();
		window.setSize(1200, 800);
		window.invalidate();

		window.setFilename(header.getName());

		// center on screen
		window.setLocationRelativeTo(null);

		this.mapContent = new MapContent(new FakeMapGame(map), new SwingSoundPlayer(), new MapEditorControls(new CombiningActionFirerer(this)));
		connector = mapContent.getInterfaceConnector();
		region.setContent(mapContent);

		redrawTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				region.requestRedraw();
			}
		}, 50, 50);

		connector.addListener(this);
		window.setVisible(true);
		displayPanel.requestFocusInWindow();
	}

	/**
	 * Quit this editor instance
	 */
	private void quit() {
		redrawTimer.cancel();
		validator.dispose();
		window.dispose();
	}

	/**
	 * Create a new map editor instance, with a action property file
	 * 
	 * @param file
	 *            File to use
	 * @throws IOException
	 */
	private void createNewMapEditorInstanceWithActionFile(File file) throws IOException {
		String[] args = new String[] { "java", "-splash:splash.png", "-classpath", System.getProperty("java.class.path"),
				MapCreatorApp.class.getName(),
				"--actionconfig=" + file.getAbsolutePath(), "--delete-actionconfig=true" };
		startProcess(args, "game");
	}

	/**
	 * Open an existing file
	 */
	private void openExistingFile() {
		OpenExistingDialog dlg = new OpenExistingDialog(window);
		dlg.setVisible(true);

		if (!dlg.isConfirmed()) {
			return;
		}

		try {
			File temp = File.createTempFile("jsettler-action", ".properties");

			ActionPropertie prop = new ActionPropertie();
			prop.setAction("open");
			prop.setMapId(dlg.getSelectedMapId());
			prop.saveToFile(temp);

			createNewMapEditorInstanceWithActionFile(temp);

		} catch (IOException e) {
			ExceptionHandler.displayError(e, "Failed to start game");
		}
	}

	/**
	 * Create a new map
	 */
	private void createNewFile() {
		NewFileDialog dlg = new NewFileDialog(window);
		dlg.setVisible(true);

		if (!dlg.isConfirmed()) {
			return;
		}

		MapFileHeader header = dlg.getHeader();

		try {
			File temp = File.createTempFile("jsettler-action", ".properties");

			ActionPropertie prop = new ActionPropertie();
			prop.setAction("new");
			prop.setMapName(header.getName());
			prop.setMapDescription(header.getDescription());
			prop.setLanscapeType(dlg.getGroundTypes());
			prop.setWidth(header.getWidth());
			prop.setHeight(header.getHeight());
			prop.setMinPlayerCount(header.getMinPlayer());
			prop.setMaxPlayerCount(header.getMaxPlayer());

			prop.saveToFile(temp);

			createNewMapEditorInstanceWithActionFile(temp);
		} catch (IOException e) {
			ExceptionHandler.displayError(e, "Failed to start game");
		}

		// TODO open a second map does not work, I didn't analyze why yet
		// MapFileHeader header = dlg.getHeader();
		// init(header, new MapData(header.getWidth(), header.getHeight(), header.getMaxPlayer(), dlg.getGroundTypes()));
		// ---------
		// EditorControl control = new EditorControl(dlg.getHeader(), dlg.getGroundTypes());
		// control.window.setLocation(window.getLocation());
		// control.window.setSize(window.getSize());
		// control.window.getSplitter().setDividerLocation(window.getSplitter().getDividerLocation());
	}

	/**
	 * Check if saved, if not ask user
	 * 
	 * @return true to continue, false to cancel
	 */
	private boolean checkSaved() {
		if (!undoRedo.isChangedSinceLastSave()) {
			return true;
		} else {
			int result = JOptionPane.showConfirmDialog(window, EditorLabels.getLabel("ctrl.save-chages"), "JSettler",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				save();
				return true;
			} else if (result == JOptionPane.NO_OPTION) {
				return true;
			}
			// cancel
			return false;
		}
	}

	/**
	 * Register toolbar / menubar actions
	 */
	private void registerActions() {
		window.registerAction("quit", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkSaved()) {
					quit();
					// TODO dispose all window, make all threads deamon, then remove this exit!
					System.exit(0);
				}
			}
		});
		window.registerAction("new", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				createNewFile();
			}
		});
		window.registerAction("open", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				openExistingFile();
			}
		});

		window.registerAction("zoom-in", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				mapContent.zoomIn();
			}
		});
		window.registerAction("zoom-out", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				mapContent.zoomOut();
			}
		});
		window.registerAction("zoom100", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				mapContent.zoom100();
			}
		});
		window.registerAction("show-resources-always", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			{
				putValue(EditorFrame.DISPLAY_CHECKBOX, true);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				Boolean checked = (Boolean) getValue(EditorFrame.CHECKBOX_VALUE);
				if (checked != null && checked) {
					showResourcesAlways = true;
				} else {
					showResourcesAlways = false;
				}

				map.setShowResources(showResourcesAlways | showResourcesBecauseOfTool);
			}
		});

		window.registerAction("save", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});

		window.registerAction("save-as", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog(window, EditorLabels.getLabel("ctrl.save-as-name"));

				if (name != null) {
					header = new MapFileHeader(header.getType(), name, null, header.getDescription(), header.getWidth(),
							header.getHeight(), header.getMinPlayer(), header.getMaxPlayer(), new Date(), header.getBgimage().clone());
					save();
					window.setFilename(name);
				}

			}
		});

		window.registerAction("open-map-folder", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(ResourceManager.getSaveDirectory(), "maps"));
				} catch (IOException e1) {
					ExceptionHandler.displayError(e1, "Could not open map folder");
				}
			}
		});

		window.registerAction("undo", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				undoRedo.undo();
				validator.reValidate();
			}
		});
		window.registerAction("redo", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				undoRedo.redo();
				validator.reValidate();
			}
		});

		window.registerAction("show-statistic", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				StatisticsDialog dlg = new StatisticsDialog(window, data);
				dlg.setVisible(true);
			}
		});
		window.registerAction("show-map-settings", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				editSettings();
			}
		});

		final AbstractAction playAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				play();
			}
		};
		window.registerAction("play", playAction);
		validator.addListener(new ValidationResultListener() {

			@Override
			public void validationFinished(ValidationListModel list) {
				playAction.setEnabled(list.size() == 0);
			}
		});

		window.registerAction("show-tools", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				sidebar.showTools();
			}
		});

		ShowErrorsAction showErrorsAction = new ShowErrorsAction(sidebar);
		window.registerAction("show-errors", showErrorsAction);
		validator.addListener(showErrorsAction);

		GotoNextErrorAction gotoNextErrorAction = new GotoNextErrorAction(this);
		window.registerAction("goto-error", gotoNextErrorAction);
		validator.addListener(gotoNextErrorAction);

		this.autoFixErrorAction = new AutoFixErrorAction();
		window.registerAction("auto-fix-error", autoFixErrorAction);
		validator.addListener(autoFixErrorAction);
	}

	/**
	 * Set some actions disabled per default, will be enabled when they are available
	 */
	private void initActions() {
		window.enableAction("save", false);
		window.enableAction("undo", false);
		window.enableAction("redo", false);
	}

	/**
	 * Display the map settings dialog
	 */
	protected void editSettings() {
		SettingsDialog dlg = new SettingsDialog(window, header) {
			private static final long serialVersionUID = 1L;

			@Override
			public void applyNewHeader(MapFileHeader header) {
				EditorControl.this.header = header;
				data.setMaxPlayers(header.getMaxPlayer());
				validator.reValidate();
			}

		};
		dlg.setVisible(true);
	}

	/**
	 * Save current map
	 */
	protected void save() {
		try {
			MapFileHeader imagedHeader = generateMapHeader();
			new LastUsedHandler().saveUsedMapId(imagedHeader.getUniqueId());

			data.doPreSaveActions();
			CommonConstants.USE_SAVEGAME_COMPRESSION = false;
			MapList.getDefaultList().saveNewMap(imagedHeader, data, null);
			undoRedo.setSaved();
		} catch (Throwable e) {
			ExceptionHandler.displayError(e, "Error saving");
		}
	}

	private MapFileHeader generateMapHeader() {
		short[] image = new PreviewImageCreator(header.getWidth(), header.getHeight(), MapFileHeader.PREVIEW_IMAGE_SIZE,
				data.getPreviewImageDataSupplier()).getPreviewImage();
		MapFileHeader imagedHeader = new MapFileHeader(header.getType(), header.getName(), header.getBaseMapId(), header.getDescription(),
				header.getWidth(), header.getHeight(), header.getMinPlayer(), header.getMaxPlayer(), new Date(), image);
		return imagedHeader;
	}

	/**
	 * Start Another process
	 * 
	 * @param args
	 *            Arguments
	 * @param name
	 *            For Log output and thread ID
	 * @throws IOException
	 */
	protected void startProcess(String[] args, final String name) throws IOException {
		System.out.println("Starting process:");
		for (String arg : args) {
			System.out.print(arg + " ");
		}
		System.out.println();

		ProcessBuilder builder = new ProcessBuilder(args);
		builder.redirectErrorStream(true);
		final Process process = builder.start();

		Thread streamReader = new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

				while (true) {
					String line;
					try {
						line = reader.readLine();
					} catch (IOException e) {
						break;
					}
					if (line == null) {
						break;
					}
					System.out.println(name + " " + line);
				}
			}
		}, "ExecThread " + name);
		streamReader.setDaemon(true);
	}

	/**
	 * Play the game in a new process
	 */
	protected void play() {
		try {
			File temp = File.createTempFile("tmp_map", "");
			data.doPreSaveActions();
			MapList.getDefaultList().saveNewMap(generateMapHeader(), data, new FileOutputStream(temp));

			String[] args = new String[] { "java", "-classpath", System.getProperty("java.class.path"), SwingManagedJSettlers.class.getName(),
					"--mapfile=" + temp.getAbsolutePath(), "--control-all", "--activate-all-players" };
			startProcess(args, "game");
		} catch (IOException e) {
			ExceptionHandler.displayError(e, "Failed to start game");
		}
	}

	/**
	 * Set the selected tool
	 * 
	 * @param lastPathComponent
	 */
	protected void changeTool(Tool lastPathComponent) {
		tool = lastPathComponent;
		toolSidebar.updateShapeSettings(tool);

		showResourcesBecauseOfTool = false;
		if (tool != null) {
			// if the resource tool is used they should be displayed
			showResourcesBecauseOfTool |= tool instanceof ResourceTool;

			if (tool instanceof PlaceBuildingTool) {
				PlaceBuildingTool pbt = (PlaceBuildingTool) tool;
				EBuildingType type = pbt.getType();

				// display resources for Mines and Fisher
				if (type.isMine() || type == EBuildingType.FISHER) {
					showResourcesBecauseOfTool = true;
				}
			}
		}

		map.setShowResources(showResourcesAlways | showResourcesBecauseOfTool);
	}

	/**
	 * This listener is called from different Thread, all swing calls have to be from event dispatcher thread!
	 */
	@Override
	public void action(final Action action) {
		System.out.println("Got action: " + action.getActionType());
		if (action.getActionType() == EActionType.SELECT_AREA) {
			// IMapArea area = ((SelectAreaAction) action).getArea();
		} else if (action instanceof DrawLineAction) {
			if (tool != null && !(tool instanceof SetStartpointTool)) {
				DrawLineAction lineAction = (DrawLineAction) action;

				// only getter call, no Swing calls
				ShapeType shape = toolSidebar.getActiveShape();

				tool.apply(data, shape, lineAction.getStart(), lineAction.getEnd(), lineAction.getUidy());

				validator.reValidate();
			}
		} else if (action instanceof StartDrawingAction) {
			if (tool != null && !(tool instanceof SetStartpointTool)) {
				StartDrawingAction lineAction = (StartDrawingAction) action;

				// only getter call, no Swing calls
				ShapeType shape = toolSidebar.getActiveShape();

				tool.start(data, shape, lineAction.getPos());

				validator.reValidate();
			}
		} else if (action instanceof EndDrawingAction) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					undoRedo.endUseStep();
					validator.reValidate();
				}
			});
		} else if (action instanceof AbortDrawingAction) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					MapDataDelta delta = data.getUndoDelta();
					data.apply(delta);
					data.resetUndoDelta();
					validator.reValidate();
				}
			});
		} else if (action.getActionType() == EActionType.SELECT_POINT) {
			if (tool != null) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						PointAction lineAction = (PointAction) action;

						ShapeType shape = toolSidebar.getActiveShape();

						tool.start(data, shape, lineAction.getPosition());
						tool.apply(data, shape, lineAction.getPosition(), lineAction.getPosition(), 0);

						undoRedo.endUseStep();
						validator.reValidate();
					}
				});
			}
		}
	}

	@Override
	public void fireAction(Action action) {
		action(action);
	}

	@Override
	public int getActivePlayer() {
		return currentPlayer;
	}

	@Override
	public void scrollTo(ShortPoint2D pos) {
		if (pos != null) {
			connector.scrollTo(pos, true);
		}
	}
}
