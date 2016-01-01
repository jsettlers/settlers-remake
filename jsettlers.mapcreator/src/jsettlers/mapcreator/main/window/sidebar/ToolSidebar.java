package jsettlers.mapcreator.main.window.sidebar;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.object.MapDecorationObject;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.common.map.object.StackObject;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.main.IPlayerSetter;
import jsettlers.mapcreator.main.tools.PlaceStackToolbox;
import jsettlers.mapcreator.main.tools.ToolTreeModel;
import jsettlers.mapcreator.tools.SetStartpointTool;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.ToolBox;
import jsettlers.mapcreator.tools.ToolNode;
import jsettlers.mapcreator.tools.landscape.FixHeightsTool;
import jsettlers.mapcreator.tools.landscape.FlatLandscapeTool;
import jsettlers.mapcreator.tools.landscape.HeightAdder;
import jsettlers.mapcreator.tools.landscape.LandscapeHeightTool;
import jsettlers.mapcreator.tools.landscape.PlaceResource;
import jsettlers.mapcreator.tools.landscape.SetLandscapeTool;
import jsettlers.mapcreator.tools.objects.DeleteObjectTool;
import jsettlers.mapcreator.tools.objects.PlaceBuildingTool;
import jsettlers.mapcreator.tools.objects.PlaceMapObjectTool;
import jsettlers.mapcreator.tools.objects.PlaceMovableTool;
import jsettlers.mapcreator.tools.objects.PlaceTemplateTool;
import jsettlers.mapcreator.tools.objects.PlaceTemplateTool.TemplateBuilding;
import jsettlers.mapcreator.tools.objects.PlaceTemplateTool.TemplateMovable;
import jsettlers.mapcreator.tools.objects.PlaceTemplateTool.TemplateObject;
import jsettlers.mapcreator.tools.shapes.ShapeProperty;
import jsettlers.mapcreator.tools.shapes.ShapeType;

/**
 * Sidebar with tools
 * 
 * @author Andreas Butti
 */
public abstract class ToolSidebar extends JPanel implements IPlayerSetter {
	private static final long serialVersionUID = 1L;

	/**
	 * Get active player
	 */
	private final IPlayerSetter playerSetter;

	private JPanel shapeButtons;
	private JPanel shapeSettings;

	/**
	 * Active shape
	 */
	private ShapeType activeShape = null;

