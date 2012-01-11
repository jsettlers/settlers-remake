package jsettlers.mapcreator.main;

import go.graphics.area.Area;
import go.graphics.region.Region;
import go.graphics.swing.AreaContainer;
import go.graphics.swing.sound.SwingSoundPlayer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.ActionFirerer;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.data.MapDataDelta;
import jsettlers.mapcreator.data.MapDataSerializer;
import jsettlers.mapcreator.main.DataTester.TestResultReceiver;
import jsettlers.mapcreator.mapview.MapGraphics;
import jsettlers.mapcreator.tools.FixHeightsTool;
import jsettlers.mapcreator.tools.FlatLandscapeTool;
import jsettlers.mapcreator.tools.HeightAdder;
import jsettlers.mapcreator.tools.LandscapeHeightTool;
import jsettlers.mapcreator.tools.LineCircleShape;
import jsettlers.mapcreator.tools.PlaceBuildingTool;
import jsettlers.mapcreator.tools.PlaceMapObjectTool;
import jsettlers.mapcreator.tools.PlaceMovableTool;
import jsettlers.mapcreator.tools.PlaceStackTool;
import jsettlers.mapcreator.tools.SetLandscapeTool;
import jsettlers.mapcreator.tools.ShapeType;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.ToolBox;
import jsettlers.mapcreator.tools.ToolNode;

