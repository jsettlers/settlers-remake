/*******************************************************************************
 * Copyright (c) 2015 - 2016
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
package jsettlers.mapcreator.tools;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Icon;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.tools.icons.ToolIcon;
import jsettlers.mapcreator.tools.shapes.EShapeType;
import jsettlers.mapcreator.tools.shapes.ShapeType;

/**
 * Base class for tools, loads the name and the Icon
 * 
 * @author Andreas Butti
 *
 */
public abstract class AbstractTool implements Tool {

	/**
	 * Icon of the tool
	 */
	protected Icon icon;

	/**
	 * Translated name of the tool
	 */
	protected String translatedName;

	/**
	 * Shape types supported by this tool
	 */
	protected final Set<EShapeType> shapeTypes = new HashSet<>();

	/**
	 * Constructor
	 * 
	 * @param name
	 *            Name of the tool, to load translation and icon
	 */
	public AbstractTool(String name) {
		icon = ToolIcon.loadIcon(name + ".png");
		translatedName = EditorLabels.getLabel("tool." + name);
	}

	/**
	 * Constructor, provide icon and name
	 * 
	 * @param icon
	 *            Icon
	 * @param translatedName
	 *            Translated name
	 */
	public AbstractTool(Icon icon, String translatedName) {
		this.icon = icon;
		this.translatedName = translatedName;
	}

	@Override
	public String getName() {
		return translatedName;
	}

	@Override
	public Icon getIcon() {
		return icon;
	}

	@Override
	public void start(MapData data, ShapeType shape, ShortPoint2D pos) {
		// can be overridden, but is mostly empty
	}

	@Override
	public final Set<EShapeType> getSupportedShapes() {
		return Collections.unmodifiableSet(shapeTypes);
	}
}
