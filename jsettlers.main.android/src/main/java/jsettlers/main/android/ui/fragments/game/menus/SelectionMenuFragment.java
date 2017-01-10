package jsettlers.main.android.ui.fragments.game.menus;

import jsettlers.common.menu.action.IAction;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.main.android.controls.ActionListener;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.providers.ControlsProvider;
import jsettlers.main.android.providers.SelectionProvider;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by tompr on 10/01/2017.
 */

public abstract class SelectionMenuFragment extends Fragment implements ActionListener {
    private ControlsAdapter controlsAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ControlsProvider controlsProvider = (ControlsProvider) getActivity();
        controlsAdapter = controlsProvider.getControls();
        controlsAdapter.addActionListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        controlsAdapter.removeActionListener(this);
    }

    @Override
    public void actionFired(IAction action) {
    }

    protected ISelectionSet getCurrentSelection() {
        SelectionProvider selectionProvider = (SelectionProvider) getParentFragment();
        return selectionProvider.getCurrentSelection();
    }
}
