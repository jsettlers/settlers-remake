package jsettlers.graphics.image;

import jsettlers.graphics.reader.ImageMetadata;
import go.graphics.Color;
import go.graphics.GLDrawContext;

/**
 * This is the image of something that is displayed as an object on the map,
 * e.g. an settler.
 * <p>
 * It can have a torso, an overlay that is always drawn together with the image.
 * 
 * @author michael
 */
public class SettlerImage extends SingleImage {

	private SingleImage torso = null;

	/**
	 * {@inheritDoc:Image#Image(ImageDataPrivider)}
	 * 
	 * @param data
	 *            The data to use.
	 */
	public SettlerImage(ImageMetadata metadata, short[] data) {
		super(metadata, data);
	}

	@Override
	public void draw(GLDrawContext gl, Color color) {
		if (this.torso != null) {
			super.draw(gl, null);
			this.torso.draw(gl, color);
		} else {
			super.draw(gl, color);
		}
	}

	/**
	 * Sets the image overlay.
	 * 
	 * @param torso
	 *            The torso. May be null.
	 */
	public void setTorso(SingleImage torso) {
		this.torso = torso;
	}

	/**
	 * Gets the torso for this image.
	 * 
	 * @return The torso.
	 */
	public Image getTorso() {
		return this.torso;
	}

	@Override
	protected int getGeometryIndex(GLDrawContext context) {
		int index = super.getGeometryIndex(context);
		if (torso != null && torso.getWidth() == getWidth()
		        && torso.getHeight() == getHeight()
		        && torso.getOffsetX() == getOffsetX()
		        && torso.getOffsetY() == getOffsetY()) {
			torso.setGeometryIndex(index);
		}
		return index;
	}
}
