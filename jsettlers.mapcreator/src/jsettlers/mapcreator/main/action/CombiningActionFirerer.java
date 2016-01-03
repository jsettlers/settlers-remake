/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
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
						ShortPoint2D start = ((DrawLineAction) action).getStart();
						ShortPoint2D end = ((DrawLineAction) action).getEnd();
						double uidy = ((DrawLineAction) action).getUidy();
						while (toFire.peek() instanceof DrawLineAction) {
							DrawLineAction next = (DrawLineAction) toFire.poll();
							end = next.getEnd();
							uidy += next.getUidy();
						}
						action = new DrawLineAction(start, end, uidy);
					}
					fireTo.fireAction(action);
				} catch (Throwable e) {
					jsettlers.exceptionhandler.ExceptionHandler.displayError(e, "Exception while handling action");
				}
			}
		}

	}

	@Override
	public void fireAction(Action action) {
		toFire.offer(action);
	}

}
