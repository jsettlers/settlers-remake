package jsettlers.main.android.ui.fragments.game.menus.settlers;

import jsettlers.main.android.R;
import jsettlers.main.android.controls.ControlsResolver;
import jsettlers.main.android.menus.SettlersSoldiersMenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tompr on 13/01/2017.
 */

public class SettlersSoldiersFragment extends Fragment {
    private SettlersSoldiersMenu settlersSoldiersMenu;

    public static SettlersSoldiersFragment newInstance() {
        return new SettlersSoldiersFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_settlers_soldiers, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settlersSoldiersMenu = ControlsResolver.getMenuFactory(getActivity()).getSettlersSoldiersMenu();
    }
}
