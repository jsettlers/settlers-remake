package jsettlers.main.android.gameplay.ui.fragments.menus.goods;

import jsettlers.main.android.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
		View view = inflater.inflate(R.layout.menu_goods_quantities, container, false);
		return view;
	}
}
