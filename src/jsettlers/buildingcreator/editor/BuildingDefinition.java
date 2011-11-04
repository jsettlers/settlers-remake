package jsettlers.buildingcreator.editor;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import jsettlers.buildingcreator.editor.jobeditor.BuildingPersonJobProperties;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.RelativeBricklayer;
import jsettlers.common.buildings.RelativeStack;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.RelativePoint;

/**
 * This represents the definition of a building and all the properties it can
 * have.
 * 
 * @author michael
 */
public class BuildingDefinition {
	private final EBuildingType type;

	/**
	 * A table of known actions and their names.
	 */
	private Hashtable<String, BuildingPersonJobProperties> actions =
	        new Hashtable<String, BuildingPersonJobProperties>();

	private LinkedList<RelativeStack> stacks = new LinkedList<RelativeStack>();
	private LinkedList<RelativeBricklayer> bricklayers =
	        new LinkedList<RelativeBricklayer>();

	private LinkedList<RelativePoint> blocked = new LinkedList<RelativePoint>();
	private LinkedList<RelativePoint> justProtected = new LinkedList<RelativePoint>();

	private LinkedList<RelativePoint> buildmarks = new LinkedList<RelativePoint>();
	private RelativePoint door = new RelativePoint(0, 0);
	private RelativePoint flag = new RelativePoint(0, 0);

	public BuildingDefinition(EBuildingType type) {
		this.type = type;
		for (RelativePoint pos : type.getBlockedTiles()) {
			blocked.add(pos);
		}
		for (RelativePoint pos : type.getProtectedTiles()) {
			if (!blocked.contains(pos)) {
				justProtected.add(pos);
			}
		}

		buildmarks.addAll(Arrays.asList(type.getBuildmarks()));
		bricklayers.addAll(Arrays.asList(type.getBricklayers()));

		door = type.getDoorTile();
		flag = type.getFlag();
		stacks.addAll(Arrays.asList(type.getRequestStacks()));
	}

	public Set<String> getActionNames() {
		return actions.keySet();
	}

	public BuildingPersonJobProperties getActionByName(String name) {
		return actions.get(name);
	}

	public void removeAction(String name) {
		// TODO: look if there are references to this action!
		actions.remove(name);
	}

	public void addAction(String name) {
		actions.put(name, new BuildingPersonJobProperties());
	}

	public EBuildingType getType() {
		return type;
	}

	public boolean getBlockedStatus(RelativePoint relative) {
		return blocked.contains(relative);
	}

	public boolean getProtectedStatus(RelativePoint relative) {
		return blocked.contains(relative) || justProtected.contains(relative);
	}

	public void setBlockedStatus(RelativePoint relative, boolean isProtected,
	        boolean isBlocked) {
		if (isProtected) {
			if (isBlocked) {
				justProtected.remove(relative);
				if (!blocked.contains(relative)) {
					blocked.add(relative);
				}
			} else {
				blocked.remove(relative);
				justProtected.add(relative);
			}
		} else {
			blocked.remove(relative);
			justProtected.remove(relative);
		}

	}

	public boolean getBuildmarkStatus(RelativePoint relative) {
		return buildmarks.contains(relative);
	}

	public void toggleBuildmarkStatus(RelativePoint relative) {
		if (buildmarks.contains(relative)) {
			buildmarks.remove(relative);
		} else {
			buildmarks.add(relative);
		}
	}

	public void toggleBrickayer(RelativePoint relative, EDirection direction) {
		RelativeBricklayer bricklayer = getBricklayerAt(relative);
		if (bricklayer != null) {
			bricklayers.remove(bricklayer);
		} else {
			bricklayers.add(new RelativeBricklayer(relative.getDx(), relative
			        .getDy(), direction));
		}
	}

	public boolean getBricklayerStatus(RelativePoint relative) {
		return null != getBricklayerAt(relative);
	}

	private RelativeBricklayer getBricklayerAt(RelativePoint relative) {
		for (RelativeBricklayer b : bricklayers) {
			if (b.getPosition().equals(relative)) {
				return b;
			}
		}
		return null;
	}

	public void setFlag(RelativePoint flag) {
		this.flag = flag;
	}

	public void setDoor(RelativePoint door) {
		this.door = door;
	}

	public String toXML() {
		return "";
	}

	public RelativePoint getDoor() {
		return door;
	}

	public RelativePoint getFlag() {
		return flag;
	}

	public void setStack(RelativePoint relative, EMaterialType material,
	        int required) {
		removeStack(relative);
		stacks.add(new RelativeStack(relative.getDx(), relative.getDy(),
		        material, (short) required));
	}

	public void removeStack(RelativePoint relative) {
		stacks.remove(relative); // Uses that stack is a relative point
	}

	public RelativeStack getStack(RelativePoint relative) {
		int index = stacks.indexOf(relative);
		if (index >= 0) {
			return stacks.get(index);
		} else {
			return null;
		}
	}

	public LinkedList<RelativePoint> getBlocked() {
		return blocked;
	}

	public LinkedList<RelativePoint> getJustProtected() {
		return justProtected;
	}

	public LinkedList<RelativeStack> getStacks() {
		return stacks;
	}

	public LinkedList<RelativePoint> getBuildmarks() {
		return buildmarks;
	}

	public LinkedList<RelativeBricklayer> getBricklayers() {
		return bricklayers;
	}
}
