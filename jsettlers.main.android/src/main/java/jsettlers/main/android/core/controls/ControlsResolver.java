package jsettlers.main.android.core.controls;

import android.app.Activity;

import jsettlers.graphics.map.MapContent;
import jsettlers.main.android.core.GameManager;

/**
 * Created by tompr on 13/01/2017.
 */

public class ControlsResolver {
    private final ControlsAdapter controlsAdapter;

    public ControlsResolver(Activity activity) {
        this.controlsAdapter = ((GameManager)activity.getApplication()).getControlsAdapter();
    }

    public ActionControls getActionControls() {
        return controlsAdapter;
    }

    public DrawControls getDrawControls() {
        return controlsAdapter;
    }

    public SelectionControls getSelectionControls() {
        return controlsAdapter;
    }

    public TaskControls getTaskControls() {
        return controlsAdapter;
    }

    public MapContent getMapContent() {
        return controlsAdapter.getMapContent();
    }

    public GameMenu getGameMenu() {
        return controlsAdapter.getGameMenu();
    }
}
