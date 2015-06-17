package jsettlers.graphics.map.controls.original.panel.selection;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.map.controls.original.panel.content.AbstractContentProvider;
import jsettlers.graphics.map.controls.original.panel.content.IContentProvider;

public abstract class AbstractSelectionContent extends AbstractContentProvider {

	@Override
	public boolean isForSelection() {
		return true;
	}

	@Override
	public void contentHiding(ActionFireable actionFireable, IContentProvider nextContent) {
		if (!nextContent.isForSelection()) {
			// TODO: Replace with a deselect-all-action
			actionFireable.fireAction(new Action(EActionType.DESELECT));
		}
		super.contentHiding(actionFireable, nextContent);
	}

	@Override
	public Action catchAction(Action action) {
		if (action.getActionType() == EActionType.ABORT) {
			return new Action(EActionType.DESELECT);
		}
		return super.catchAction(action);
	}
}