	// @formatter:off
	private final ToolNode TOOLBOX = new ToolBox("<toolbox root>, hidden", new ToolNode[] {
			new ToolBox(EditorLabels.getLabel("tools.category.landscape"), new ToolNode[] {
					new SetLandscapeTool(ELandscapeType.GRASS, false),
					new SetLandscapeTool(ELandscapeType.DRY_GRASS, false),
					new SetLandscapeTool(ELandscapeType.SAND, false),
					new SetLandscapeTool(ELandscapeType.FLATTENED, false),
					new SetLandscapeTool(ELandscapeType.DESERT, false),
					new SetLandscapeTool(ELandscapeType.EARTH, false),
					new SetLandscapeTool(ELandscapeType.WATER1, false),
					new SetLandscapeTool(ELandscapeType.WATER2, false),
					new SetLandscapeTool(ELandscapeType.WATER3, false),
					new SetLandscapeTool(ELandscapeType.WATER4, false),
					new SetLandscapeTool(ELandscapeType.WATER5, false),
					new SetLandscapeTool(ELandscapeType.WATER6, false),
					new SetLandscapeTool(ELandscapeType.WATER7, false),
					new SetLandscapeTool(ELandscapeType.WATER8, false),
					new SetLandscapeTool(ELandscapeType.RIVER1, true),
					new SetLandscapeTool(ELandscapeType.RIVER2, true),
					new SetLandscapeTool(ELandscapeType.RIVER3, true),
					new SetLandscapeTool(ELandscapeType.RIVER4, true),
					new SetLandscapeTool(ELandscapeType.MOUNTAIN, false),
					new SetLandscapeTool(ELandscapeType.SNOW, false),
					new SetLandscapeTool(ELandscapeType.MOOR, false),
					new SetLandscapeTool(ELandscapeType.FLATTENED_DESERT, false),
					new SetLandscapeTool(ELandscapeType.SHARP_FLATTENED_DESERT, false),
					new SetLandscapeTool(ELandscapeType.GRAVEL, false)
					}),
			new ToolBox(EditorLabels.getLabel("tools.category.heigths"), new ToolNode[] {
					new LandscapeHeightTool(),
					new HeightAdder(true),
					new HeightAdder(false),
					new FlatLandscapeTool(),
					new FixHeightsTool(), }),
			new ToolBox(EditorLabels.getLabel("tools.category.land-resources"), new ToolNode[] {
					new PlaceResource(EResourceType.FISH),
					new PlaceResource(EResourceType.IRONORE),
					new PlaceResource(EResourceType.GOLDORE),
					new PlaceResource(EResourceType.COAL),
					new PlaceResource(null)
					}),
			new ToolBox(EditorLabels.getLabel("tools.category.objects"), new ToolNode[] {
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
					new PlaceMapObjectTool(new MapDecorationObject(EMapObjectType.PLANT_DECORATION)),
					new PlaceMapObjectTool(new MapDecorationObject(EMapObjectType.DESERT_DECORATION))
					}),
			new ToolBox(EditorLabels.getLabel("tools.category.settlers"), new ToolNode[] {
					new ToolBox(EditorLabels.getLabel("tools.category.worker"), new ToolNode[] {
							new PlaceMovableTool(EMovableType.BEARER, this),
							new PlaceMovableTool(EMovableType.BRICKLAYER, this),
							new PlaceMovableTool(EMovableType.DIGGER, this),
							new PlaceMovableTool(EMovableType.BAKER, this),
							new PlaceMovableTool(EMovableType.CHARCOAL_BURNER, this),
							new PlaceMovableTool(EMovableType.FARMER, this),
							new PlaceMovableTool(EMovableType.FISHERMAN, this),
							new PlaceMovableTool(EMovableType.FORESTER, this),
							new PlaceMovableTool(EMovableType.LUMBERJACK, this),
							new PlaceMovableTool(EMovableType.MELTER, this),
							new PlaceMovableTool(EMovableType.MILLER, this),
							new PlaceMovableTool(EMovableType.MINER, this),
							new PlaceMovableTool(EMovableType.PIG_FARMER, this),
							new PlaceMovableTool(EMovableType.SAWMILLER, this),
							new PlaceMovableTool(EMovableType.SLAUGHTERER, this),
							new PlaceMovableTool(EMovableType.SMITH, this),
							new PlaceMovableTool(EMovableType.STONECUTTER, this),
							new PlaceMovableTool(EMovableType.WATERWORKER, this),
							}),
					new ToolBox(EditorLabels.getLabel("tools.category.specialist"), new ToolNode[] {
							new PlaceMovableTool(EMovableType.GEOLOGIST, this),
							new PlaceMovableTool(EMovableType.PIONEER, this),
							new PlaceMovableTool(EMovableType.THIEF, this),
							new PlaceMovableTool(EMovableType.DONKEY, this),
							}),
					new ToolBox(EditorLabels.getLabel("tools.category.soldier"), new ToolNode[] {
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
			new ToolBox(EditorLabels.getLabel("tools.category.materials"), new ToolNode[] {
					new ToolBox(EditorLabels.getLabel("tools.category.mat-build"), new ToolNode[] {
							new PlaceStackToolbox(EMaterialType.PLANK, 8),
							new PlaceStackToolbox(EMaterialType.STONE, 8),
							new PlaceStackToolbox(EMaterialType.TRUNK, 8),
							}),
					new ToolBox(EditorLabels.getLabel("tools.category.mat-food"), new ToolNode[] {
							new PlaceStackToolbox(EMaterialType.BREAD, 8),
							new PlaceStackToolbox(EMaterialType.CROP, 8),
							new PlaceStackToolbox(EMaterialType.FISH, 8),
							new PlaceStackToolbox(EMaterialType.FLOUR, 8),
							new PlaceStackToolbox(EMaterialType.PIG, 8),
							new PlaceStackToolbox(EMaterialType.WATER, 8),
							new PlaceStackToolbox(EMaterialType.WINE, 8),
							new PlaceStackToolbox(EMaterialType.HONEY, 8),
							new PlaceStackToolbox(EMaterialType.MEAD, 8),
							}),
					new ToolBox(EditorLabels.getLabel("tools.category.mat-resources"), new ToolNode[] {
							new PlaceStackToolbox(EMaterialType.COAL, 8),
							new PlaceStackToolbox(EMaterialType.IRON, 8),
							new PlaceStackToolbox(EMaterialType.IRONORE, 8),
							new PlaceStackToolbox(EMaterialType.GOLD, 8),
							new PlaceStackToolbox(EMaterialType.GOLDORE, 8),
							}),
					new ToolBox(EditorLabels.getLabel("tools.category.mat-tools"), new ToolNode[] {
							new PlaceStackToolbox(EMaterialType.HAMMER, 8),
							new PlaceStackToolbox(EMaterialType.BLADE, 8),
							new PlaceStackToolbox(EMaterialType.AXE, 8),
							new PlaceStackToolbox(EMaterialType.SAW, 8),
							new PlaceStackToolbox(EMaterialType.PICK, 8),
							new PlaceStackToolbox(EMaterialType.SCYTHE, 8),
							new PlaceStackToolbox(EMaterialType.FISHINGROD, 8),
							}),
					new ToolBox(EditorLabels.getLabel("tools.category.mat-weapons"), new ToolNode[] {
							new PlaceStackToolbox(EMaterialType.SWORD, 8),
							new PlaceStackToolbox(EMaterialType.BOW, 8),
							new PlaceStackToolbox(EMaterialType.SPEAR, 8),
							}),
					}),
			new ToolBox(EditorLabels.getLabel("tools.category.buildings"), new ToolNode[] {
					new ToolBox(EditorLabels.getLabel("tools.category.resources"), new ToolNode[] {
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
							new PlaceBuildingTool(EBuildingType.TOOLSMITH, this),
							}),
					new ToolBox(EditorLabels.getLabel("tools.category.food"), new ToolNode[] {
							new PlaceBuildingTool(EBuildingType.FARM, this),
							new PlaceBuildingTool(EBuildingType.MILL, this),
							new PlaceBuildingTool(EBuildingType.BAKER, this),
							new PlaceBuildingTool(EBuildingType.WATERWORKS, this),
							new PlaceBuildingTool(EBuildingType.PIG_FARM, this),
							new PlaceBuildingTool(EBuildingType.SLAUGHTERHOUSE, this),
							new PlaceBuildingTool(EBuildingType.FISHER, this),
							new PlaceBuildingTool(EBuildingType.DONKEY_FARM, this),
							new PlaceBuildingTool(EBuildingType.WINEGROWER, this)
							}),
					new ToolBox(EditorLabels.getLabel("tools.category.military"), new ToolNode[] {
							new PlaceBuildingTool(EBuildingType.TOWER, this),
							new PlaceBuildingTool(EBuildingType.BIG_TOWER, this),
							new PlaceBuildingTool(EBuildingType.CASTLE, this),
							new PlaceBuildingTool(EBuildingType.WEAPONSMITH, this),
							new PlaceBuildingTool(EBuildingType.DOCKYARD, this)
							}),
					new ToolBox(EditorLabels.getLabel("tools.category.social"), new ToolNode[] {
							new PlaceBuildingTool(EBuildingType.SMALL_LIVINGHOUSE, this),
							new PlaceBuildingTool(EBuildingType.MEDIUM_LIVINGHOUSE, this),
							new PlaceBuildingTool(EBuildingType.BIG_LIVINGHOUSE, this),
							new PlaceBuildingTool(EBuildingType.TEMPLE, this),
							new PlaceBuildingTool(EBuildingType.BIG_TEMPLE, this),
							new PlaceBuildingTool(EBuildingType.STOCK, this),
							}),
					}),
			new ToolBox(EditorLabels.getLabel("presets"), new ToolNode[] {
					new PlaceTemplateTool(EditorLabels.getLabel("preset-start"), new TemplateObject[] {
							new TemplateBuilding(0, 0, EBuildingType.TOWER),

							// goods
							new TemplateObject(-4, 7, new StackObject(EMaterialType.PLANK, 8)),
							new TemplateObject(-4, 10, new StackObject(EMaterialType.PLANK, 8)),

							new TemplateObject(-1, 7, new StackObject(EMaterialType.PLANK, 8)),
							new TemplateObject(-1, 10, new StackObject(EMaterialType.PLANK, 8)),
							new TemplateObject(-1, 13, new StackObject(EMaterialType.PLANK, 8)),

							new TemplateObject(2, 7, new StackObject(EMaterialType.STONE, 8)),
							new TemplateObject(2, 10, new StackObject(EMaterialType.PLANK, 8)),
							new TemplateObject(2, 13, new StackObject(EMaterialType.PLANK, 8)),

							new TemplateObject(5, 7, new StackObject(EMaterialType.STONE, 8)),
							new TemplateObject(5, 10, new StackObject(EMaterialType.STONE, 8)),
							new TemplateObject(5, 13, new StackObject(EMaterialType.STONE, 8)),

							new TemplateObject(8, 7, new StackObject(EMaterialType.FISH, 8)),
							new TemplateObject(8, 10, new StackObject(EMaterialType.COAL, 8)),
							new TemplateObject(8, 13, new StackObject(EMaterialType.STONE, 8)),

							new TemplateObject(11, 7, new StackObject(EMaterialType.BREAD, 8)),
							new TemplateObject(11, 10, new StackObject(EMaterialType.IRONORE, 8)),
							new TemplateObject(11, 13, new StackObject(EMaterialType.BLADE, 6)),

							new TemplateObject(14, 7, new StackObject(EMaterialType.MEAT, 8)),
							new TemplateObject(14, 10, new StackObject(EMaterialType.FISHINGROD, 2)),
							new TemplateObject(14, 13, new StackObject(EMaterialType.HAMMER, 8)),

							new TemplateObject(17, 7, new StackObject(EMaterialType.SCYTHE, 2)),
							// new TemplateObject(17, 10, new StackObject(EMaterialType., 2)),
							new TemplateObject(17, 13, new StackObject(EMaterialType.AXE, 6)),

							new TemplateObject(20, 10, new StackObject(EMaterialType.PICK, 8)),
							new TemplateObject(20, 13, new StackObject(EMaterialType.SAW, 3)),

							// worker
							new TemplateMovable(8, 16, EMovableType.BRICKLAYER),
							new TemplateMovable(9, 18, EMovableType.BRICKLAYER),
							new TemplateMovable(10, 16, EMovableType.BRICKLAYER),
							new TemplateMovable(11, 18, EMovableType.BRICKLAYER),
							new TemplateMovable(12, 16, EMovableType.BRICKLAYER),

							new TemplateMovable(14, 16, EMovableType.DIGGER),
							new TemplateMovable(15, 18, EMovableType.DIGGER),
							new TemplateMovable(16, 16, EMovableType.DIGGER),
							new TemplateMovable(17, 18, EMovableType.DIGGER),
							new TemplateMovable(18, 16, EMovableType.DIGGER),

							new TemplateMovable(18, 17, EMovableType.SMITH),
							new TemplateMovable(18, 18, EMovableType.SMITH),
							new TemplateMovable(20, 16, EMovableType.MELTER),

							// soldiers
							new TemplateMovable(-11, -12, EMovableType.SWORDSMAN_L1), new TemplateMovable(-11, -14, EMovableType.SWORDSMAN_L1),
							new TemplateMovable(-11, -16, EMovableType.SWORDSMAN_L1),

							new TemplateMovable(-9, -10, EMovableType.SWORDSMAN_L1), new TemplateMovable(-9, -12, EMovableType.SWORDSMAN_L1),
							new TemplateMovable(-9, -14, EMovableType.SWORDSMAN_L1), new TemplateMovable(-9, -16, EMovableType.BOWMAN_L1),

							new TemplateMovable(-7, -10, EMovableType.PIKEMAN_L1), new TemplateMovable(-7, -12, EMovableType.PIKEMAN_L1),
							new TemplateMovable(-7, -14, EMovableType.BOWMAN_L1),
							new TemplateMovable(-7, -16, EMovableType.BOWMAN_L1),

							new TemplateMovable(-5, -10, EMovableType.PIKEMAN_L1),
							new TemplateMovable(-5, -12, EMovableType.BOWMAN_L1),
							new TemplateMovable(-5, -14, EMovableType.BOWMAN_L1),

							// bearer
							new TemplateMovable(-2, -12, EMovableType.BEARER),
							new TemplateMovable(-2, -14, EMovableType.BEARER),
							new TemplateMovable(-2, -16, EMovableType.BEARER),

							new TemplateMovable(0, -10, EMovableType.BEARER),
							new TemplateMovable(0, -12, EMovableType.BEARER),
							new TemplateMovable(0, -14, EMovableType.BEARER),
							new TemplateMovable(0, -16, EMovableType.BEARER),

							new TemplateMovable(2, -10, EMovableType.BEARER),
							new TemplateMovable(2, -12, EMovableType.BEARER),
							new TemplateMovable(2, -14, EMovableType.BEARER),
							new TemplateMovable(2, -16, EMovableType.BEARER),

							new TemplateMovable(4, -10, EMovableType.BEARER),
							new TemplateMovable(4, -12, EMovableType.BEARER),
							new TemplateMovable(4, -14, EMovableType.BEARER),

							new TemplateMovable(5, -12, EMovableType.BEARER),
							new TemplateMovable(5, -14, EMovableType.BEARER),
							new TemplateMovable(5, -16, EMovableType.BEARER),

							new TemplateMovable(7, -10, EMovableType.BEARER),
							new TemplateMovable(7, -12, EMovableType.BEARER),
							new TemplateMovable(7, -14, EMovableType.BEARER),
							new TemplateMovable(7, -16, EMovableType.BEARER),

							new TemplateMovable(9, -10, EMovableType.BEARER),
							new TemplateMovable(9, -12, EMovableType.BEARER),
							new TemplateMovable(9, -14, EMovableType.BEARER),
							new TemplateMovable(9, -16, EMovableType.BEARER),

							new TemplateMovable(11, -10, EMovableType.BEARER),
							new TemplateMovable(11, -12, EMovableType.BEARER),
							new TemplateMovable(11, -14, EMovableType.BEARER),
					}, this),
					new PlaceTemplateTool(EditorLabels.getLabel("preset-wood"), new TemplateObject[] {
							new TemplateBuilding(0, 10, EBuildingType.LUMBERJACK),
							new TemplateBuilding(0, 0, EBuildingType.FORESTER),
							new TemplateBuilding(3, -9, EBuildingType.LUMBERJACK),

							new TemplateMovable(8, -8, EMovableType.LUMBERJACK),
							new TemplateMovable(7, 3, EMovableType.FORESTER),
							new TemplateMovable(7, 12, EMovableType.LUMBERJACK),
					}, this),
				}),

			new SetStartpointTool(this),
			new DeleteObjectTool(),
		});
	// @formatter:on

	/**
	 * Listener for activated shape
	 */
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

	/**
	 * Constructor
	 */
	public ToolSidebar(IPlayerSetter playerSetter) {
		setLayout(new BorderLayout());
		this.playerSetter = playerSetter;

		final JTree toolshelf = new JTree(new ToolTreeModel(TOOLBOX));
		add(new JScrollPane(toolshelf), BorderLayout.CENTER);
		toolshelf.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = e.getNewLeadSelectionPath();
				if (path == null) {
					changeTool(null);
					return;
				}
				Object lastPathComponent = path.getLastPathComponent();
				if (lastPathComponent instanceof ToolBox) {
					changeTool(null);
				} else if (lastPathComponent instanceof Tool) {
					Tool newTool = (Tool) lastPathComponent;
					changeTool(newTool);
				}

			}
		});
		toolshelf.setCellRenderer(new ToolRenderer());
		toolshelf.setRootVisible(false);

		Box shape = Box.createVerticalBox();
		JLabel headerLabel = new JLabel(EditorLabels.getLabel("shapes"));
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
		shape.add(headerLabel);
		shapeButtons = new JPanel();
		shapeButtons.setLayout(new GridLayout(0, 1));
		shape.add(shapeButtons);
		shapeSettings = new JPanel();
		shapeSettings.setLayout(new GridLayout(0, 1));
		shape.add(shapeSettings);
		add(shape, BorderLayout.SOUTH);
	}

	/**
	 * @return The active shape
	 */
	public ShapeType getActiveShape() {
		return activeShape;
	}

	protected abstract void changeTool(Tool lastPathComponent);

	@Override
	public int getActivePlayer() {
		return playerSetter.getActivePlayer();
	}

	/**
	 * Update the shape buttons
	 * 
	 * @param tool
	 *            Selected tool
	 * @param activeShape
	 *            Active shape
	 */
	public void updateShapeButtons(Tool tool) {
		shapeButtons.removeAll();

		if (tool != null) {
			ButtonGroup shapeGroup = new ButtonGroup();
			for (ShapeType shape : tool.getShapes()) {
				JCheckBox button = new JCheckBox(shape.getName());
				button.setSelected(shape == activeShape);
				button.addActionListener(new ShapeActionListener(shape));
				shapeGroup.add(button);
				shapeButtons.add(button);
			}
		}

		shapeButtons.revalidate();
	}

	/**
	 * Set the active shape
	 * 
	 * @param shape
	 *            Shape
	 */
	public void setShape(ShapeType shape) {
		this.activeShape = shape;
		shapeSettings.removeAll();
		if (shape != null) {
			for (ShapeProperty property : shape.getProperties()) {
				shapeSettings.add(new ShapePropertyEditor(shape, property));
			}
		}
		shapeSettings.revalidate();
	}

}
