package jsettlers.graphics.map.controls.mobile;

/**
 * This class manages fading.
 * <p>
 * You can just make something fade to a given value, or you can force it to
 * values (e.g. for manual scrolling).
 * 
 * @author michael
 */
public class AnimatedFader {
	private static final int ANIMATION_TIME = 500;
	private final float min;
	private final float max;
	/**
	 * The value where animation started.
	 */
	private float startValue;
	private float desired;
	private long startTime;

	public AnimatedFader(float min, float max) {
		this.min = min;
		this.max = max;
		this.startValue = min;
	}

	/**
	 * Sets the fader to a given value. Does not change it.
	 * 
	 * @param value
	 *            The value.
	 */
	public void setTo(float value) {
		fadeTo(value);
		this.startValue = this.desired;
	}

	public void fadeTo(float value) {
		this.startValue = this.desired;
		if (value < min) {
			this.desired = min;
		} else if (value > max) {
			this.desired = max;
		} else {
			this.desired = value;
		}
		this.startTime = System.currentTimeMillis();
	}

	public float getValue() {
		long progress = System.currentTimeMillis() - startTime;
		if (progress > 500) {
			return desired;
		} else {
			float a =
			        (float) (Math.cos((double) progress / ANIMATION_TIME
			                * Math.PI) / 2 + .5);
			return startValue * a + desired * (1 - a);
		}
	}
}
