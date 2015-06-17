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
package jsettlers.graphics.androidui;

import go.graphics.GLDrawContext;
import go.graphics.UIPoint;
import jsettlers.common.images.ImageLink;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.ui.UIPanel;

/**
 * This is the main menu on the left side of the screen.
 * <p>
 * 
 * @author michael
 *
 */
public class MainMenu {
	private static final float WIDTH = 300;
	private static final float COLLAPSED_MARGIN = 10;
	private static final float COLLAPSED_WIDTH = 20;
	private AnimatedFader size = new AnimatedFader(0, 1);
	private float height = 100;

	private final UIPanel collapsedMark = new UIPanel();
	private final UIPanel content = new UIPanel();

	public MainMenu() {
		collapsedMark.setBackground(ImageLink.fromName("menu_collapsed", 0));

		content.setBackground(ImageLink.fromName("menu_gackground", 0));
	}

	public void setScreenSize(float newWidth, float newHeight) {
		this.height = newHeight;
		collapsedMark.setPosition(new FloatRectangle(0, COLLAPSED_MARGIN, COLLAPSED_WIDTH, height - COLLAPSED_MARGIN));
	}

	public void drawAt(GLDrawContext gl) {
		float sizeValue = this.size
				.getValue();
		if (sizeValue < .01) {
			drawCollappsedMarker(gl);
		} else {
			gl.glPushMatrix();
			gl.glTranslatef((-1 + sizeValue) * WIDTH, 0, 0);
			drawContent(gl);
			gl.glPopMatrix();
		}
	}

	private void drawContent(GLDrawContext gl) {
		content.drawAt(gl);
	}

	private void drawCollappsedMarker(GLDrawContext gl) {
		collapsedMark.drawAt(gl);
	}

	public boolean containsPoint(UIPoint position) {
		float sizeValue = this.size
				.getValue();
		if (sizeValue < .01) {
			return position.getX() < COLLAPSED_WIDTH;
		} else {
			double x = sizeValue * WIDTH;
			return position.getX() < x;
		}
	}

	public Action getActionFor(UIPoint position) {
		float sizeValue = this.size
				.getValue();
		if (sizeValue < .01) {
			return new FadeAnimatedFaderAction(size, 1);
		}
		// TODO Auto-generated method stub
		return null;
	}

}
