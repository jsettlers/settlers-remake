package jsettlers.graphics.action;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class lets you schedule the firing of actions in a separate thread.
 * 
 * @author michael
 */
public class ActionFirerer implements ActionFireable {
	private final Thread thread;

	private BlockingQueue<Action> toFire = new LinkedBlockingQueue<Action>();

	private final ActionFireable fireTo;

	public ActionFirerer(ActionFireable fireTo) {
		this.fireTo = fireTo;
		this.thread = new ActionFirererThread();
		this.thread.setDaemon(true);
		this.thread.start();
	}

	private class ActionFirererThread extends Thread {
		@Override
		public void run() {
			while (true) {
				Action action;
				try {
					action = toFire.take();
					fireTo.fireAction(action);
				} catch (Throwable e) {
					System.err.println("Exception while habdling action:");
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void fireAction(Action action) {
		toFire.offer(action);
	}
}
