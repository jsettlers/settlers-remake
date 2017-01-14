package jsettlers.main.android.controls;

import jsettlers.main.android.providers.ControlsProvider;

import android.app.Activity;

/**
 * Created by tompr on 13/01/2017.
 */

public class ControlsResolver {
    public static ActionControls getActionControls(Activity activity) {
        return getControls(activity);
    }

    public static DrawControls getDrawControls(Activity activity) {
        return getControls(activity);
    }

    public static SelectionControls getSelectionControls(Activity activity) {
        return getControls(activity);
    }

    public static TaskControls getTaskControls(Activity activity) {
        return getControls(activity);
    }

    public static MenuFactory getMenuFactory(Activity activity) {
        return getControls(activity);
    }




    private static ControlsAdapter getControls(Activity activity) {
        return ((ControlsProvider)activity).getControlsAdapter();
    }
}
