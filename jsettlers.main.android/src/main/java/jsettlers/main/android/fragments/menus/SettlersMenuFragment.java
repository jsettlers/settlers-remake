package jsettlers.main.android.fragments.menus;

import jsettlers.main.android.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tompr on 22/11/2016.
 */

public class SettlersMenuFragment extends Fragment {
    public static SettlersMenuFragment newInstance() {
        return new SettlersMenuFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_settlers, container, false);
    }
}
