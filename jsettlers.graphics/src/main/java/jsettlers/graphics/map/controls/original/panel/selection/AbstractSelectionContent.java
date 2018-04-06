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
package jsettlers.graphics.map.controls.original.panel.selection;

import jsettlers.common.action.EActionType;
import jsettlers.common.action.IAction;
import jsettlers.common.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.map.controls.original.panel.content.AbstractContentProvider;

/**
 * This is the base class for all selection content panels.
 * 
 * @author Michael Zangl
 *
 */
public abstract class AbstractSelectionContent extends AbstractContentProvider {

	@Override
	public boolean isForSelection() {
		return true;
	}

	@Override
	public void contentHiding(ActionFireable actionFireable, AbstractContentProvider nextContent) {
		if (!nextContent.isForSelection()) {
			// TODO: Replace with a deselect-all-action
			actionFireable.fireAction(new Action(EActionType.DESELECT));
		}
		super.contentHiding(actionFireable, nextContent);
	}

	@Override
	public IAction catchAction(IAction action) {
		if (action.getActionType() == EActionType.ABORT) {
			return new Action(EActionType.DESELECT);
		}
		return super.catchAction(action);
	}
}
