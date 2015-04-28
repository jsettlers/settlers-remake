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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import jsettlers.algorithms.previewimage.PreviewImageCreator;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.map.object.MapDecorationObject;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.common.map.object.StackObject;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
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
import jsettlers.logic.map.save.MapSaver;
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
import jsettlers.mapcreator.main.error.ShowErrorsButton;
import jsettlers.mapcreator.main.map.MapEditorControls;
import jsettlers.mapcreator.main.tools.PlaceStackToolbox;
import jsettlers.mapcreator.main.tools.ShapePropertyEditor;
import jsettlers.mapcreator.main.tools.ToolRenderer;
import jsettlers.mapcreator.main.tools.ToolTreeModel;
import jsettlers.mapcreator.mapview.MapGraphics;
import jsettlers.mapcreator.stat.StatisticsWindow;
import jsettlers.mapcreator.tools.SetStartpointTool;
import jsettlers.mapcreator.tools.Tool;
import jsettlers.mapcreator.tools.ToolBox;
import jsettlers.mapcreator.tools.ToolNode;
import jsettlers.mapcreator.tools.landscape.FixHeightsTool;
import jsettlers.mapcreator.tools.landscape.FlatLandscapeTool;
import jsettlers.mapcreator.tools.landscape.HeightAdder;
import jsettlers.mapcreator.tools.landscape.LandscapeHeightTool;
import jsettlers.mapcreator.tools.landscape.PlaceResource;
import jsettlers.mapcreator.tools.landscape.ResourceTool;
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

public class EditorWindow implements IMapInterfaceListener, ActionFireable, TestResultReceiver, IPlayerSetter, IScrollToAble {

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

	private static final int MAX_UNDO = 100;

	private final LinkedList<ShapeType> lastUsed = new LinkedList<ShapeType>();

