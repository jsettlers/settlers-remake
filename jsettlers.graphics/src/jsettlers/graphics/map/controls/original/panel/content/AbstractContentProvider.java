package jsettlers.graphics.map.controls.original.panel.content;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.ui.UIPanel;

public abstract class AbstractContentProvider {

	public abstract UIPanel getPanel();

	public ESecondaryTabType getTabs() {
		return ESecondaryTabType.NONE;
	}

	public boolean isForSelection() {
		return false;
	}

	public void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid) {
	}

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

	public void contentHiding(ActionFireable actionFireable, AbstractContentProvider nextContent) {
	}

	public void contentShowing(ActionFireable actionFireable) {
	}

}
