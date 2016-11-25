package jsettlers.main.android.fragmentsnew.menus;

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

public class GoodsQuantitiesFragment extends Fragment {
    public static GoodsQuantitiesFragment newInstance() {
        return new GoodsQuantitiesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goods_quantities, container, false);
        return view;
    }
}
