package jsettlers.graphics.androidui.actions;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.SelectAction;
import jsettlers.graphics.localization.Labels;

public class SelectWorkareaAction extends ContextAction {
	@Override
	public String getDesciption() {
		return Labels.getString("click_set_workcenter");
	}

	@Override
	public Action replaceAction(Action action) {
		if (action.getActionType() == EActionType.SELECT_POINT) {
			return new SelectAction(((SelectAction) action).getPosition(),
			        EActionType.SET_WORK_AREA);
		}
		return super.replaceAction(action);
	}
}
