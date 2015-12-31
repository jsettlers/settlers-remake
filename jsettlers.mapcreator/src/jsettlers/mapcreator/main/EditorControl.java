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
package jsettlers.mapcreator.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import go.graphics.area.Area;
import go.graphics.region.Region;
import go.graphics.swing.AreaContainer;
import go.graphics.swing.sound.SwingSoundPlayer;
import jsettlers.algorithms.previewimage.PreviewImageCreator;
import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.position.ShortPoint2D;
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
import jsettlers.mapcreator.main.DataTester.TestResultReceiver;
import jsettlers.mapcreator.main.action.AbortDrawingAction;
import jsettlers.mapcreator.main.action.CombiningActionFirerer;
import jsettlers.mapcreator.main.action.DrawLineAction;
import jsettlers.mapcreator.main.action.EndDrawingAction;
import jsettlers.mapcreator.main.action.StartDrawingAction;
import jsettlers.mapcreator.main.error.IScrollToAble;
import jsettlers.mapcreator.main.error.ShowErrorsAction;
import jsettlers.mapcreator.main.map.MapEditorControls;
import jsettlers.mapcreator.main.window.EditorFrame;
import jsettlers.mapcreator.main.window.SettingsDialog;
import jsettlers.mapcreator.main.window.sidebar.Sidebar;
import jsettlers.mapcreator.main.window.sidebar.ToolSidebar;
import jsettlers.mapcreator.mapview.MapGraphics;
import jsettlers.mapcreator.stat.StatisticsDialog;
import jsettlers.mapcreator.tools.SetStartpointTool;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.landscape.ResourceTool;
import jsettlers.mapcreator.tools.shapes.ShapeType;

/**
 * Controller for map editing
 * 
 * @author Andreas Butti
 */
public class EditorControl implements IMapInterfaceListener, ActionFireable, TestResultReceiver, IPlayerSetter, IScrollToAble {

	private static final int MAX_UNDO = 100;

	private final LinkedList<ShapeType> lastUsed = new LinkedList<ShapeType>();

	private final MapData data;
	private final MapGraphics map;
	private Tool tool = null;

	private byte currentPlayer = 0;

	/**
	 * Last failure point to jump to
	 */
	private ShortPoint2D testFailPoint = null;

	private final LinkedList<MapDataDelta> undoDeltas = new LinkedList<MapDataDelta>();

	private final LinkedList<MapDataDelta> redoDeltas = new LinkedList<MapDataDelta>();

	private final DataTester dataTester;

	private MapInterfaceConnector connector;

	/**
	 * Header of the current open map
	 */
	private MapFileHeader header;

	/**
	 * Window displayed
	 */
	private EditorFrame window;

	private AbstractAction gotoErrorAction;

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
	private Sidebar sidebar = new Sidebar(toolSidebar);

	/**
	 * Constructor
	 * 
	 * @param header
	 *            Header of the file to open
	 * @param ground
	 */
	public EditorControl(MapFileHeader header, ELandscapeType ground) {
		this.header = header;
		short width = header.getWidth();
		short height = header.getHeight();
		short playerCount = header.getMaxPlayer();
		data = new MapData(width, height, playerCount, ground);
		map = new MapGraphics(data);

		dataTester = new DataTester(data, this);
		init();
	}

	public EditorControl(MapLoader loader) throws MapLoadException {
		data = new MapData(loader.getMapData());
		header = loader.getFileHeader();
		map = new MapGraphics(data);
		dataTester = new DataTester(data, this);
		init();
	}

	/**
	 * Initialize editor, called from constructor
	 */
	private void init() {
		buildMapEditingWindow();
		dataTester.start();
		sidebar.initErrorTab(dataTester.getErrorList(), this);
	}