	// @formatter:off
	private final ToolNode TOOLBOX = new ToolBox(EditorLabels.getLabel("toolbox"), new ToolNode[] {
			new ToolBox(EditorLabels.getLabel("category-landscape"), new ToolNode[] { new SetLandscapeTool(ELandscapeType.GRASS, false),
					new SetLandscapeTool(ELandscapeType.DRY_GRASS, false), new SetLandscapeTool(ELandscapeType.SAND, false),
					new SetLandscapeTool(ELandscapeType.FLATTENED, false), new SetLandscapeTool(ELandscapeType.DESERT, false),
					new SetLandscapeTool(ELandscapeType.EARTH, false), new SetLandscapeTool(ELandscapeType.WATER1, false),
					new SetLandscapeTool(ELandscapeType.WATER2, false), new SetLandscapeTool(ELandscapeType.WATER3, false),
					new SetLandscapeTool(ELandscapeType.WATER4, false), new SetLandscapeTool(ELandscapeType.WATER5, false),
					new SetLandscapeTool(ELandscapeType.WATER6, false), new SetLandscapeTool(ELandscapeType.WATER7, false),
					new SetLandscapeTool(ELandscapeType.WATER8, false), new SetLandscapeTool(ELandscapeType.RIVER1, true),
					new SetLandscapeTool(ELandscapeType.RIVER2, true), new SetLandscapeTool(ELandscapeType.RIVER3, true),
					new SetLandscapeTool(ELandscapeType.RIVER4, true), new SetLandscapeTool(ELandscapeType.MOUNTAIN, false),
					new SetLandscapeTool(ELandscapeType.SNOW, false), new SetLandscapeTool(ELandscapeType.MOOR, false),
					new SetLandscapeTool(ELandscapeType.FLATTENED_DESERT, false), new SetLandscapeTool(ELandscapeType.SHARP_FLATTENED_DESERT, false),
					new SetLandscapeTool(ELandscapeType.GRAVEL, false), }),
			new ToolBox(EditorLabels.getLabel("category-heigths"), new ToolNode[] { new LandscapeHeightTool(), new HeightAdder(true), new HeightAdder(false), new FlatLandscapeTool(),
					new FixHeightsTool(), }),
			new ToolBox(EditorLabels.getLabel("category-land-resources"), new ToolNode[] { new PlaceResource(EResourceType.FISH), new PlaceResource(EResourceType.IRON),
					new PlaceResource(EResourceType.GOLD), new PlaceResource(EResourceType.COAL), new PlaceResource(null) }),
			new ToolBox(EditorLabels.getLabel("category-objects"), new ToolNode[] { new PlaceMapObjectTool(MapTreeObject.getInstance()),
					new PlaceMapObjectTool(MapStoneObject.getInstance(0)), new PlaceMapObjectTool(MapStoneObject.getInstance(1)),
					new PlaceMapObjectTool(MapStoneObject.getInstance(2)), new PlaceMapObjectTool(MapStoneObject.getInstance(3)),
					new PlaceMapObjectTool(MapStoneObject.getInstance(4)), new PlaceMapObjectTool(MapStoneObject.getInstance(5)),
					new PlaceMapObjectTool(MapStoneObject.getInstance(6)), new PlaceMapObjectTool(MapStoneObject.getInstance(7)),
					new PlaceMapObjectTool(MapStoneObject.getInstance(8)), new PlaceMapObjectTool(MapStoneObject.getInstance(9)),
					new PlaceMapObjectTool(MapStoneObject.getInstance(10)),
					new PlaceMapObjectTool(new MapDecorationObject(EMapObjectType.PLANT_DECORATION)),
					new PlaceMapObjectTool(new MapDecorationObject(EMapObjectType.DESERT_DECORATION)), }),
			new ToolBox(EditorLabels.getLabel("category-settlers"), new ToolNode[] {
					new ToolBox(EditorLabels.getLabel("category-worker"), new ToolNode[] { new PlaceMovableTool(EMovableType.BEARER, this),
							new PlaceMovableTool(EMovableType.BRICKLAYER, this), new PlaceMovableTool(EMovableType.DIGGER, this),
							new PlaceMovableTool(EMovableType.BAKER, this), new PlaceMovableTool(EMovableType.CHARCOAL_BURNER, this),
							new PlaceMovableTool(EMovableType.FARMER, this), new PlaceMovableTool(EMovableType.FISHERMAN, this),
							new PlaceMovableTool(EMovableType.FORESTER, this), new PlaceMovableTool(EMovableType.LUMBERJACK, this),
							new PlaceMovableTool(EMovableType.MELTER, this), new PlaceMovableTool(EMovableType.MILLER, this),
							new PlaceMovableTool(EMovableType.MINER, this), new PlaceMovableTool(EMovableType.PIG_FARMER, this),
							new PlaceMovableTool(EMovableType.SAWMILLER, this), new PlaceMovableTool(EMovableType.SLAUGHTERER, this),
							new PlaceMovableTool(EMovableType.SMITH, this), new PlaceMovableTool(EMovableType.STONECUTTER, this),
							new PlaceMovableTool(EMovableType.WATERWORKER, this), }),
					new ToolBox(EditorLabels.getLabel("category-specialist"), new ToolNode[] { new PlaceMovableTool(EMovableType.GEOLOGIST, this),
							new PlaceMovableTool(EMovableType.PIONEER, this), new PlaceMovableTool(EMovableType.THIEF, this),
							new PlaceMovableTool(EMovableType.DONKEY, this), }),
					new ToolBox(EditorLabels.getLabel("category-soldier"), new ToolNode[] { new PlaceMovableTool(EMovableType.SWORDSMAN_L1, this),
							new PlaceMovableTool(EMovableType.SWORDSMAN_L2, this), new PlaceMovableTool(EMovableType.SWORDSMAN_L3, this),
							new PlaceMovableTool(EMovableType.BOWMAN_L1, this), new PlaceMovableTool(EMovableType.BOWMAN_L2, this),
							new PlaceMovableTool(EMovableType.BOWMAN_L3, this), new PlaceMovableTool(EMovableType.PIKEMAN_L1, this),
							new PlaceMovableTool(EMovableType.PIKEMAN_L2, this), new PlaceMovableTool(EMovableType.PIKEMAN_L3, this), }), }),
			new ToolBox(EditorLabels.getLabel("category-materials"), new ToolNode[] {
					new ToolBox(EditorLabels.getLabel("category-mat-build"), new ToolNode[] { new PlaceStackToolbox(EMaterialType.PLANK, 8),
							new PlaceStackToolbox(EMaterialType.STONE, 8), new PlaceStackToolbox(EMaterialType.TRUNK, 8), }),
					new ToolBox(EditorLabels.getLabel("category-mat-food"), new ToolNode[] { new PlaceStackToolbox(EMaterialType.BREAD, 8),
							new PlaceStackToolbox(EMaterialType.CROP, 8), new PlaceStackToolbox(EMaterialType.FISH, 8),
							new PlaceStackToolbox(EMaterialType.FLOUR, 8), new PlaceStackToolbox(EMaterialType.PIG, 8),
							new PlaceStackToolbox(EMaterialType.WATER, 8), new PlaceStackToolbox(EMaterialType.WINE, 8), }),
					new ToolBox(EditorLabels.getLabel("category-mat-resources"), new ToolNode[] { new PlaceStackToolbox(EMaterialType.COAL, 8),
							new PlaceStackToolbox(EMaterialType.IRON, 8), new PlaceStackToolbox(EMaterialType.IRONORE, 8),
							new PlaceStackToolbox(EMaterialType.GOLD, 8), new PlaceStackToolbox(EMaterialType.GOLDORE, 8), }),
					new ToolBox(EditorLabels.getLabel("category-mat-tools"), new ToolNode[] { new PlaceStackToolbox(EMaterialType.HAMMER, 8),
							new PlaceStackToolbox(EMaterialType.BLADE, 8), new PlaceStackToolbox(EMaterialType.AXE, 8),
							new PlaceStackToolbox(EMaterialType.SAW, 8), new PlaceStackToolbox(EMaterialType.PICK, 8),
							new PlaceStackToolbox(EMaterialType.SCYTHE, 8), new PlaceStackToolbox(EMaterialType.FISHINGROD, 8), }),
					new ToolBox(EditorLabels.getLabel("category-mat-weapons"), new ToolNode[] { new PlaceStackToolbox(EMaterialType.SWORD, 8),
							new PlaceStackToolbox(EMaterialType.BOW, 8), new PlaceStackToolbox(EMaterialType.SPEAR, 8), }), }),
			new ToolBox(EditorLabels.getLabel("category-buildings"), new ToolNode[] {
					new ToolBox(EditorLabels.getLabel("category-resources"), new ToolNode[] { new PlaceBuildingTool(EBuildingType.LUMBERJACK, this),
							new PlaceBuildingTool(EBuildingType.SAWMILL, this), new PlaceBuildingTool(EBuildingType.STONECUTTER, this),
							new PlaceBuildingTool(EBuildingType.FORESTER, this), new PlaceBuildingTool(EBuildingType.IRONMELT, this),
							new PlaceBuildingTool(EBuildingType.IRONMINE, this), new PlaceBuildingTool(EBuildingType.GOLDMELT, this),
							new PlaceBuildingTool(EBuildingType.GOLDMINE, this), new PlaceBuildingTool(EBuildingType.COALMINE, this),
							new PlaceBuildingTool(EBuildingType.CHARCOAL_BURNER, this), new PlaceBuildingTool(EBuildingType.TOOLSMITH, this), }),
					new ToolBox(EditorLabels.getLabel("category-food"), new ToolNode[] { new PlaceBuildingTool(EBuildingType.FARM, this),
							new PlaceBuildingTool(EBuildingType.MILL, this), new PlaceBuildingTool(EBuildingType.BAKER, this),
							new PlaceBuildingTool(EBuildingType.WATERWORKS, this), new PlaceBuildingTool(EBuildingType.PIG_FARM, this),
							new PlaceBuildingTool(EBuildingType.SLAUGHTERHOUSE, this), new PlaceBuildingTool(EBuildingType.FISHER, this),
							new PlaceBuildingTool(EBuildingType.DONKEY_FARM, this), new PlaceBuildingTool(EBuildingType.WINEGROWER, this), }),
					new ToolBox(EditorLabels.getLabel("category-military"), new ToolNode[] { new PlaceBuildingTool(EBuildingType.TOWER, this),
							new PlaceBuildingTool(EBuildingType.BIG_TOWER, this), new PlaceBuildingTool(EBuildingType.CASTLE, this),
							new PlaceBuildingTool(EBuildingType.WEAPONSMITH, this), new PlaceBuildingTool(EBuildingType.DOCKYARD, this), }),
					new ToolBox(EditorLabels.getLabel("category-social"), new ToolNode[] { new PlaceBuildingTool(EBuildingType.SMALL_LIVINGHOUSE, this),
							new PlaceBuildingTool(EBuildingType.MEDIUM_LIVINGHOUSE, this),
							new PlaceBuildingTool(EBuildingType.BIG_LIVINGHOUSE, this), new PlaceBuildingTool(EBuildingType.TEMPLE, this),
							new PlaceBuildingTool(EBuildingType.BIG_TEMPLE, this), new PlaceBuildingTool(EBuildingType.STOCK, this), }), }),
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
					}, this), }),

			new SetStartpointTool(this), new DeleteObjectTool(), });
	// @formatter:on

	private final MapData data;
	private final MapGraphics map;
	private Tool tool = null;
	private ShapeType activeShape = null;
	private JPanel shapeButtons;
	private JPanel shapeSettings;

	private byte currentPlayer = 0;

	private ShortPoint2D testFailPoint = null;

	private JButton undoButton;

	private final LinkedList<MapDataDelta> undoDeltas = new LinkedList<MapDataDelta>();

	private final LinkedList<MapDataDelta> redoDeltas = new LinkedList<MapDataDelta>();

	private final DataTester dataTester;

	private MapInterfaceConnector connector;

	private JLabel testResult;

	private JButton startGameButton;

	private MapFileHeader header;

	private JButton saveButton;

	private JButton redoButton;

	private ShowErrorsButton showErrorsButton;

	private JFrame window;

	public EditorWindow(MapFileHeader header, ELandscapeType ground) {
		this.header = header;
		short width = header.getWidth();
		short height = header.getHeight();
		short playerCount = header.getMaxPlayer();
		data = new MapData(width, height, playerCount, ground);
		map = new MapGraphics(data);

		dataTester = new DataTester(data, this);
		buildMapEditingWindow();
		dataTester.start();
	}

	public EditorWindow(MapLoader loader) throws MapLoadException {
		data = new MapData(loader.getMapData());
		header = loader.getFileHeader();
		map = new MapGraphics(data);

		dataTester = new DataTester(data, this);
		buildMapEditingWindow();
		dataTester.start();
	}

	public void buildMapEditingWindow() {
		window = new JFrame("map editor");
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

		// toolbar
		JToolBar toolbar = createToolbar();
		root.add(toolbar, BorderLayout.NORTH);

		// window
		window.add(root);
		window.pack();
		window.setSize(1200, 800);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setLocationRelativeTo(null);

		MapContent content = new MapContent(new FakeMapGame(map), new SwingSoundPlayer(), new MapEditorControls(new CombiningActionFirerer(this)));
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

		saveButton = new JButton(EditorLabels.getLabel("save"));
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		});
		saveButton.setEnabled(false);
		bar.add(saveButton);

		undoButton = new JButton(EditorLabels.getLabel("undo"));
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				undo();
			}
		});
		undoButton.setEnabled(false);
		bar.add(undoButton);

		redoButton = new JButton(EditorLabels.getLabel("redo"));
		redoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				redo();
			}
		});
		redoButton.setEnabled(false);
		bar.add(redoButton);

		showErrorsButton = new ShowErrorsButton(dataTester.getErrorList(), this);
		bar.add(showErrorsButton);

		testResult = new JLabel();
		testResult.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (testFailPoint != null) {
					connector.scrollTo(testFailPoint, true);
				}
			}
		});
		bar.add(testResult);

		bar.add(Box.createGlue());

		bar.add(new JLabel(EditorLabels.getLabel("current-player")));
		final SpinnerNumberModel model = new SpinnerNumberModel(0, 0, data.getPlayerCount() - 1, 1);
		JSpinner playerSpinner = new JSpinner(model);
		playerSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				currentPlayer = model.getNumber().byteValue();
			}
		});
		playerSpinner.setPreferredSize(new Dimension(50, 1));
		playerSpinner.setMaximumSize(new Dimension(50, 40));
		bar.add(playerSpinner);

		JButton statistics = new JButton(EditorLabels.getLabel("statistics"));
		statistics.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new StatisticsWindow(data);
			}
		});
		bar.add(statistics);

		JButton editSettings = new JButton(EditorLabels.getLabel("settings"));
		editSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editSettings();
			}
		});
		bar.add(editSettings);

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

	protected void editSettings() {
		final JDialog dialog = new JDialog(window, EditorLabels.getLabel("settings"), true);
		final MapHeaderEditor headerEditor = new MapHeaderEditor(header, false);
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
		box.add(headerEditor);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MapFileHeader nheader = headerEditor.getHeader();
				if (nheader.getWidth() != header.getWidth() || nheader.getHeight() != header.getHeight()) {
					JOptionPane.showMessageDialog(window, "Widh and height are fixed.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				header = nheader;
				data.setMaxPlayers(header.getMaxPlayer());
				dataTester.retest();
				dialog.setVisible(false);
			}
		});
		box.add(okButton);

		dialog.add(box);
		dialog.setLocationRelativeTo(null);
		dialog.pack();
		dialog.setVisible(true);
	}

	protected void save() {
		try {
			MapFileHeader imagedHeader = generateMapHeader();
			data.doPreSaveActions();
			MapList.getDefaultList().saveNewMap(imagedHeader, data);
		} catch (Throwable e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(saveButton, e.getMessage());
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
			MapSaver.saveMap(generateMapHeader(), data, new FileOutputStream(temp));

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

			new Thread(new Runnable() {
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

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void undo() {
		if (!undoDeltas.isEmpty()) {
			MapDataDelta delta = undoDeltas.pollLast();

			MapDataDelta inverse = data.apply(delta);

			redoDeltas.addLast(inverse);
			redoButton.setEnabled(true);
		}
		if (undoDeltas.isEmpty()) {
			undoButton.setEnabled(false);
			saveButton.setEnabled(false);
		}
	}

	protected void redo() {
		if (!redoDeltas.isEmpty()) {
			MapDataDelta delta = redoDeltas.pollLast();

			MapDataDelta inverse = data.apply(delta);

			undoDeltas.addLast(inverse);
			undoButton.setEnabled(true);
			saveButton.setEnabled(true);
		}
		if (redoDeltas.isEmpty()) {
			redoButton.setEnabled(false);
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
		undoButton.setEnabled(true);
		redoButton.setEnabled(false);
		saveButton.setEnabled(true);
	}

	private JPanel createMenu() {
		JPanel menu = new JPanel();
		menu.setLayout(new BorderLayout());

		final JTree toolshelf = new JTree(new ToolTreeModel(TOOLBOX));
		menu.add(new JScrollPane(toolshelf), BorderLayout.CENTER);
		toolshelf.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent arg0) {
				TreePath path = arg0.getNewLeadSelectionPath();
				if (path == null) {
					changeTool(null);
					return;
				}
				Object lastPathComponent = path.getLastPathComponent();
				if (lastPathComponent instanceof ToolBox) {
					ToolBox toolBox = (ToolBox) lastPathComponent;
					TreePath newPath = path.pathByAddingChild(toolBox.getTools()[0]);
					toolshelf.setSelectionPath(newPath);
				} else if (lastPathComponent instanceof Tool) {
					Tool newTool = (Tool) lastPathComponent;
					changeTool(newTool);
				}
			}
		});
		toolshelf.setCellRenderer(new ToolRenderer());
		toolshelf.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));

		JPanel shape = new JPanel();
		shape.setLayout(new BoxLayout(shape, BoxLayout.Y_AXIS));
		shape.setBorder(BorderFactory.createTitledBorder(EditorLabels.getLabel("shapes")));
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

			map.setShowResources(tool instanceof ResourceTool);

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
			shapeButtons.setLayout(new BoxLayout(shapeButtons, BoxLayout.LINE_AXIS));
		}
		shapeButtons.revalidate();
	}

	protected void setShape(ShapeType shape) {
		activeShape = shape;
		// updateShapeButtons();
		shapeSettings.removeAll();
		if (shape != null) {
			for (ShapeProperty property : shape.getProperties()) {
				shapeSettings.add(new ShapePropertyEditor(shape, property));
			}
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

				tool.apply(data, shape, lineAction.getStart(), lineAction.getEnd(), lineAction.getUidy());

				dataTester.retest();
			}
		} else if (action instanceof StartDrawingAction) {
			if (tool != null) {
				StartDrawingAction lineAction = (StartDrawingAction) action;

				ShapeType shape = getActiveShape();

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

				ShapeType shape = getActiveShape();

				tool.start(data, shape, lineAction.getPosition());
				tool.apply(data, shape, lineAction.getPosition(), lineAction.getPosition(), 0);

				endUseStep();
				dataTester.retest();
			}
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
	public void testResult(String result, boolean allowed, ShortPoint2D failPoint) {
		testFailPoint = failPoint;
		startGameButton.setEnabled(allowed);
		testResult.setText(result);
		showErrorsButton.setEnabled(!allowed);
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
