package jsettlers.graphics.androidui.actions;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.localization.Labels;

public class MoveToOnClick extends ContextAction {

	@Override
	public String getDesciption() {
		return Labels.getString("click_to_move");
	}

	@Override
	public Action replaceAction(Action action) {
		if (action.getActionType() == EActionType.SELECT_POINT) {
			return new PointAction(EActionType.MOVE_TO, ((PointAction) action).getPosition());
		}
		return super.replaceAction(action);
	}

}
