package jsettlers.main.android.fragmentsnew.menus;

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

public class GoodsMenuFragment extends Fragment {
    public static GoodsMenuFragment newInstance() {
        return new GoodsMenuFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_goods, container, false);
    }
}
