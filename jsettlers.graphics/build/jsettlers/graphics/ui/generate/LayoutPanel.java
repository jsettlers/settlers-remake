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
package jsettlers.graphics.ui.generate;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;

public class LayoutPanel {

	public final int x;
	public final int y;
	public final int width;
	public final int height;
	private final String id;
	private final String className;
	private final ArrayList<LayoutPanel> children = new ArrayList<>();
	private final ArrayList<AbstractArgument> arguments = new ArrayList<>();

	public LayoutPanel(Attributes attributes) {
		this.x = getIntAttribute(attributes, "x", 0);
		this.y = getIntAttribute(attributes, "y", 0);
		if (attributes.getIndex("width") > 0) {
			this.width = getIntAttribute(attributes, "width", 100);
		} else {
			this.width = getIntAttribute(attributes, "maxx", 100) - x;
		}
		if (attributes.getIndex("height") > 0) {
			this.height = getIntAttribute(attributes, "height", 100);
		} else {
			this.height = getIntAttribute(attributes, "maxy", 100) - y;
		}
		this.id = attributes.getValue("id");
		if (id != null && !Pattern.matches("\\w+", id)) {
			throw new IllegalArgumentException("Illegal ID: " + id);
		}
		this.className = getClassName(attributes);

		// try {
		// Class<?> classTest = Class.forName(className);
		// if (!UIPanel.class.isAssignableFrom(classTest)) {
		// throw new IllegalArgumentException("This is no valid UI panel: " + className);
		// }
		// } catch (ClassNotFoundException e) {
		// throw new IllegalArgumentException("There is no such class: " + className);
		// }
	}

	private String getClassName(Attributes attributes) {
		String name = attributes.getValue("class");
		if (name == null) {
			name = "UIPanel";
		}
		if (!name.contains(".")) {
			name = "jsettlers.graphics.ui." + name;
		}
		return name;
	}

	private int getIntAttribute(Attributes attributes, String qName, int defaultValue) {
		String val = attributes.getValue(qName);
		if (val == null) {
			return defaultValue;
		} else {
			return Integer.parseInt(val);
		}
	}

	public void addChild(LayoutPanel panel) {
		children.add(panel);
	}

	public ArrayList<LayoutPanel> getChildren() {
		return children;
	}

	public String getId() {
		return id;
	}

	public String getClassName() {
		return className;
	}

	public void addArgument(AbstractArgument imageArgument) {
		arguments.add(imageArgument);
	}

	public ArrayList<AbstractArgument> getArguments() {
		return arguments;
	}
}
