package jsettlers.graphics.androidui.actions;

import jsettlers.graphics.action.Action;

/**
 * This is something the user is currenlty doing
 * 
 * @author michael
 */
public abstract class ContextAction {
	public abstract String getDesciption();
	
	public Action replaceAction(Action action) {
		return action;
	}
}
