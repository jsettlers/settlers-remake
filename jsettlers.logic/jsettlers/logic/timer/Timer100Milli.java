package jsettlers.logic.timer;

public class Timer100Milli extends AbstractTimer<ITimerable> {

	protected Timer100Milli() {
		super((short) 100);
	}

	private static Timer100Milli uniIns;

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

	private static AbstractTimer<ITimerable> get() {
		if (uniIns == null) {
			uniIns = new Timer100Milli();
		}
		return uniIns;
	}

}
