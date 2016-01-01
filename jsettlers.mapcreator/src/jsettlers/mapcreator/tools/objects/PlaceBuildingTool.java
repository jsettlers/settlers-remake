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
package jsettlers.mapcreator.tools.objects;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.main.IPlayerSetter;

public class PlaceBuildingTool extends PlaceMapObjectTool {

	private final EBuildingType type;
	private final IPlayerSetter player;

	public PlaceBuildingTool(EBuildingType type, IPlayerSetter player) {
		super(null);
		this.type = type;
		this.player = player;
	}

	@Override
	public MapObject getObject() {
		return new BuildingObject(type, (byte) player.getActivePlayer());
	}

	@Override
	public String getName() {
		return String.format(EditorLabels.getLabel("buildingdescr"), Labels.getName(type));
	}

}
