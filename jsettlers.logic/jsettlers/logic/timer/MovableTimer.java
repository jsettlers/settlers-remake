package jsettlers.logic.timer;

import jsettlers.logic.constants.Constants;
import synchronic.timer.IParallelTimerable;
import synchronic.timer.ParallelableTimer;

public class MovableTimer extends ParallelableTimer<ITimerable> {
	private static MovableTimer uniIns;

	protected MovableTimer() {
		super(Constants.MOVABLE_DELAY);
	}

	public static MovableTimer get() {
		if (uniIns == null) {
			uniIns = new MovableTimer();
		}
		return uniIns;
	}

	public static void stop() {
		if (uniIns != null) {
			uniIns.cancel();
			uniIns = null;
		}
	}

	public static void add(ITimerable timerable) {
		get().schedule(timerable);
	}

	public static void remove(ITimerable timerable) {
		get().unSchedule(timerable);
	}

	public static void add(IParallelTimerable parallelable) {
		get().schedule(parallelable);
	}

	public static void remove(IParallelTimerable parallelable) {
		get().unSchedule(parallelable);
	}

	@Override
	protected void handleException(Throwable e, ITimerable curr) {
		super.handleException(e, curr);
		curr.kill();
	}
}
