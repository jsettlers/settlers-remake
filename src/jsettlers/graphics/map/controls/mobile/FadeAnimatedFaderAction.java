package jsettlers.graphics.map.controls.mobile;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;

public class FadeAnimatedFaderAction extends Action implements Runnable {
	private final AnimatedFader fader;
	private final float value;

	public FadeAnimatedFaderAction(AnimatedFader fader, float value) {
		super(EActionType.GUI_RUNNABLE);
		this.fader = fader;
		this.value = value;
	}
	
	@Override
	public void run() {
	    fader.fadeTo(value);
	}
}
