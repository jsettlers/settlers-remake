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

import jsettlers.common.action.IAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.ActionFireable;

/**
 * Get actions, and combine multiple actions together
 * 
 * @author Andreas Butti
 */
public class CombiningActionFirerer implements ActionFireable {

	/**
	 * Working thread
	 */
	private final Thread thread;

	/**
	 * Queue
	 */
	private final BlockingQueue<IAction> toFire = new LinkedBlockingQueue<>();

	/**
	 * Target
	 */
	private final ActionFireable fireTo;

	/**
	 * Constructor
	 * 
	 * @param fireTo
	 *            Target
	 */
	public CombiningActionFirerer(ActionFireable fireTo) {
		this.fireTo = fireTo;
		thread = new Thread(this::forwardActions);
		thread.setName("action firerer");
		thread.setDaemon(true);
		thread.start();

	}

	/**
	 * Loop to forward actions
	 */
	protected void forwardActions() {
		while (true) {
			try {
				forwardSingleAction();
			} catch (Throwable e) {
				jsettlers.exceptionhandler.ExceptionHandler.displayError(e, "Exception while handling action");
			}
		}
	}

	/**
	 * Forward a single action
	 * 
	 * @throws InterruptedException
	 */
	protected void forwardSingleAction() throws InterruptedException {
		IAction action = toFire.take();
		if (action instanceof DrawLineAction && toFire.peek() instanceof DrawLineAction) {
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
	}

	@Override
	public void fireAction(IAction action) {
		toFire.offer(action);
	}

}
