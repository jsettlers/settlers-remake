/*******************************************************************************
 * Copyright (c)  - 2017
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

import jsettlers.logic.map.loading.data.objects.MapDataObject;
import jsettlers.logic.map.loading.data.objects.MovableObject;
import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.control.IPlayerSetter;
import jsettlers.mapcreator.localization.EditorLabels;

import java.util.Locale;

public class PlaceMovableTool extends PlaceMapObjectTool {

	private final EMovableType type;
	private final IPlayerSetter player;

	public PlaceMovableTool(EMovableType type, IPlayerSetter player) {
		super(null);
		this.type = type;
		this.player = player;
		this.translatedName = String.format(Locale.ENGLISH, EditorLabels.getLabel("tool.place"), Labels.getName(type));
	}

	@Override
	public MapDataObject getObject() {
		return new MovableObject(type, (byte) player.getActivePlayer());
	}
}
