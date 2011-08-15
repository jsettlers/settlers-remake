package jsettlers.graphics.utils;

import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import jsettlers.common.position.IntRectangle;

public class UIList {
	private static final int SCROLL_BAR_Y = 7;

	private static final int SCROLLBAR_WIDTH = 10;

	private final List<UIListItem> items;

	private IntRectangle bounds;

	private final int listHeight;

	// current y offset the user scrolled.
	private int currenty;

	public UIList(List<UIListItem> items, IntRectangle bounds) {
		this.items = Collections.unmodifiableList(items);
		this.bounds = bounds;
		this.listHeight = calculateHeight();
	}

	private int calculateHeight() {
		int height = 0;
		for (UIListItem item : this.items) {
			height += item.getHeight();
		}
		return height;
	}

	public IntRectangle getBounds() {
		return this.bounds;
	}

	public void setBounds(IntRectangle bounds) {
		this.bounds = bounds;
	}

	public void drawAtScreen(GL2 gl) {
		int contentright = this.bounds.getX2() - SCROLLBAR_WIDTH;

		drawScrollbar(gl);
		
		int y = 0;
		for (UIListItem item : this.items) {
			int itemHeight = item.getHeight();
			int maxy = this.bounds.getY2() + this.currenty - y;
			int miny = maxy - itemHeight;
			
			if (miny < this.bounds.getY2() && maxy > this.bounds.getY1()) {
				IntRectangle rect = new IntRectangle(this.bounds.getX1(), miny, contentright, maxy);
				item.drawAt(gl, rect);
			}
			
			y += itemHeight;
		}
	}

	private void drawScrollbar(GL2 gl) {
		int left = this.bounds.getX2() - SCROLLBAR_WIDTH;
		int right = this.bounds.getX2();
		int middle = (left + right) / 2;
		int top = this.bounds.getY2();
		int bottom = this.bounds.getY1();

		gl.glBegin(GL.GL_TRIANGLES);
		gl.glVertex2i(middle, top);
		gl.glVertex2i(right, top - 5);
		gl.glVertex2i(left, top - 5);

		gl.glVertex2i(middle, bottom);
		gl.glVertex2i(right, bottom + 5);
		gl.glVertex2i(left, bottom + 5);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex2i(right, top - SCROLL_BAR_Y);
		gl.glVertex2i(left, top - SCROLL_BAR_Y);
		gl.glVertex2i(left, bottom + SCROLL_BAR_Y);
		gl.glVertex2i(right, bottom + SCROLL_BAR_Y);
		gl.glEnd();

		// the scroll position indicator
		float scroolbarHeightFactor = (float) this.bounds.getHeight() / this.listHeight;

		int barHeight =
		        (int) (scroolbarHeightFactor * (this.bounds.getHeight() - 2 * SCROLL_BAR_Y));
		int bartop =
		        top - SCROLL_BAR_Y - (int) (scroolbarHeightFactor * this.currenty);

		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2i(right, bartop);
		gl.glVertex2i(left, bartop);
		gl.glVertex2i(left, bartop - barHeight);
		gl.glVertex2i(right, bartop - barHeight);
		gl.glEnd();
	}
}
