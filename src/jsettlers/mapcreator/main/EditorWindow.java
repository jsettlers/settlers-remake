package jsettlers.mapcreator.main;

import go.graphics.area.Area;
import go.graphics.region.Region;
import go.graphics.swing.AreaContainer;
import go.graphics.swing.sound.SwingSoundPlayer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.ActionFirerer;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.SelectAreaAction;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.mapview.MapGraphics;
import jsettlers.mapcreator.tools.HeightAdder;
import jsettlers.mapcreator.tools.LandscapeHeightTool;
import jsettlers.mapcreator.tools.PlaceMapObjectTool;
import jsettlers.mapcreator.tools.PlaceMovableTool;
import jsettlers.mapcreator.tools.SetLandscapeTool;
import jsettlers.mapcreator.tools.ShapeType;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.ToolBox;
import jsettlers.mapcreator.tools.ToolNode;

public class EditorWindow implements IMapInterfaceListener, ActionFireable {

	//@formatter:off
	private static final ToolNode TOOLBOX = new ToolBox("Werkzege",
	        new ToolNode[] {
		        new ToolBox("Landschaft", new ToolNode[] {
		                new SetLandscapeTool(ELandscapeType.GRASS, false),
		                new SetLandscapeTool(ELandscapeType.DRY_GRASS, false),
		                new SetLandscapeTool(ELandscapeType.SAND, false),
		                new SetLandscapeTool(ELandscapeType.WATER, false),
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
		        		new PlaceMovableTool(EMovableType.BEARER),
		        		new PlaceMovableTool(EMovableType.SWORDSMAN_L1),
		        		new PlaceMovableTool(EMovableType.SWORDSMAN_L2),
		        		new PlaceMovableTool(EMovableType.SWORDSMAN_L3),
		        }),
	        });
	//@formatter:on
	private final MapData data;
	private final MapGraphics map;
	private ELandscapeType currentLandscape = ELandscapeType.GRASS;
	private Tool tool = null;

	public EditorWindow(int width, int height) {
		data = new MapData(width, height);
		map = new MapGraphics(data);

		startMapEditing();
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
		MapInterfaceConnector connector = content.getInterfaceConnector();
		region.setContent(content);

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				region.requestRedraw();
			}
		}, 50, 50);

		connector.addListener(this);
	}

	private JPanel createMenu() {
		// ActionListener landscapeSelected = new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent arg0) {
		// changeEditLandscape(ELandscapeType.valueOf(arg0
		// .getActionCommand()));
		// }
		// };

		JPanel menu = new JPanel();
		menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
		// JPanel landscapeSelector = new JPanel();
		// landscapeSelector.setLayout(new BoxLayout(landscapeSelector,
		// BoxLayout.Y_AXIS));
		// landscapeSelector.setBorder(BorderFactory
		// .createTitledBorder("landscape type"));
		// ButtonGroup landscapeGroup = new ButtonGroup();
		// for (ELandscapeType type : ELandscapeType.values()) {
		// JRadioButton item = new JRadioButton(type.toString());
		// item.setActionCommand(type.toString());
		// item.addActionListener(landscapeSelected);
		// item.setSelected(currentLandscape == type);
		// landscapeSelector.add(item);
		// landscapeGroup.add(item);
		// }
		// menu.add(landscapeSelector);
		JTree toolshelf = new JTree(new ToolTreeModel(TOOLBOX));
		menu.add(toolshelf);
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

		return menu;
	}

	protected void changeTool(Tool lastPathComponent) {
		tool = lastPathComponent;
	}

	protected void changeEditLandscape(ELandscapeType type) {
		currentLandscape = type;
	}

	@Override
	public void action(Action action) {
		System.out.println("Got action: " + action.getActionType());
		if (action.getActionType() == EActionType.SELECT_AREA) {
			IMapArea area = ((SelectAreaAction) action).getArea();
			data.fill(currentLandscape, area);
		} else if (action instanceof DrawLineAction) {
			if (tool != null) {
				DrawLineAction lineAction = (DrawLineAction) action;

				ShapeType shape = getActiveShape();// TODO

				tool.apply(data, shape, lineAction.getStart(),
				        lineAction.getEnd(), lineAction.getUidy());
			}
		} else if (action instanceof StartDrawingAction) {
			if (tool != null) {
				StartDrawingAction lineAction = (StartDrawingAction) action;

				ShapeType shape = getActiveShape();// TODO

				tool.start(data, shape, lineAction.getPos());
			}
		}
	}

	private ShapeType getActiveShape() {
		return tool.getShapes()[1];
	}

	@Override
	public void fireAction(Action action) {
		action(action);
	}
}
