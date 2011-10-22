package jsettlers.graphics.image;

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
public class SettlerImage extends Image {

	private Image torso = null;

	/**
	 * {@inheritDoc:Image#Image(ImageDataPrivider)}
	 * @param data The data to use.
	 */
	public SettlerImage(ImageDataPrivider data) {
		super(data);
	}
	
	@Override
	public void draw(GLDrawContext gl, Color color) {
	    super.draw(gl, null);
	    if (this.torso != null) {
	    	this.torso.draw(gl, color);
	    }
	}

	/**
	 * Sets the image overlay.
	 * @param torso The torso. May be null.
	 */
	public void setTorso(Image torso) {
		this.torso = torso;
	}

	/**
	 * Gets the torso for this image.
	 * @return The torso.
	 */
	public Image getTorso() {
		return this.torso;
	}

}
