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
package jsettlers.mapcreator.main.window.sidebar;

import javax.swing.JTabbedPane;

import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.mapvalidator.IScrollToAble;
import jsettlers.mapcreator.mapvalidator.result.fix.FixData;

/**
 * The sidebar with all tabs
 * 
 * @author Andreas Butti
 */
public class Sidebar extends JTabbedPane {
	private static final long serialVersionUID = 1L;

	/**
	 * Sidebar with the tools
	 */
	private final ToolSidebar toolSidebar;

	/**
	 * Sidebar with the erros
	 */
	private final ErrorSidebar errorSidebar;

	/**
	 * Constructor
	 * 
	 * @param toolSidebar
	 *            Sidebar with the tools
	 * @param scrollTo
	 *            Interface to scroll to position
	 */
	public Sidebar(ToolSidebar toolSidebar, IScrollToAble scrollTo) {
		this.toolSidebar = toolSidebar;
		this.errorSidebar = new ErrorSidebar(scrollTo);

		addTab(EditorLabels.getLabel("sidebar.tools"), toolSidebar);
		addTab(EditorLabels.getLabel("sidebar.errors"), errorSidebar);
	}

	/**
	 * @param fixData
	 *            Fix data helper
	 */
	public void setFixData(FixData fixData) {
		errorSidebar.setFixData(fixData);
	}

	/**
	 * @return Sidebar with the erros
	 */
	public ErrorSidebar getErrorSidebar() {
		return errorSidebar;
	}

	/**
	 * Select the error tab
	 */
	public void selectError() {
		setSelectedComponent(errorSidebar);
	}

	/**
	 * Select the tools tab
	 */
	public void showTools() {
		setSelectedComponent(toolSidebar);
	}
}
