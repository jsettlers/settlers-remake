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
package jsettlers.mapcreator.main.window.sidebar;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.logic.map.loading.data.objects.DecorationMapDataObject;
import jsettlers.logic.map.loading.data.objects.StoneMapDataObject;
import jsettlers.logic.map.loading.data.objects.MapTreeObject;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.exceptionhandler.ExceptionHandler;
import jsettlers.mapcreator.control.IPlayerSetter;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.main.tools.PlaceStackToolbox;
import jsettlers.mapcreator.main.tools.ToolTreeModel;
import jsettlers.mapcreator.presetloader.PresetLoader;
import jsettlers.mapcreator.tools.SetStartpointTool;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.ToolBox;
import jsettlers.mapcreator.tools.ToolNode;
import jsettlers.mapcreator.tools.landscape.FixHeightsTool;
import jsettlers.mapcreator.tools.landscape.FlatLandscapeTool;
import jsettlers.mapcreator.tools.landscape.IncreaseDecreaseHeightAdder;
import jsettlers.mapcreator.tools.landscape.LandscapeHeightTool;
import jsettlers.mapcreator.tools.landscape.PlaceResource;
import jsettlers.mapcreator.tools.landscape.SetLandscapeTool;
import jsettlers.mapcreator.tools.objects.DeleteObjectTool;
import jsettlers.mapcreator.tools.objects.PlaceBuildingTool;
import jsettlers.mapcreator.tools.objects.PlaceMapObjectTool;
import jsettlers.mapcreator.tools.objects.PlaceMovableTool;
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

	/**
	 * Panel with the shape settings
	 */
	private final ShapeSelectionPanel shapeSettingsPanel = new ShapeSelectionPanel();

	/**
	 * Presets, Templates: Loaded from .xml file
	 */
	private final ToolBox PRESETS = new ToolBox(EditorLabels.getLabel("presets"));

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
					new IncreaseDecreaseHeightAdder(true),
					new IncreaseDecreaseHeightAdder(false),
					new FlatLandscapeTool(),
					new FixHeightsTool()
					}),
			new ToolBox(EditorLabels.getLabel("tools.category.land-resources"), new ToolNode[] {
					new PlaceResource(EResourceType.FISH),
					new PlaceResource(EResourceType.IRONORE),
					new PlaceResource(EResourceType.GOLDORE),
					new PlaceResource(EResourceType.COAL),
					new PlaceResource(null)
					}),
			new ToolBox(EditorLabels.getLabel("tools.category.objects"), new ToolNode[] {
					new PlaceMapObjectTool(MapTreeObject.getInstance()),
					new PlaceMapObjectTool(StoneMapDataObject.getInstance(0)),
					new PlaceMapObjectTool(StoneMapDataObject.getInstance(1)),
					new PlaceMapObjectTool(StoneMapDataObject.getInstance(2)),
					new PlaceMapObjectTool(StoneMapDataObject.getInstance(3)),
					new PlaceMapObjectTool(StoneMapDataObject.getInstance(4)),
					new PlaceMapObjectTool(StoneMapDataObject.getInstance(5)),
					new PlaceMapObjectTool(StoneMapDataObject.getInstance(6)),
					new PlaceMapObjectTool(StoneMapDataObject.getInstance(7)),
					new PlaceMapObjectTool(StoneMapDataObject.getInstance(8)),
					new PlaceMapObjectTool(StoneMapDataObject.getInstance(9)),
					new PlaceMapObjectTool(StoneMapDataObject.getInstance(10)),
					new PlaceMapObjectTool(StoneMapDataObject.getInstance(11)),
					new PlaceMapObjectTool(StoneMapDataObject.getInstance(12)),
					new PlaceMapObjectTool(new DecorationMapDataObject(EMapObjectType.PLANT_DECORATION)),
					new PlaceMapObjectTool(new DecorationMapDataObject(EMapObjectType.DESERT_DECORATION))
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
							new PlaceMovableTool(EMovableType.WATERWORKER, this)
							}),
					new ToolBox(EditorLabels.getLabel("tools.category.specialist"), new ToolNode[] {
							new PlaceMovableTool(EMovableType.GEOLOGIST, this),
							new PlaceMovableTool(EMovableType.PIONEER, this),
							new PlaceMovableTool(EMovableType.THIEF, this),
							new PlaceMovableTool(EMovableType.DONKEY, this)
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
							new PlaceMovableTool(EMovableType.PIKEMAN_L3, this)
							}),
					}),
			new ToolBox(EditorLabels.getLabel("tools.category.materials"), new ToolNode[] {
					new ToolBox(EditorLabels.getLabel("tools.category.mat-build"), new ToolNode[] {
							new PlaceStackToolbox(EMaterialType.PLANK, 8),
							new PlaceStackToolbox(EMaterialType.STONE, 8),
							new PlaceStackToolbox(EMaterialType.TRUNK, 8)
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
							new PlaceStackToolbox(EMaterialType.MEAD, 8)
							}),
					new ToolBox(EditorLabels.getLabel("tools.category.mat-resources"), new ToolNode[] {
							new PlaceStackToolbox(EMaterialType.COAL, 8),
							new PlaceStackToolbox(EMaterialType.IRON, 8),
							new PlaceStackToolbox(EMaterialType.IRONORE, 8),
							new PlaceStackToolbox(EMaterialType.GOLD, 8),
							new PlaceStackToolbox(EMaterialType.GOLDORE, 8)
							}),
					new ToolBox(EditorLabels.getLabel("tools.category.mat-tools"), new ToolNode[] {
							new PlaceStackToolbox(EMaterialType.HAMMER, 8),
							new PlaceStackToolbox(EMaterialType.BLADE, 8),
							new PlaceStackToolbox(EMaterialType.AXE, 8),
							new PlaceStackToolbox(EMaterialType.SAW, 8),
							new PlaceStackToolbox(EMaterialType.PICK, 8),
							new PlaceStackToolbox(EMaterialType.SCYTHE, 8),
							new PlaceStackToolbox(EMaterialType.FISHINGROD, 8)
							}),
					new ToolBox(EditorLabels.getLabel("tools.category.mat-weapons"), new ToolNode[] {
							new PlaceStackToolbox(EMaterialType.SWORD, 8),
							new PlaceStackToolbox(EMaterialType.BOW, 8),
							new PlaceStackToolbox(EMaterialType.SPEAR, 8)
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
							new PlaceBuildingTool(EBuildingType.TOOLSMITH, this)
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
							new PlaceBuildingTool(EBuildingType.MARKET_PLACE, this),
							new PlaceBuildingTool(EBuildingType.HARBOR, this)
							}),
					}),
					PRESETS,

			new SetStartpointTool(this),
			new DeleteObjectTool(),
		});
	// @formatter:on

	/**
	 * Constructor
	 * 
	 * @param playerSetter
	 *            Interface to get current active player
	 */
	public ToolSidebar(IPlayerSetter playerSetter) {
		setLayout(new BorderLayout());
		this.playerSetter = playerSetter;

		loadPresets();

		final JTree toolshelf = new JTree(new ToolTreeModel(TOOLBOX));
		add(new JScrollPane(toolshelf), BorderLayout.CENTER);
		toolshelf.addTreeSelectionListener(e -> {
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

		});
		toolshelf.setCellRenderer(new ToolRenderer());
		toolshelf.setRootVisible(false);

		add(shapeSettingsPanel, BorderLayout.NORTH);
	}

	/**
	 * Load presets, from internal .xml and additional external .xml file
	 */
	private void loadPresets() {
		PresetLoader loader = new PresetLoader(PRESETS, this);
		try {
			loader.load(PresetLoader.class.getResourceAsStream("preset.xml"));
		} catch (Exception e) {
			ExceptionHandler.displayError(e, "Could not load internal preset.xml file!");
		}

	}

	/**
	 * @return The active shape
	 */
	public ShapeType getActiveShape() {
		return shapeSettingsPanel.getActiveShape();
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
	 */
	public void updateShapeSettings(Tool tool) {
		shapeSettingsPanel.updateShapeSettings(tool);
	}
}
