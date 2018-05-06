/*
 * Copyright (c) 2018
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
 */
package jsettlers.common.action;

/**
 * This is a action the user has requested.
 * <p>
 * Each Action has an active status, that indicates that it is currently executed. When the execution of the action is begun, the flag should be set
 * so that the user interface enters a blocking mode, and goes back to normal mode when the action is finished. It is not guaranteed that there is no
 * other action being sent during that time, e.g. an cancel-action.
 * <p>
 * Actions may be reused and fired multiple times by the interface, but they are always inactive when being fired.
 * 
 * @author michael
 */
public class Action implements IAction {
	private final EActionType actionType;
	private boolean active = false;

	/**
	 * Creates a new generic action.
	 * 
	 * @param actionType
	 *            The type the action should have.
	 */
	public Action(EActionType actionType) {
		this.actionType = actionType;
	}

	/**
	 * Sets the active flag of this action.
	 * 
	 * @param active
	 *            The flag that indicates if this action is active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Gets the type of the action.
	 * 
	 * @return The type.
	 */
	@Override
	public EActionType getActionType() {
		return this.actionType;
	}

	/**
	 * Returns whether this action is active.
	 * 
	 * @return true if and only if the active flag is set.
	 */
	public boolean isActive() {
		return this.active;
	}
}
