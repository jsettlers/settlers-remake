package jsettlers.logic.timer;

public class Timer100Milli extends SuperTimer<ITimerable> {

	protected Timer100Milli() {
		super((short) 100);
	}

	private static Timer100Milli uniIns;

	public static void start() {
		if (uniIns == null) {
			uniIns = new Timer100Milli();
		}
	}

	public static void stop() {
		if (uniIns != null) {
			uniIns.cancel();
			uniIns = null;
		}
	}

	public static void add(ITimerable t) {
		uniIns.add_(t);
	}

	public static void remove(ITimerable t) {
		uniIns.remove_(t);
	}

	@Override
	protected void executeAction() {
		for (ITimerable s : timerables) {
			try {
				s.timerEvent();
			} catch (Throwable t) {
				System.out.println("Timer100Milli catched: ");
				t.printStackTrace();
				s.kill();
			}
		}
	}

}
