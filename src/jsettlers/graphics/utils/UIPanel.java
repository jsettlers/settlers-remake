package jsettlers.graphics.utils;

import go.graphics.GLDrawContext;

import java.util.LinkedList;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.position.IntRectangle;
import jsettlers.graphics.action.Action;
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
	private static final int DETAIL_IMAGES = 3;

	private LinkedList<ChildLink> children =
	        new LinkedList<UIPanel.ChildLink>();
	private IntRectangle position = new IntRectangle(0, 0, 1, 1);

	private ImageLink background;

	public UIPanel() {
	}

	/**
	 * Sets the background. file=-1 means no background
	 * 
	 * @param file
	 * @param settlerSeqIndex
	 */
	public void setBackground(ImageLink link) {
		this.background = link;
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
		this.children.add(new ChildLink(child, left, bottom, right, top));
	}

	/**
	 * Adds a child to the center of the panel.
	 * @param child The child to add.
	 * @param width The relative width of the child (0..1).
	 * @param height The relative height of the child (0..1).
	 */
	public void addChildCentered(UIElement child, float width, float height) {
		addChild(child, 0.5f - width / 2, 0.5f - height / 2, 0.5f + width / 2,
		        0.5f + height / 2);
	}

	public void drawAt(GLDrawContext gl) {
		drawBackground(gl);

		if (children.size() > 0) {
			gl.glPushMatrix();
			gl.glTranslatef(position.getMinX(), position.getMinY(), 0);
			for (ChildLink link : children) {
				link.drawAt(gl, position.getWidth(), position.getHeight());
			}
			gl.glPopMatrix();
		}
	}

	protected void drawBackground(GLDrawContext gl) {
		ImageLink link = getBackgroundImage();
		if (link != null) {
			IntRectangle position = getPosition();
			Image image =
			        getDetailedImage(link, position.getWidth(),
			                position.getHeight());
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
	protected void drawAtRect(GLDrawContext gl, Image image, IntRectangle position) {
		int minX = position.getMinX();
		int minY = position.getMinY();
		int maxX = position.getMaxX();
		int maxY = position.getMaxY();
		image.drawImageAtRect(gl, minX, minY, maxX, maxY);
	}


	/**
	 * Gets a detailed GUI image.
	 * <p>
	 * For gui images, assumes that the next two images are mre detailed.
	 * <p>
	 * For settler sequences, assumes the same for the next two sequence
	 * members.
	 */
	protected Image getDetailedImage(ImageLink link, int width, int height) {
		Image image = ImageProvider.getInstance().getImage(link);
		ImageLink currentLink = link;

		for (int i = 1; i < DETAIL_IMAGES
		        && (image.getWidth() < width || image.getHeight() < height); i++) {
			if (currentLink.getType() == EImageLinkType.SETTLER) {
				image =
				        ImageProvider
				                .getInstance()
				                .getSettlerSequence(link.getFile(),
				                        link.getSequence())
				                .getImageSafe(link.getImage() + i);
			} else {
				image =
				        ImageProvider.getInstance().getGuiImage(link.getFile(),
				                link.getSequence());
			}
		}
		return image;
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

		public ChildLink(UIElement child, float left, float bottom,
		        float right, float top) {
			this.child = child;
			this.left = left;
			this.right = right;
			this.top = top;
			this.bottom = bottom;
		}

		public void drawAt(GLDrawContext gl, int width, int height) {
			child.setPosition(new IntRectangle((int) (left * width),
			        (int) (bottom * height), (int) (right * width),
			        (int) (top * height)));
			child.drawAt(gl);
		}

		public Action getActionRelative(float parentx, float parenty) {
			if (left <= parentx && parentx <= right && bottom <= parenty
			        && parenty <= top) {
				float relativex = (parentx - left) / (right - left);
				float relativey = (parenty - bottom) / (top - bottom);
				return child.getAction(relativex, relativey);
			} else {
				return null;
			}
		}

		public String getDesctiptionRelative(float parentx, float parenty) {
			if (left <= parentx && parentx <= right && bottom <= parenty
			        && parenty <= top) {
				float relativex = (parentx - left) / (right - left);
				float relativey = (parenty - bottom) / (top - bottom);
				return child.getDescription(relativex, relativey);
			} else {
				return null;
			}
		}
	}

	@Override
	public void setPosition(IntRectangle position) {
		this.position = position;
	}

	public IntRectangle getPosition() {
		return position;
	}

	public void removeAll() {
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
			String description =
			        link.getDesctiptionRelative(relativex, relativey);
			if (description != null) {
				return description;
			}
		}
		return null;
	}
}