	public void buildMapEditingWindow() {
		window = new EditorFrame() {
			private static final long serialVersionUID = 1L;

			@Override
			protected JSpinner createPlayerSelectSpinner() {
				final SpinnerNumberModel model = new SpinnerNumberModel(0, 0, data.getPlayerCount() - 1, 1);
				JSpinner playerSpinner = new JSpinner(model);
				playerSpinner.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						currentPlayer = model.getNumber().byteValue();
					}
				});
				return playerSpinner;
			}

		};
		registerActions();
		window.initMenubarAndToolbar();
		JPanel root = new JPanel();
		root.setLayout(new BorderLayout(10, 10));

		// map
		Area area = new Area();
		final Region region = new Region(Region.POSITION_CENTER);
		area.add(region);
		AreaContainer displayPanel = new AreaContainer(area);
		displayPanel.setMinimumSize(new Dimension(640, 480));
		displayPanel.requestFocusInWindow();
		displayPanel.setFocusable(true);
		root.add(displayPanel, BorderLayout.CENTER);

		// toolbar
		initToolbar();

		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, root, sidebar);
		// window
		window.add(splitter, BorderLayout.CENTER);
		splitter.setDividerLocation(1000);

		// window.pack();
		window.setSize(1200, 800);
		window.invalidate();

		window.setFilename(header.getName());

		window.setLocationRelativeTo(null);

		this.mapContent = new MapContent(new FakeMapGame(map), new SwingSoundPlayer(), new MapEditorControls(new CombiningActionFirerer(this)));
		connector = mapContent.getInterfaceConnector();
		region.setContent(mapContent);

		new Timer(true).schedule(new TimerTask() {
			@Override
			public void run() {
				region.requestRedraw();
			}
		}, 50, 50);

		connector.addListener(this);
		window.setVisible(true);
	}

	/**
	 * Register toolbar / menubar actions
	 */
	private void registerActions() {
		window.registerAction("quit", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (undoDeltas.isEmpty()) {
					window.dispose();
					// TODO dispose all window, make all threads deamon, then remove this exit!
					System.exit(0);
				} else {
					int result = JOptionPane.showConfirmDialog(window, "Save changes?", "JSettler", JOptionPane.YES_NO_CANCEL_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						save();
						window.dispose();

						// TODO dispose all window, make all threads deamon, then remove this exit!
						System.exit(0);
					} else if (result == JOptionPane.NO_OPTION) {
						window.dispose();

						// TODO dispose all window, make all threads deamon, then remove this exit!
						System.exit(0);
					}
					// else: cancel, do nothing
				}
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

		window.registerAction("save", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});

		window.registerAction("undo", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				undo();
			}
		});
		window.registerAction("redo", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				redo();
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

		window.registerAction("play", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				play();
			}
		});

		window.registerAction("show-tools", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				sidebar.showTools();
			}
		});

		ShowErrorsAction showErrorsAction = new ShowErrorsAction(dataTester.getErrorList(), sidebar);
		window.registerAction("show-errors", showErrorsAction);
		showErrorsAction.updateText();

		this.gotoErrorAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			{
				putValue(EditorFrame.DISPLAY_TEXT_IN_TOOLBAR, true);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (testFailPoint != null) {
					connector.scrollTo(testFailPoint, true);
				}
			}
		};
		window.registerAction("goto-error", gotoErrorAction);
	}

	private void initToolbar() {
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
				dataTester.retest();
			}

		};
		dlg.setVisible(true);
	}

	protected void save() {
		try {
			MapFileHeader imagedHeader = generateMapHeader();
			data.doPreSaveActions();
			CommonConstants.USE_SAVEGAME_COMPRESSION = false;
			MapList.getDefaultList().saveNewMap(imagedHeader, data, null);
		} catch (Throwable e) {
			ErrorDisplay.displayError(e, "Error saving");
		}
	}

	private MapFileHeader generateMapHeader() {
		short[] image = new PreviewImageCreator(header.getWidth(), header.getHeight(), MapFileHeader.PREVIEW_IMAGE_SIZE,
				data.getPreviewImageDataSupplier()).getPreviewImage();
		MapFileHeader imagedHeader = new MapFileHeader(header.getType(), header.getName(), header.getBaseMapId(), header.getDescription(),
				header.getWidth(), header.getHeight(), header.getMinPlayer(), header.getMaxPlayer(), new Date(), image);
		return imagedHeader;
	}

	protected void play() {
		try {
			File temp = File.createTempFile("tmp_map", "");
			data.doPreSaveActions();
			MapList.getDefaultList().saveNewMap(generateMapHeader(), data, new FileOutputStream(temp));

			String[] args = new String[] { "java", "-classpath", System.getProperty("java.class.path"), SwingManagedJSettlers.class.getName(),
					"--mapfile=" + temp.getAbsolutePath(), "--control-all", "--activate-all-players" };

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
						System.out.println("Running game: " + line);
					}
				}
			}, "run game process");
			streamReader.setDaemon(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void undo() {
		if (!undoDeltas.isEmpty()) {
			MapDataDelta delta = undoDeltas.pollLast();

			MapDataDelta inverse = data.apply(delta);

			redoDeltas.addLast(inverse);
			window.enableAction("redo", true);
		}
		if (undoDeltas.isEmpty()) {
			window.enableAction("undo", false);
			window.enableAction("save", false);
		}
	}

	protected void redo() {
		if (!redoDeltas.isEmpty()) {
			MapDataDelta delta = redoDeltas.pollLast();

			MapDataDelta inverse = data.apply(delta);

			undoDeltas.addLast(inverse);
			window.enableAction("undo", true);
			window.enableAction("save", true);
		}
		if (redoDeltas.isEmpty()) {
			window.enableAction("redo", false);
		}
	}

	/**
	 * Ends a use step of a tool: creates a diff to the last step.
	 */
	private void endUseStep() {
		MapDataDelta delta = data.getUndoDelta();
		data.resetUndoDelta();
		if (undoDeltas.size() >= MAX_UNDO) {
			undoDeltas.removeFirst();
		}
		undoDeltas.add(delta);
		redoDeltas.clear();
		window.enableAction("undo", true);
		window.enableAction("redo", false);

		window.enableAction("save", true);
	}

	protected void changeTool(Tool lastPathComponent) {
		tool = lastPathComponent;
		toolSidebar.updateShapeButtons(tool);
		if (tool != null) {
			ShapeType shape = tool.getShapes()[0];

			List<ShapeType> shapes = Arrays.asList(tool.getShapes());
			for (ShapeType used : lastUsed) {
				if (shapes.contains(used)) {
					shape = used;
					break;
				}
			}
			lastUsed.remove(shape);
			lastUsed.addFirst(shape);

			map.setShowResources(tool instanceof ResourceTool);

			toolSidebar.setShape(shape);
		} else {
			toolSidebar.setShape(null);
		}
	}

	@Override
	public void action(Action action) {
		System.out.println("Got action: " + action.getActionType());
		if (action.getActionType() == EActionType.SELECT_AREA) {
			// IMapArea area = ((SelectAreaAction) action).getArea();
		} else if (action instanceof DrawLineAction) {
			if (tool != null && !(tool instanceof SetStartpointTool)) {
				DrawLineAction lineAction = (DrawLineAction) action;

				ShapeType shape = toolSidebar.getActiveShape();

				tool.apply(data, shape, lineAction.getStart(), lineAction.getEnd(), lineAction.getUidy());

				dataTester.retest();
			}
		} else if (action instanceof StartDrawingAction) {
			if (tool != null && !(tool instanceof SetStartpointTool)) {
				StartDrawingAction lineAction = (StartDrawingAction) action;

				ShapeType shape = toolSidebar.getActiveShape();

				tool.start(data, shape, lineAction.getPos());

				dataTester.retest();
			}
		} else if (action instanceof EndDrawingAction) {
			endUseStep();
			dataTester.retest();
		} else if (action instanceof AbortDrawingAction) {
			MapDataDelta delta = data.getUndoDelta();
			data.apply(delta);
			data.resetUndoDelta();
			dataTester.retest();
		} else if (action.getActionType() == EActionType.SELECT_POINT) {
			if (tool != null) {
				PointAction lineAction = (PointAction) action;

				ShapeType shape = toolSidebar.getActiveShape();

				tool.start(data, shape, lineAction.getPosition());
				tool.apply(data, shape, lineAction.getPosition(), lineAction.getPosition(), 0);

				endUseStep();
				dataTester.retest();
			}
		}
	}

	@Override
	public void fireAction(Action action) {
		action(action);
	}

	@Override
	public void testResult(String result, boolean successful, ShortPoint2D failPoint) {
		testFailPoint = failPoint;
		window.enableAction("play", successful);

		if (successful) {
			gotoErrorAction.putValue(javax.swing.Action.NAME, EditorLabels.getLabel("no-errors"));
			gotoErrorAction.setEnabled(false);
		} else {
			gotoErrorAction.putValue(javax.swing.Action.NAME, result);
			gotoErrorAction.setEnabled(true);
		}
	}

	@Override
	public byte getActivePlayer() {
		return currentPlayer;
	}

	@Override
	public void scrollTo(ShortPoint2D pos) {
		if (pos != null) {
			connector.scrollTo(pos, true);
		}
	}
}
