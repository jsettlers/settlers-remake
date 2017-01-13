package jsettlers.main.android.ui.fragments.game.menus.selection;

import jsettlers.common.menu.action.EActionType;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.main.android.controls.ActionControls;
import jsettlers.main.android.controls.ControlsResolver;
import jsettlers.main.android.controls.SelectionControls;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by tompr on 10/01/2017.
 */

public abstract class SelectionFragment extends Fragment {
    private ActionControls actionControls;
    private SelectionControls selectionControls;

    private ISelectionSet selection;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        actionControls = ControlsResolver.getActionControls(getActivity());
        selectionControls = ControlsResolver.getSelectionControls(getActivity());

        selection = selectionControls.getCurrentSelection();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // If the selection hasn't changed when the selection menu is dismissed by the user then the user has started using some other menu and we should deselect. Check isRemoving to confirm its not just a rotation.
        // If the selection has changed then we don't want to overwrite it.
        if (selection == selectionControls.getCurrentSelection() && isRemoving()) {
            actionControls.fireAction(new Action(EActionType.DESELECT));
        }
    }

    public ISelectionSet getSelection() {
        return selection;
    }
}
