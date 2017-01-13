package jsettlers.main.android.controls;

import jsettlers.graphics.action.ActionFireable;

/**
 * Created by tompr on 13/01/2017.
 */

public interface ActionControls extends ActionFireable {
    void addActionListener(ActionListener actionListener);
    void removeActionListener(ActionListener actionListener);
}
