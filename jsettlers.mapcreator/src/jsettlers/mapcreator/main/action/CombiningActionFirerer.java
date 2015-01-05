package jsettlers.mapcreator.main.action;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;

public class CombiningActionFirerer implements ActionFireable {
	private final Thread thread;

	private BlockingQueue<Action> toFire = new LinkedBlockingQueue<Action>();

	private final ActionFireable fireTo;

	public CombiningActionFirerer(ActionFireable fireTo) {
		this.fireTo = fireTo;
		this.thread = new ActionFirererThread();
		this.thread.setDaemon(true);
		this.thread.start();
	}

	private class ActionFirererThread extends Thread {
		public ActionFirererThread() {
			super("action firerer");
		}

		@Override
		public void run() {
			while (true) {
				Action action;
				try {
					action = toFire.take();
					if (action instanceof DrawLineAction
							&& toFire.peek() instanceof DrawLineAction) {
						ShortPoint2D start =
								((DrawLineAction) action).getStart();
						ShortPoint2D end = ((DrawLineAction) action).getEnd();
						double uidy = ((DrawLineAction) action).getUidy();
						while (toFire.peek() instanceof DrawLineAction) {
							DrawLineAction next =
									(DrawLineAction) toFire.poll();
							end = next.getEnd();
							uidy += next.getUidy();
						}
						action = new DrawLineAction(start, end, uidy);
					}
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
