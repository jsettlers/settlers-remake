package jsettlers.graphics.androidui.actions;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.MoveToAction;
import jsettlers.graphics.action.SelectAction;
import jsettlers.graphics.localization.Labels;

public class MoveToOnClick extends ContextAction {

	@Override
	public String getDesciption() {
		return Labels.getString("click_to_move");
	}

	@Override
	public Action replaceAction(Action action) {
		if (action.getActionType() == EActionType.SELECT_POINT) {
			return new MoveToAction(((SelectAction) action).getPosition());
		}
		return super.replaceAction(action);
	}

}
