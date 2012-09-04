package jsettlers.graphics.map.controls.mobile;

import jsettlers.graphics.startscreen.ExecutableAction;

public class FadeAnimatedFaderAction extends ExecutableAction {
	private final AnimatedFader fader;
	private final float value;

	public FadeAnimatedFaderAction(AnimatedFader fader, float value) {
		super();
		this.fader = fader;
		this.value = value;
	}
	
	@Override
	public void execute() {
	    fader.fadeTo(value);
	}
}
