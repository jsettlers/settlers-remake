package jsettlers.graphics.map.controls.original.panel.selection;

import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.map.controls.original.panel.content.AbstractContentProvider;

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
