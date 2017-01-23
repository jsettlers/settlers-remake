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

public class GoodsProductionFragment extends Fragment {
    public static GoodsProductionFragment newInstance() {
        return new GoodsProductionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_goods_production, container, false);
        return view;
    }
}
