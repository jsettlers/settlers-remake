package jsettlers.graphics.map.controls.original.panel.content;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PointAction;

public abstract class AbstractContentProvider implements IContentProvider {

	@Override
	public void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid) {
	}

	@Override
	public ESecondaryTabType getTabs() {
		return ESecondaryTabType.NONE;
	}

	@Override
	public Action catchAction(Action action) {
		PointAction overrideAction;
		if (action.getActionType() == EActionType.SELECT_POINT
				&& (overrideAction = getSelectAction(((PointAction) action).getPosition())) != null) {
			return overrideAction;
		} else {
			return action;
		}
	}

	public PointAction getSelectAction(ShortPoint2D position) {
		return null;
	}

	@Override
	public void contentHiding(ActionFireable actionFireable) {
	}

	@Override
	public void contentShowing(ActionFireable actionFireable) {
	}

}
