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
package go.graphics.swing;

import go.graphics.area.Area;

import javax.swing.JFrame;

/**
 * This is a window that consists of exactly one area.
 * 
 * @author michael
 *
 */
public class AreaWindow {
	private final Area area;
	private final JFrame frame;

	public AreaWindow(Area area) {
		this.area = area;
		this.frame = new JFrame();
		frame.add(new AreaContainer(area));
		frame.setSize(500, 500);
		frame.pack();
		frame.setVisible(true);
	}

	public void close() {
		frame.setVisible(false);
		frame.dispose();
	}

	public Area getArea() {
		return area;
	}
}
