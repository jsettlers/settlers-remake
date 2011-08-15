package jsettlers.graphics.utils;

import jsettlers.common.images.ImageLink;
import jsettlers.graphics.action.Action;

/**
 * This is a button, consisting of images in the given file.
 * <p>
 * The first 3 images after the start index are images for a small, medium and
 * big button.
 * 
 * @author michael
 */
public class Button extends UIPanel implements UIButton {
	/**
	 * How many detail steps there are usually
	 */
	private boolean active = false;
	private final Action action;
	private final String description;
	private final ImageLink image;
	private final ImageLink activeImage;

	public Button(Action action, ImageLink image, ImageLink active,
	        String description) {
		this.action = action;
		this.image = image;
		activeImage = active;
		this.description = description;
	}

	/*@Override
	public void drawAt(GL2 gl) {
		ImageLink start = active ? activeImage : image;
		Image image = null;
		// for (int i = 0; i < DETAIL_IMAGES; i++) {
		image = provider.getImage(start);
		// if (image.getWidth() >= position.getWidth()
		// && image.getHeight() >= position.getHeight()) {
		// break;
		// }
		// }

		image.drawAt(gl, position.getMinX(), position.getMaxY());

		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex2i(position.getMinX(), position.getMinY());
		gl.glVertex2i(position.getMinX(), position.getMaxY());
		gl.glVertex2i(position.getMaxX(), position.getMaxY());
		gl.glVertex2i(position.getMaxX(), position.getMinY());
		gl.glEnd();
	}*/
	
	@Override
	protected ImageLink getBackgroundImage() {
	    return active ? activeImage : image;
	}


	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public Action getAction() {
		return action;
	}

	@Override
	public Action getAction(float relativex, float relativey) {
		return action;
	}

	@Override
	public String getDescription(float relativex, float relativey) {
	    return description;
    }

}
