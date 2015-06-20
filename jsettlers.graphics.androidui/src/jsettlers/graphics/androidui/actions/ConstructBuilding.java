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
package jsettlers.graphics.androidui.actions;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.BuildAction;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.action.ShowConstructionMarksAction;
import jsettlers.graphics.localization.Labels;

public class ConstructBuilding extends ContextAction {
	private EBuildingType type;

	public ConstructBuilding(EBuildingType type) {
		this.type = type;
	}

	@Override
	public String getDesciption() {
		return String.format(Labels.getString("select_to_build"), Labels.getName(type));
	}

	@Override
	public Action replaceAction(Action action) {
		if (action.getActionType() == EActionType.SELECT_POINT) {
			return new BuildAction(type, ((PointAction) action).getPosition());
		} else {
			return action;
		}
	}

	@Override
	public void onDeactivate(ActionFireable actionFireable) {
		actionFireable.fireAction(new ShowConstructionMarksAction(null));
		super.onDeactivate(actionFireable);
	}

}
