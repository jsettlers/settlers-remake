package jsettlers.main.android.core.events;

/**
 * Created by Tom Pratt on 30/09/2017.
 */

import android.arch.lifecycle.LiveData;

import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;

public class DrawEvents extends LiveData<Void> implements DrawListener {
	private final DrawControls drawControls;

	public DrawEvents(DrawControls drawControls) {
		this.drawControls = drawControls;
		setValue(null);
	}

	@Override
	protected void onActive() {
		super.onActive();
		drawControls.addInfrequentDrawListener(this);
	}

	@Override
	protected void onInactive() {
		super.onInactive();
		drawControls.removeInfrequentDrawListener(this);
	}

	@Override
	public void draw() {
		postValue(null);
	}
}
