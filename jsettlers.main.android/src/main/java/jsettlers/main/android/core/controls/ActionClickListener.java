package jsettlers.main.android.core.controls;

import android.view.View;

import jsettlers.common.menu.action.EActionType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;

/**
 * Created by tompr on 13/01/2017.
 */

public class ActionClickListener implements View.OnClickListener {
    private final ActionFireable actionFireable;
    private final Action action;

    public ActionClickListener(ActionFireable actionFireable, EActionType actionType) {
        this(actionFireable, new Action(actionType));
    }

    public ActionClickListener(ActionFireable actionFireable, Action action) {
        this.actionFireable = actionFireable;
        this.action = action;
    }

    @Override
    public void onClick(View view) {
        actionFireable.fireAction(action);
    }
}
