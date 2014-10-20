package jsettlers.graphics.map.controls.original.panel.content;

public class AnimateablePosition {
	private static final long ANIMATION_TIME = 300;
	private float startx, starty;
	private float destx, desty;
	private long animationstart = 0;

	public AnimateablePosition(float startx, float starty) {
		this.startx = startx;
		this.starty = starty;
		this.destx = startx;
		this.desty = starty;
	}

	public float getX() {
		float p = getProgress();
		return (1 - p) * startx + p * destx;
	}

	private float getProgress() {
		if (animationstart == 0) {
			return 1; // faster
		}

		long timediff = System.currentTimeMillis() - animationstart;
		if (timediff >= ANIMATION_TIME) {
			animationstart = 0;
			return 1;
		}
		return (float) timediff / ANIMATION_TIME;
	}

	public float getY() {
		float p = getProgress();
		return (1 - p) * starty + p * desty;
	}
	
	public void setPosition(float x, float y) {
		this.startx = getX();
		this.starty = getY();
		this.animationstart = System.currentTimeMillis();
		this.destx = x;
		this.desty = y;
	}
}
