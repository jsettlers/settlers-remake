package jsettlers.graphics.action;

import jsettlers.graphics.map.controls.original.panel.content.EContentType;

public class ChangePanelAction extends Action {

	private final EContentType content;

	public ChangePanelAction(EContentType content) {
		super(EActionType.CHANGE_PANEL);
		this.content = content;
	}

	public EContentType getContent() {
		return content;
	}

}
