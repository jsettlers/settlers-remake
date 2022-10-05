/*******************************************************************************
 * Copyright (c) 2015-2018
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
package go.graphics.swing;

import java.awt.BorderLayout;
import java.awt.Color;

import go.graphics.DrawmodeListener;
import go.graphics.RedrawListener;
import go.graphics.area.Area;
import go.graphics.event.GOEvent;
import go.graphics.swing.contextcreator.EBackendType;
import go.graphics.swing.contextcreator.ContextException;

/**
 * This class lets you embed areas into swing components.
 * 
 * @author michael
 * @author paul
 */
public class AreaContainer extends ContextContainer implements RedrawListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8204496712425576430L;
	protected final Area area;

	/**
	 * creates a new area container
	 * 
	 * @param area
	 *            The area to display
	 */
	public AreaContainer(Area area) {
		this(area, EBackendType.DEFAULT, false, 0);
	}

	public AreaContainer(Area area, EBackendType backend, boolean debug, float guiScale) {
		super(backend, new BorderLayout(), debug);
		this.area = area;
		this.guiScale = guiScale;

		if(cc instanceof DrawmodeListener) {
			area.setDrawmodeListener((DrawmodeListener) cc);
		}

		setBackground(Color.BLACK);

		area.addRedrawListener(this);

	}

	public void resizeContext(int width, int height) throws ContextException {
		super.resizeContext(width, height);
		area.setWidth(width);
		area.setHeight(height);

	}

	public void draw() throws ContextException {
		super.draw();
		area.drawArea(context);
	}

	@Override
	public void handleEvent(GOEvent event) {
		area.handleEvent(event);
	}

	public void notifyResize() {
		cc.componentResized(null);
	}
}