public class EditorWindow implements IMapInterfaceListener, ActionFireable,
        TestResultReceiver, IPlayerSetter {

	private final class RadiusChangeListener implements ChangeListener {
		private final LineCircleShape shape;

		private RadiusChangeListener(LineCircleShape shape) {
			this.shape = shape;
		}

		@Override
		public void stateChanged(ChangeEvent arg0) {
			shape.setRadius(((JSlider) arg0.getSource()).getModel().getValue());
		}
	}

	private final class ShapeActionListener implements ActionListener {
		private final ShapeType shape;

		private ShapeActionListener(ShapeType shape) {
			this.shape = shape;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			setShape(shape);
		}
	}

	private LinkedList<ShapeType> lastUsed = new LinkedList<ShapeType>();

	//@formatter:off
	private final ToolNode TOOLBOX = new ToolBox("Werkzege",
	        new ToolNode[] {
		        new ToolBox("Landschaft", new ToolNode[] {
		                new SetLandscapeTool(ELandscapeType.GRASS, false),
		                new SetLandscapeTool(ELandscapeType.DRY_GRASS, false),
		                new SetLandscapeTool(ELandscapeType.SAND, false),
		                new SetLandscapeTool(ELandscapeType.WATER1, false),
		                new SetLandscapeTool(ELandscapeType.RIVER1, true),
		                new SetLandscapeTool(ELandscapeType.RIVER2, true),
		                new SetLandscapeTool(ELandscapeType.RIVER3, true),
		                new SetLandscapeTool(ELandscapeType.RIVER4, true),
		                new SetLandscapeTool(ELandscapeType.MOUNTAIN, false),
		                new SetLandscapeTool(ELandscapeType.SNOW, false),
		        }),
		        new ToolBox("Höhen", new ToolNode[] {
		        		new LandscapeHeightTool(),
		        		new HeightAdder(true),
		        		new HeightAdder(false),
		        		new FlatLandscapeTool(),
		        		new FixHeightsTool(),
		        }),
		        new ToolBox("Objekte", new ToolNode[] {
		        		new PlaceMapObjectTool(MapTreeObject.getInstance()),
		        		new PlaceMapObjectTool(MapStoneObject.getInstance(0)),
		        		new PlaceMapObjectTool(MapStoneObject.getInstance(1)),
		        		new PlaceMapObjectTool(MapStoneObject.getInstance(2)),
		        		new PlaceMapObjectTool(MapStoneObject.getInstance(3)),
		        		new PlaceMapObjectTool(MapStoneObject.getInstance(4)),
		        		new PlaceMapObjectTool(MapStoneObject.getInstance(5)),
		        		new PlaceMapObjectTool(MapStoneObject.getInstance(6)),
		        		new PlaceMapObjectTool(MapStoneObject.getInstance(7)),
		        		new PlaceMapObjectTool(MapStoneObject.getInstance(8)),
		        		new PlaceMapObjectTool(MapStoneObject.getInstance(9)),
		        		new PlaceMapObjectTool(MapStoneObject.getInstance(10)),
		        }),
		        new ToolBox("Siedler", new ToolNode[] {
				        new ToolBox("Arbeiter", new ToolNode[] {
			        		new PlaceMovableTool(EMovableType.BEARER, this),
			        		new PlaceMovableTool(EMovableType.BRICKLAYER, this),
			        		new PlaceMovableTool(EMovableType.DIGGER, this),
			        		new PlaceMovableTool(EMovableType.SMITH, this),
				        }),
				        new ToolBox("Arbeiter", new ToolNode[] {
			        		new PlaceMovableTool(EMovableType.GEOLOGIST, this),
			        		new PlaceMovableTool(EMovableType.PIONEER, this),
			        		new PlaceMovableTool(EMovableType.THIEF, this)
				        }),
				        new ToolBox("Krieger", new ToolNode[] {
			        		new PlaceMovableTool(EMovableType.SWORDSMAN_L1, this),
			        		new PlaceMovableTool(EMovableType.SWORDSMAN_L2, this),
			        		new PlaceMovableTool(EMovableType.SWORDSMAN_L3, this),
			        		new PlaceMovableTool(EMovableType.BOWMAN_L1, this),
			        		new PlaceMovableTool(EMovableType.BOWMAN_L2, this),
			        		new PlaceMovableTool(EMovableType.BOWMAN_L3, this),
			        		new PlaceMovableTool(EMovableType.PIKEMAN_L1, this),
			        		new PlaceMovableTool(EMovableType.PIKEMAN_L2, this),
			        		new PlaceMovableTool(EMovableType.PIKEMAN_L3, this),
				        }),
		        }),
		        new ToolBox("Materialien", new ToolNode[] {
				        new ToolBox("Bauen", new ToolNode[] {
			        		new PlaceStackTool(EMaterialType.PLANK, 8),
			        		new PlaceStackTool(EMaterialType.STONE, 8),
			        		new PlaceStackTool(EMaterialType.TRUNK, 8),
				        }),
					    new ToolBox("Essen", new ToolNode[] {
			        		new PlaceStackTool(EMaterialType.BREAD, 8),
			        		new PlaceStackTool(EMaterialType.CROP, 8),
			        		new PlaceStackTool(EMaterialType.FISH, 8),
			        		new PlaceStackTool(EMaterialType.FISHINGROD, 8),
			        		new PlaceStackTool(EMaterialType.FLOUR, 8),
			        		new PlaceStackTool(EMaterialType.PIG, 8),
			        		new PlaceStackTool(EMaterialType.WATER, 8),
			        		new PlaceStackTool(EMaterialType.WINE, 8),
				        }),
					    new ToolBox("Rohstoffe", new ToolNode[] {
			        		new PlaceStackTool(EMaterialType.COAL, 8),
			        		new PlaceStackTool(EMaterialType.IRON, 8),
			        		new PlaceStackTool(EMaterialType.IRONORE, 8),
			        		new PlaceStackTool(EMaterialType.GOLD, 8),
			        		new PlaceStackTool(EMaterialType.GOLDORE, 8),
				        }),
					    new ToolBox("Werkzeug", new ToolNode[] {
			        		new PlaceStackTool(EMaterialType.HAMMER, 8),
			        		new PlaceStackTool(EMaterialType.BLADE, 8),
			        		new PlaceStackTool(EMaterialType.AXE, 8),
			        		new PlaceStackTool(EMaterialType.SAW, 8),
			        		new PlaceStackTool(EMaterialType.PICK, 8),
			        		new PlaceStackTool(EMaterialType.SCYTHE, 8),
				        }),
					    new ToolBox("Waffen", new ToolNode[] {
			        		new PlaceStackTool(EMaterialType.SWORD, 8),
			        		new PlaceStackTool(EMaterialType.BOW, 8),
			        		new PlaceStackTool(EMaterialType.SPEAR, 8),
				        }),
		        }),
		        new ToolBox("Gebäude", new ToolNode[] {
				        new ToolBox("Rohstoffe", new ToolNode[] {
				        		new PlaceBuildingTool(EBuildingType.LUMBERJACK, this),
				        		new PlaceBuildingTool(EBuildingType.SAWMILL, this),
				        		new PlaceBuildingTool(EBuildingType.STONECUTTER, this),
				        		new PlaceBuildingTool(EBuildingType.FORESTER, this),
				        		new PlaceBuildingTool(EBuildingType.IRONMELT, this),
				        		new PlaceBuildingTool(EBuildingType.IRONMINE, this),
				        		new PlaceBuildingTool(EBuildingType.GOLDMELT, this),
				        		new PlaceBuildingTool(EBuildingType.GOLDMINE, this),
				        		new PlaceBuildingTool(EBuildingType.COALMINE, this),
				        		new PlaceBuildingTool(EBuildingType.CHARCOAL_BURNER, this),
				        }),
				        new ToolBox("Militär", new ToolNode[] {
				        		new PlaceBuildingTool(EBuildingType.TOWER, this),
				        		new PlaceBuildingTool(EBuildingType.BIG_TOWER, this),
				        		new PlaceBuildingTool(EBuildingType.CASTLE, this),
				        }),
		        }),
	        });
	//@formatter:on

	private final MapData data;
	private final MapGraphics map;
	private Tool tool = null;
	private ShapeType activeShape = null;
	private JPanel shapeButtons;
	private JPanel shapeSettings;

	private byte currentPlayer = 0;

	private ISPosition2D testFailPoint = null;

	private JButton undoButton;

	private LinkedList<MapDataDelta> undoDeltas =
	        new LinkedList<MapDataDelta>();

	private DataTester dataTester;

	private MapInterfaceConnector connector;

	private JTextField testResult;

	private JButton startGameButton;

	public EditorWindow(int width, int height, int playerCount) {
		data = new MapData(width, height, playerCount);
		map = new MapGraphics(data);

		startMapEditing();
		dataTester = new DataTester(data, this);
	}

	public void startMapEditing() {
		JFrame window = new JFrame("map editor");
		JPanel root = new JPanel();
		root.setLayout(new BorderLayout(10, 10));

		// map
		Area area = new Area();
		final Region region = new Region(Region.POSITION_CENTER);
		area.add(region);
		AreaContainer panel = new AreaContainer(area);
		panel.setPreferredSize(new Dimension(640, 480));
		panel.requestFocusInWindow();
		panel.setFocusable(true);
		root.add(panel, BorderLayout.CENTER);

		// menu
		JPanel menu = createMenu();
		menu.setPreferredSize(new Dimension(300, 800));
		root.add(menu, BorderLayout.EAST);

		// menu
		JToolBar toolbar = createToolbar();
		root.add(toolbar, BorderLayout.NORTH);

		// window
		window.add(root);
		window.pack();
		window.setSize(1200, 800);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setLocationRelativeTo(null);

		MapContent content =
		        new MapContent(map, new SwingSoundPlayer(),
		                new MapEditorControls(new ActionFirerer(this)));
		connector = content.getInterfaceConnector();
		region.setContent(content);

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				region.requestRedraw();
			}
		}, 50, 50);

		connector.addListener(this);
	}

	private JToolBar createToolbar() {
		JToolBar bar = new JToolBar();

		undoButton = new JButton("Undo");
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				undo();
			}
		});
		undoButton.setEnabled(false);
		bar.add(undoButton);

		testResult = new JTextField();
		testResult.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (testFailPoint != null) {
					connector.scrollTo(testFailPoint, true);
				}
			}
		});
		bar.add(testResult);

		final SpinnerNumberModel model =
		        new SpinnerNumberModel(0, 0, data.getPlayerCount() - 1, 1);
		JSpinner playerSpinner = new JSpinner(model);
		playerSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				currentPlayer = model.getNumber().byteValue();
			}
		});
		bar.add(playerSpinner);

		startGameButton = new JButton("Play");
		startGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				play();
			}
		});
		bar.add(startGameButton);
		return bar;
	}

	protected void play() {
		try {
			File temp = File.createTempFile("tmp_map", "");
			MapDataSerializer.serialize(data, new FileOutputStream(temp));

			String[] args =
			        new String[] {
			                "java",
			                "-classpath",
			                System.getProperty("java.class.path"),
			                "jsettlers.mapcreator.main.PlayProcess",
			                temp.getAbsolutePath(),
			        };

			System.out.println("Starting process:");
			for (String arg : args) {
				System.out.print(arg + " ");
			}
			System.out.println();
			
			ProcessBuilder builder = new ProcessBuilder(args);
			builder.directory(new File("").getAbsoluteFile());
			builder.redirectErrorStream(true);
			final Process process = builder.start();

			new Thread(new Runnable() {
				@Override
				public void run() {
					BufferedReader reader =
					        new BufferedReader(new InputStreamReader(
					                process.getInputStream()));

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
						System.out.println(line);
					}
				}
			}, "run game process");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void undo() {
		if (!undoDeltas.isEmpty()) {
			MapDataDelta delta = undoDeltas.pollLast();

			data.apply(delta);

		}
		if (undoDeltas.isEmpty()) {
			undoButton.setEnabled(false);
		}
	}

	/**
	 * Ends a use step of a tool: creates a diff to the last step.
	 */
	private void endUseStep() {
		MapDataDelta delta = data.getUndoDelta();
		data.resetUndoDelta();
		undoDeltas.add(delta);
		undoButton.setEnabled(true);
	}

	private JPanel createMenu() {
		JPanel menu = new JPanel();
		menu.setLayout(new BorderLayout());

		JTree toolshelf = new JTree(new ToolTreeModel(TOOLBOX));
		menu.add(toolshelf, BorderLayout.CENTER);
		toolshelf.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent arg0) {
				Object lastPathComponent =
				        arg0.getNewLeadSelectionPath().getLastPathComponent();
				if (lastPathComponent instanceof Tool) {
					changeTool((Tool) lastPathComponent);
				}
			}
		});
		toolshelf.setCellRenderer(new ToolRenderer());
		toolshelf
		        .setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

		JPanel shape = new JPanel();
		shape.setLayout(new BoxLayout(shape, BoxLayout.Y_AXIS));
		shape.setBorder(BorderFactory.createTitledBorder("Formen"));
		shapeButtons = new JPanel();
		shape.add(shapeButtons);
		shapeSettings = new JPanel();
		shape.add(shapeSettings);
		menu.add(shape, BorderLayout.SOUTH);

		return menu;
	}

	protected void changeTool(Tool lastPathComponent) {
		tool = lastPathComponent;
		updateShapeButtons();
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
			setShape(shape);
		} else {
			setShape(null);
		}
	}

	private void updateShapeButtons() {
		shapeButtons.removeAll();
		if (tool != null) {
			ButtonGroup shapeGroup = new ButtonGroup();
			for (ShapeType shape : tool.getShapes()) {
				JToggleButton button = new JToggleButton(shape.getName());
				button.setSelected(shape == activeShape);
				button.addActionListener(new ShapeActionListener(shape));
				shapeGroup.add(button);
				shapeButtons.add(button);
			}
		}
		shapeButtons.revalidate();
	}

	protected void setShape(ShapeType shape) {
		activeShape = shape;
		// updateShapeButtons();
		shapeSettings.removeAll();
		if (shape instanceof LineCircleShape) {
			final LineCircleShape shape2 = (LineCircleShape) shape;
			JSlider radiusSelector = new JSlider(1, 50, shape2.getRadius());
			radiusSelector.addChangeListener(new RadiusChangeListener(shape2));
			shapeSettings.add(radiusSelector);
		}
		shapeSettings.revalidate();
	}

	@Override
	public void action(Action action) {
		System.out.println("Got action: " + action.getActionType());
		if (action.getActionType() == EActionType.SELECT_AREA) {
			// IMapArea area = ((SelectAreaAction) action).getArea();
		} else if (action instanceof DrawLineAction) {
			if (tool != null) {
				DrawLineAction lineAction = (DrawLineAction) action;

				ShapeType shape = getActiveShape();

				tool.apply(data, shape, lineAction.getStart(),
				        lineAction.getEnd(), lineAction.getUidy());
			}
		} else if (action instanceof StartDrawingAction) {
			if (tool != null) {
				StartDrawingAction lineAction = (StartDrawingAction) action;

				ShapeType shape = getActiveShape();

				tool.start(data, shape, lineAction.getPos());
			}
		} else if (action instanceof EndDrawingAction) {
			endUseStep();
			dataTester.retest();
		}
	}

	private ShapeType getActiveShape() {
		return activeShape;
	}

	@Override
	public void fireAction(Action action) {
		action(action);
	}

	@Override
	public void testResult(String result, boolean allowed,
	        ShortPoint2D failPoint) {
		testFailPoint = failPoint;
		startGameButton.setEnabled(allowed);
		testResult.setText(result);
	}

	@Override
	public byte getActivePlayer() {
		return currentPlayer;
	}
}
