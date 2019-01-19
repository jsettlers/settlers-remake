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
package jsettlers.graphics.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import go.graphics.GLDrawContext;
import jsettlers.common.images.ImageLink;
import jsettlers.common.position.FloatRectangle;
import jsettlers.common.action.Action;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.draw.ImageProvider;

/**
 * This is a panel that holds UI elements and can have a background.
 * <p>
 * All elements are positioned relatively.
 *
 * @author michael
 */
public class UIPanel implements UIElement {
	private final LinkedList<ChildLink> children = new LinkedList<>();
	private FloatRectangle position = new FloatRectangle(0, 0, 1, 1);

	private ImageLink background;

	private boolean attached = false;

	/**
	 * Sets the background. file=-1 means no background
	 *
	 */
	public void setBackground(ImageLink imageLink) {
		this.background = imageLink;
	}

	/**
	 * Adds a child to the panel.
	 *
	 * @param child
	 *            The child to add.
	 * @param left
	 *            relative left border (0..1).
	 * @param bottom
	 *            relative bottom border (0..1).
	 * @param right
	 *            relative right border (0..1).
	 * @param top
	 *            relative top border (0..1).
	 */
	public void addChild(UIElement child, float left, float bottom,
			float right, float top) {
		if (child == null) {
			throw new NullPointerException();
		}
		this.children.add(new ChildLink(child, left, bottom, right, top));
		if (attached) {
			child.onAttach();
		}
	}

	public void removeChild(UIElement child) {
		for (Iterator<ChildLink> iterator = children.iterator(); iterator.hasNext();) {
			ChildLink l = iterator.next();
			if (l.child.equals(child)) {
				if (attached) {
					l.child.onDetach();
				}
				iterator.remove();
				break;
			}
		}
	}

	public List<UIElement> getChildren() {
		ArrayList<UIElement> list = new ArrayList<>();
		for (ChildLink c : children) {
			list.add(c.child);
		}
		return list;
	}

	@Override
	public void drawAt(GLDrawContext gl) {
		drawBackground(gl);

		drawChildren(gl);
	}

	protected void drawChildren(GLDrawContext gl) {
		if (children.size() > 0) {
			for (ChildLink link : children) {
				link.drawAt(gl, position);
			}
		}
	}

	protected void drawBackground(GLDrawContext gl) {
		ImageLink link = getBackgroundImage();
		if (link != null) {
			FloatRectangle position = getPosition();
			Image image = ImageProvider.getInstance().getImage(link, position.getWidth(), position.getHeight());
			drawAtRect(gl, image, position);
		}
	}

	/**
	 * Draws an image at a given rect
	 *
	 * @param gl
	 *            The context to use
	 * @param image
	 *            The image to draw
	 * @param position
	 *            The position to draw the image at
	 */
	protected void drawAtRect(GLDrawContext gl, Image image, FloatRectangle position) {
		float minX = position.getMinX();
		float minY = position.getMinY();
		float maxX = position.getMaxX();
		float maxY = position.getMaxY();
		image.drawImageAtRect(gl, minX, minY, maxX-minX, maxY-minY);
	}

	protected ImageLink getBackgroundImage() {
		return background;
	}

	private class ChildLink {

		private final UIElement child;
		private final float left;
		private final float right;
		private final float top;
		private final float bottom;

		public ChildLink(UIElement child, float left, float bottom, float right, float top) {
			this.child = child;
			this.left = left;
			this.right = right;
			this.top = top;
			this.bottom = bottom;
		}

		public void drawAt(GLDrawContext gl, FloatRectangle pos) {
			child.setPosition(new FloatRectangle((left * pos.getWidth())+pos.getMinX(), (bottom * pos.getHeight())+pos.getMinY(),
					(right * pos.getWidth())+pos.getMinX(), (top * pos.getHeight())+pos.getMinY()));
			child.drawAt(gl);
		}

		public Action getActionRelative(float parentx, float parenty) {
			if (left <= parentx && parentx <= right && bottom <= parenty && parenty <= top) {
				float relativex = (parentx - left) / (right - left);
				float relativey = (parenty - bottom) / (top - bottom);
				return child.getAction(relativex, relativey);
			} else {
				return null;
			}
		}

		public String getDesctiptionRelative(float parentx, float parenty) {
			if (left <= parentx && parentx <= right && bottom <= parenty && parenty <= top) {
				float relativex = (parentx - left) / (right - left);
				float relativey = (parenty - bottom) / (top - bottom);
				return child.getDescription(relativex, relativey);
			} else {
				return null;
			}
		}
	}

	@Override
	public void setPosition(FloatRectangle position) {
		this.position = position;
	}

	public FloatRectangle getPosition() {
		return position;
	}

	public void removeAll() {
		if (attached) {
			for (ChildLink link : children) {
				link.child.onDetach();
			}
		}
		this.children.clear();
	}

	@Override
	public Action getAction(float relativex, float relativey) {
		for (ChildLink link : children) {
			Action action = link.getActionRelative(relativex, relativey);
			if (action != null) {
				return action;
			}
		}
		return null;
	}

	@Override
	public String getDescription(float relativex, float relativey) {
		for (ChildLink link : children) {
			String description = link.getDesctiptionRelative(relativex, relativey);
			if (description != null) {
				return description;
			}
		}
		return null;
	}

	@Override
	public void onAttach() {
		if (!attached) {
			for (ChildLink link : children) {
				link.child.onAttach();
			}
		}
		attached = true;
	}

	@Override
	public void onDetach() {
		if (attached) {
			for (ChildLink link : children) {
				link.child.onDetach();
			}
		}
		attached = false;
	}
}
