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

import javax.swing.Icon;

/**
 * This is a tool that only holds some more tools.
 * 
 * @author michael
 */
public class ToolBox implements ToolNode {

	/**
	 * Name of the node
	 */
	private final String name;

	/**
	 * Array with the tools
	 */
	private final ToolNode[] tools;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            Name of the node
	 * @param tools
	 *            Array with the tools
	 */
	public ToolBox(String name, ToolNode[] tools) {
		this.name = name;
		this.tools = tools;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Get tool at index
	 * 
	 * @param index
	 *            Index
	 * @return Tool
	 */
	public ToolNode getTool(int index) {
		return tools[index];
	}

	/**
	 * @return Tool length
	 */
	public int getToolLength() {
		return tools.length;
	}

	@Override
	public Icon getIcon() {
		// use default icon
		return null;
	}
}
