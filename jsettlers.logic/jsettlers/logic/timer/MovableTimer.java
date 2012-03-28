package jsettlers.logic.timer;

import jsettlers.logic.constants.Constants;

public class MovableTimer extends AbstractTimer<ITimerable> {

	protected MovableTimer() {
		super(Constants.MOVABLE_INTERRUPT_DELAY);
	}

	private static MovableTimer uniIns;

	public static void stop() {
		if (uniIns != null) {
			uniIns.cancel();
			uniIns = null;
		}
	}

	public static void add(ITimerable t) {
		get().add_(t);
	}

	public static void remove(ITimerable t) {
		get().remove_(t);
	}

	private static synchronized AbstractTimer<ITimerable> get() {
		if (uniIns == null) {
			uniIns = new MovableTimer();
		}
		return uniIns;
	}

}
