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
package jsettlers.buildingcreator.editor;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import jsettlers.buildingcreator.editor.jobeditor.BuildingPersonJobProperties;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.RelativeBricklayer;
import jsettlers.common.buildings.stacks.ConstructionStack;
import jsettlers.common.buildings.stacks.RelativeStack;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.RelativePoint;

/**
 * This represents the definition of a building and all the properties it can have.
 * 
 * @author michael
 */
public class BuildingDefinition {
	private final EBuildingType type;

	/**
	 * A table of known actions and their names.
	 */
	private final Hashtable<String, BuildingPersonJobProperties> actions = new Hashtable<>();

	private final LinkedList<ConstructionStack> constructionStacks = new LinkedList<>();
	private final LinkedList<RelativeStack> requestStacks = new LinkedList<>();
	private final LinkedList<RelativeStack> offerStacks = new LinkedList<>();

	private final LinkedList<RelativeBricklayer> bricklayers = new LinkedList<>();

	private final LinkedList<RelativePoint> blocked = new LinkedList<>();
	private final LinkedList<RelativePoint> justProtected = new LinkedList<>();

	private final LinkedList<RelativePoint> buildmarks = new LinkedList<>();
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

		constructionStacks.addAll(Arrays.asList(type.getConstructionStacks()));
		requestStacks.addAll(Arrays.asList(type.getRequestStacks()));
		offerStacks.addAll(Arrays.asList(type.getOfferStacks()));
	}

	public Set<String> getActionNames() {
		return actions.keySet();
	}

	public BuildingPersonJobProperties getActionByName(String name) {
		return actions.get(name);
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
		for (RelativeBricklayer bricklayer : bricklayers) {
			if (relative.equals(bricklayer)) {
				return bricklayer;
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

	public void setConstructionStack(RelativePoint relative, EMaterialType material, int required) {
		removeStack(relative);
		constructionStacks.add(new ConstructionStack(relative.getDx(), relative.getDy(), material, (short) required));
	}

	public void setRequestStack(RelativePoint relative, EMaterialType material) {
		removeStack(relative);
		requestStacks.add(new RelativeStack(relative.getDx(), relative.getDy(), material));
	}

	public void setOfferStack(RelativePoint relative, EMaterialType material) {
		removeStack(relative);
		offerStacks.add(new RelativeStack(relative.getDx(), relative.getDy(), material));
	}

	public void removeStack(RelativePoint relative) {
		constructionStacks.remove(relative); // Uses that stack is a relative point
		requestStacks.remove(relative);
		offerStacks.remove(relative);
	}

	public LinkedList<RelativePoint> getBlocked() {
		return blocked;
	}

	public LinkedList<RelativePoint> getJustProtected() {
		return justProtected;
	}

	public LinkedList<ConstructionStack> getConstructionStacks() {
		return constructionStacks;
	}

	public LinkedList<RelativeStack> getRequestStacks() {
		return requestStacks;
	}

	public LinkedList<RelativeStack> getOfferStacks() {
		return offerStacks;
	}

	public LinkedList<RelativePoint> getBuildmarks() {
		return buildmarks;
	}

	public LinkedList<RelativeBricklayer> getBricklayers() {
		return bricklayers;
	}
}
