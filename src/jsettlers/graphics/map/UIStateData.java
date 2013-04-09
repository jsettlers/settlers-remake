package jsettlers.graphics.map;

import java.io.Serializable;

import jsettlers.common.position.ShortPoint2D;

/**
 * This class contains the data needed for the GUI to restore it's state.
 * 
 * @author michael
 */
public class UIStateData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4163727213374601975L;
	private final float screenCenterX;
	private final float screenCenterY;
	private final float zoom;
	private ShortPoint2D startPoint = null;

	public UIStateData(float screenCenterX, float screenCenterY, float zoom) {
		super();
		this.screenCenterX = screenCenterX;
		this.screenCenterY = screenCenterY;
		this.zoom = zoom;
	}

	public UIStateData(ShortPoint2D startPoint) {
		this(0, 0, 0);
		this.startPoint = startPoint;
	}

	public float getScreenCenterX() {
		return screenCenterX;
	}

	public float getScreenCenterY() {
		return screenCenterY;
	}

	public float getZoom() {
		return zoom;
	}

	/**
	 * Gets the start point. THis point overrides screen center settings.
	 * 
	 * @return
	 */
	public ShortPoint2D getStartPoint() {
		return startPoint;
	}
}
