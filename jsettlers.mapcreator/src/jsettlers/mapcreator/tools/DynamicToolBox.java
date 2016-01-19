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
package jsettlers.mapcreator.tools;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

/**
 * Tool list with dynamic content
 * 
 * @author Andreas Butti
 *
 */
public class DynamicToolBox extends ToolBox {

	/**
	 * Tools
	 */
	private final List<ToolNode> tools = new ArrayList<>();

	/**
	 * Constructor
	 * 
	 * @param name
	 *            Name of the node
	 */
	public DynamicToolBox(String name) {
		super(name, null);
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 *            Name of the node
	 * @param tools
	 *            Array with the tools
	 */
	public DynamicToolBox(String name, ToolNode[] tools) {
		super(name, null);

		for (ToolNode t : tools) {
			this.tools.add(t);
		}
	}

	@Override
	public ToolNode getTool(int index) {
		return tools.get(index);
	}

	@Override
	public int getToolLength() {
		return tools.size();
	}

	/**
	 * Add a tool
	 * 
	 * @param t
	 *            To add
	 */
	public void add(ToolNode t) {
		tools.add(t);
	}

	@Override
	public Icon getIcon() {
		// use default icon
		return null;
	}
}
