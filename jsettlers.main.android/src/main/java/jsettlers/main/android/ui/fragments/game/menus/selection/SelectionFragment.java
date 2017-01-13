package jsettlers.main.android.ui.fragments.game.menus.selection;

import jsettlers.common.menu.action.EActionType;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.providers.ControlsProvider;
import jsettlers.main.android.providers.SelectionProvider;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by tompr on 10/01/2017.
 */

public abstract class SelectionFragment extends Fragment {

    private SelectionProvider selectionProvider;
    private ISelectionSet selectionSet;
    private ControlsAdapter controlsAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        selectionProvider = (SelectionProvider) getParentFragment();
        selectionSet = selectionProvider.getCurrentSelection();

        ControlsProvider controlsProvider = (ControlsProvider) getActivity();
        controlsAdapter = controlsProvider.getControls();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // If the selection hasn't changed when the selection menu is dismissed by the user then the user has started using some other menu and we should deselect. Check isRemoving to confirm its not just a rotation.
        // If the selection has changed then we don't want to overwrite it.
        if (selectionSet == selectionProvider.getCurrentSelection() && isRemoving()) {
            getControls().fireAction(new Action(EActionType.DESELECT));
        }
    }

    protected ISelectionSet getSelection() {
        return selectionSet;
    }

    protected ControlsAdapter getControls() {
        return controlsAdapter;
    }
}
