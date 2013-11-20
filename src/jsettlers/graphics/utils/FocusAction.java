package jsettlers.graphics.utils;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;

public class FocusAction extends Action {

	private final UIInput input;

	public FocusAction(UIInput input) {
		super(EActionType.FOCUS);
		this.input = input;
    }
	
	public UIInput getInput() {
	    return input;
    }

}
