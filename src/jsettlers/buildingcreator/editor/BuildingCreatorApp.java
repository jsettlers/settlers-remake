package jsettlers.buildingcreator.editor;

import go.graphics.swing.sound.SwingSoundPlayer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jsettlers.buildingcreator.editor.map.BuildingtestMap;
import jsettlers.buildingcreator.editor.map.PseudoTile;
import jsettlers.common.Color;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.RelativeBricklayer;
import jsettlers.common.buildings.RelativeStack;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.MainUtils;
import jsettlers.graphics.JSettlersScreen;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.map.IMapInterfaceListener;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.startscreen.interfaces.FakeMapGame;
import jsettlers.main.swing.SwingManagedJSettlers;

/**
 * This is the main building creator class.
 * 
 * @author michael
 */
public class BuildingCreatorApp implements IMapInterfaceListener {
	private BuildingDefinition definition;
	private final BuildingtestMap map;

	private ToolType tool = ToolType.SET_BLOCKED;
	private JLabel positionDisplayer;
	private JFrame window;

	private BuildingCreatorApp(HashMap<String, String> argsMap) throws FileNotFoundException, IOException {
		SwingManagedJSettlers.setupResourceManagers(argsMap, new File("../jsettlers.main.swing/config.prp"));

		EBuildingType type = askType();

		definition = new BuildingDefinition(type);
		map = new BuildingtestMap(definition);
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				reloadColor(new ShortPoint2D(x, y));
			}
		}

		MapInterfaceConnector connector = startMapWindow();
		connector.addListener(this);

		JPanel menu = generateMenu();

		window = new JFrame("Edit " + type.toString());
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.add(menu);
		window.pack();
		window.setVisible(true);

		connector.fireAction(new Action(EActionType.TOGGLE_DEBUG));
	}

	private JPanel generateMenu() {
		JPanel menu = new JPanel();
		menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
		// menu.setPreferredSize(new Dimension(200, 100));
		// actionList = new JPanel();
		// actionList.setLayout(new BoxLayout(actionList, BoxLayout.Y_AXIS));
		// menu.add(new JScrollPane(actionList));

		menu.add(createToolChangeBar());

		// JButton addButton = new JButton("add new action");
		// addButton.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// addNewAction();
		// }
		// });
		// menu.add(addButton);

		JButton xmlButton = new JButton("show xml data");
		xmlButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showXML();
			}
		});
		menu.add(xmlButton);
		positionDisplayer = new JLabel();
		menu.add(positionDisplayer);
		return menu;
	}

	private MapInterfaceConnector startMapWindow() {
		JSettlersScreen gui = SwingManagedJSettlers.startGui();
		MapContent content = new MapContent(new FakeMapGame(map), new SwingSoundPlayer());
		gui.setContent(content);
		MapInterfaceConnector connector = content.getInterfaceConnector();
		return connector;
	}

	private JButton createToolChangeBar() {
		JButton button = new JButton("Select tool...");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				tool = (ToolType) JOptionPane.showInputDialog(null, "Select building type", "Building Type", JOptionPane.QUESTION_MESSAGE, null,
						ToolType.values(), tool);

				for (int x = 0; x < map.getWidth(); x++) {
					for (int y = 0; y < map.getWidth(); y++) {
						reloadColor(new ShortPoint2D(x, y));
					}
				}
			}
		});
		return button;
	}

	private EBuildingType askType() {
		return (EBuildingType) JOptionPane.showInputDialog(null, "Select building type", "Building Type", JOptionPane.QUESTION_MESSAGE, null,
				EBuildingType.values, null);
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {

		new BuildingCreatorApp(MainUtils.createArgumentsMap(args));
	}

	@Override
	public void action(Action action) {
		if (action instanceof PointAction) {
			PointAction sAction = (PointAction) action;
			ShortPoint2D pos = sAction.getPosition();
			RelativePoint relative = absoluteToRelative(pos);

			positionDisplayer.setText("x = " + (pos.x - BuildingtestMap.OFFSET) + ", y = " + (pos.y - BuildingtestMap.OFFSET));

			if (tool == ToolType.SET_BLOCKED) {
				toogleUsedTile(relative);
			} else if (tool == ToolType.SET_DOOR) {
				setDoor(relative);
			} else if (tool == ToolType.ADD_STACK) {
				addStack(relative);
			} else if (tool == ToolType.REMOVE_STACK) {
				removeStack(relative);
			} else if (tool == ToolType.SET_FLAG) {
				setFlag(relative);
			} else if (tool == ToolType.SET_BUILDMARK) {
				definition.toggleBuildmarkStatus(relative);
			} else if (tool == ToolType.BRICKLAYER_NE) {
				definition.toggleBrickayer(relative, EDirection.NORTH_EAST);
			} else if (tool == ToolType.BRICKLAYER_NW) {
				definition.toggleBrickayer(relative, EDirection.NORTH_WEST);
			}

			reloadColor(pos);
		}
	}

	private void removeStack(RelativePoint relative) {
		definition.removeStack(relative);
	}

	private void addStack(RelativePoint relative) {
		EMaterialType material = (EMaterialType) JOptionPane.showInputDialog(null, "Select building type", "Building Type",
				JOptionPane.QUESTION_MESSAGE, null, EMaterialType.values(), tool);
		Integer buildrequired = (Integer) JOptionPane.showInputDialog(null, "Select building type", "Building Type", JOptionPane.QUESTION_MESSAGE,
				null, new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }, tool);
		if (material != null && buildrequired != null) {
			definition.setStack(relative, material, buildrequired.intValue());
		}
	}

	private void setDoor(RelativePoint tile) {
		RelativePoint oldDoor = definition.getDoor();
		ShortPoint2D oldPos = relativeToAbsolute(oldDoor);
		reloadColor(oldPos);

		definition.setDoor(tile);
	}

	private void setFlag(RelativePoint tile) {
		RelativePoint oldFlag = definition.getFlag();
		ShortPoint2D oldPos = relativeToAbsolute(oldFlag);
		reloadColor(oldPos);

		definition.setFlag(tile);
	}

	private ShortPoint2D relativeToAbsolute(RelativePoint oldDoor) {
		ShortPoint2D oldPos = new ShortPoint2D(oldDoor.getDx() + BuildingtestMap.OFFSET, oldDoor.getDy() + BuildingtestMap.OFFSET);
		return oldPos;
	}

	private RelativePoint absoluteToRelative(ShortPoint2D pos) {
		RelativePoint tile = new RelativePoint(pos.x - BuildingtestMap.OFFSET, pos.y - BuildingtestMap.OFFSET);
		return tile;
	}

	private void toogleUsedTile(RelativePoint relative) {
		if (definition.getBlockedStatus(relative)) {
			definition.setBlockedStatus(relative, false, false);
		} else if (definition.getProtectedStatus(relative)) {
			definition.setBlockedStatus(relative, true, true);
		} else {
			definition.setBlockedStatus(relative, true, false);
		}
	}

	private void reloadColor(ShortPoint2D pos) {
		PseudoTile tile = map.getTile(pos);
		ArrayList<Color> colors = new ArrayList<Color>();

		RelativePoint relative = absoluteToRelative(pos);
		if (definition.getBlockedStatus(relative)) {
			colors.add(new Color(0xff0343df));
		} else if (definition.getProtectedStatus(relative)) {
			colors.add(new Color(0xff75bbfd));
		}

		if (tool == ToolType.SET_BUILDMARK) {
			if (definition.getBuildmarkStatus(relative)) {
				colors.add(new Color(0xfff97306));
			}
		}

		if (tool == ToolType.SET_DOOR) {
			if (definition.getDoor().equals(relative)) {
				colors.add(new Color(0xfff97306));
			}
		}

		if (tool == ToolType.SET_FLAG) {
			if (definition.getFlag().equals(relative)) {
				colors.add(new Color(0xfff97306));
			}
		}

		if (tool == ToolType.ADD_STACK || tool == ToolType.REMOVE_STACK) {
			if (definition.getStack(relative) != null) {
				colors.add(new Color(0xfff97306));
				tile.setStack(new MapStack(definition.getStack(relative)));
			} else {
				tile.setStack(null);
			}
		}

		if (tool == ToolType.BRICKLAYER_NE || tool == ToolType.BRICKLAYER_NW) {
			if (definition.getBricklayerStatus(relative)) {
				colors.add(new Color(0xfff97306));
			}
		}

		if (!colors.isEmpty()) {
			tile.setDebugColor(mixColors(colors));
		} else {
			tile.setDebugColor(0);
		}
	}

	private static int mixColors(ArrayList<Color> colors) {
		float bluesum = 0;
		float redsum = 0;
		float greensum = 0;
		for (Color color : colors) {
			bluesum += color.getBlue();
			redsum += color.getRed();
			greensum += color.getGreen();
		}
		int color = Color.getARGB(redsum / colors.size(), greensum / colors.size(), bluesum / colors.size(), 1);
		return color;
	}

	private void showXML() {
		StringBuilder builder = new StringBuilder("");
		for (RelativePoint tile : definition.getBlocked()) {
			builder.append("\t<blocked dx=\"");
			builder.append(tile.getDx());
			builder.append("\" dy=\"");
			builder.append(tile.getDy());
			builder.append("\" block=\"true\" />\n");
		}
		for (RelativePoint tile : definition.getJustProtected()) {
			builder.append("\t<blocked dx=\"");
			builder.append(tile.getDx());
			builder.append("\" dy=\"");
			builder.append(tile.getDy());
			builder.append("\" block=\"false\" />\n");
		}

		RelativePoint door = definition.getDoor();
		builder.append("\t<door dx=\"");
		builder.append(door.getDx());
		builder.append("\" dy=\"");
		builder.append(door.getDy());
		builder.append("\" />\n");

		for (RelativeStack stack : definition.getStacks()) {
			builder.append("\t<stack dx=\"");
			builder.append(stack.getDx());
			builder.append("\" dy=\"");
			builder.append(stack.getDy());
			builder.append("\" material=\"");
			builder.append(stack.getMaterialType().name());
			builder.append("\" buildrequired=\"");
			builder.append(stack.requiredForBuild());
			builder.append("\" />\n");
		}
		for (RelativeBricklayer bricklayer : definition.getBricklayers()) {
			builder.append("\t<bricklayer dx=\"");
			builder.append(bricklayer.getPosition().getDx());
			builder.append("\" dy=\"");
			builder.append(bricklayer.getPosition().getDy());
			builder.append("\" direction=\"");
			builder.append(bricklayer.getDirection());
			builder.append("\" />\n");
		}

		RelativePoint flag = definition.getFlag();
		builder.append("\t<flag dx=\"");
		builder.append(flag.getDx());
		builder.append("\" dy=\"");
		builder.append(flag.getDy());
		builder.append("\" />\n");

		for (RelativePoint mark : definition.getBuildmarks()) {
			builder.append("\t<buildmark dx=\"");
			builder.append(mark.getDx());
			builder.append("\" dy=\"");
			builder.append(mark.getDy());
			builder.append("\" />\n");
		}

		JDialog dialog = new JDialog(window, "xml");
		dialog.add(new JScrollPane(new JTextArea(builder.toString())));
		dialog.pack();
		dialog.setVisible(true);
	}
}
