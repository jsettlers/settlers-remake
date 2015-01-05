package jsettlers.graphics.androidui.menu;

import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import android.view.View;
import android.view.View.OnClickListener;

public class ActionClickListener implements OnClickListener {

	private Action action;
	private ActionFireable fireable;
	private Hideable hide;

	public ActionClickListener(ActionFireable fireable, Action action,
			Hideable hide) {
		this.fireable = fireable;
		this.action = action;
		this.hide = hide;
	}

	@Override
	public void onClick(View arg0) {
		fireable.fireAction(action);
		if (hide != null) {
			hide.requestHide();
		}
	}

}
