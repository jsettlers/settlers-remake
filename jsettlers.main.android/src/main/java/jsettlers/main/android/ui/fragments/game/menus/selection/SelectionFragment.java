package jsettlers.main.android.ui.fragments.game.menus.selection;

import jsettlers.common.selectable.ISelectionSet;
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

    private ISelectionSet selectionSet;
    private ControlsAdapter controlsAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SelectionProvider selectionProvider = (SelectionProvider) getParentFragment();
        selectionSet = selectionProvider.getCurrentSelection();

        ControlsProvider controlsProvider = (ControlsProvider) getActivity();
        controlsAdapter = controlsProvider.getControls();
    }

    protected ISelectionSet getSelection() {
        return selectionSet;
    }

    protected ControlsAdapter getControls() {
        return controlsAdapter;
    }
}
