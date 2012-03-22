package jsettlers.logic.timer;

public class PartitionManagerTimer extends AbstractTimer<ITimerable> {

	protected PartitionManagerTimer() {
		super((short) 20);
	}

	private static PartitionManagerTimer uniIns;

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
			uniIns = new PartitionManagerTimer();
		}
		return uniIns;
	}

}
