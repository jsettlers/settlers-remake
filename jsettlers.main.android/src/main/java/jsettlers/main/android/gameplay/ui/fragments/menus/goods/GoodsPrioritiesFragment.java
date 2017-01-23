package jsettlers.main.android.gameplay.ui.fragments.menus.goods;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jsettlers.main.android.R;

/**
 * Created by tompr on 24/11/2016.
 */

public class GoodsPrioritiesFragment extends Fragment {
    public static GoodsPrioritiesFragment newInstance() {
        return new GoodsPrioritiesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_goods_priorities, container, false);
        return view;
    }
}
