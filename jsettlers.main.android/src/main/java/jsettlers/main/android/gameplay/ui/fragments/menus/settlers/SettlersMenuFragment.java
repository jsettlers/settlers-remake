package jsettlers.main.android.gameplay.ui.fragments.menus.settlers;

import jsettlers.main.android.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import biz.laenger.android.vpbs.BottomSheetUtils;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by tompr on 22/11/2016.
 */

public class SettlersMenuFragment extends Fragment {
	private ViewPager viewPager;

	public static SettlersMenuFragment newInstance() {
		return new SettlersMenuFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_view_pager, container, false);

		viewPager = (ViewPager) view.findViewById(R.id.view_pager);
		BottomSheetUtils.setupViewPager(viewPager);

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		viewPager.setAdapter(new SettlersPagerAdapter(getChildFragmentManager()));

		CircleIndicator indicator = (CircleIndicator) getView().findViewById(R.id.circle_indicator);
		indicator.setViewPager(viewPager);
	}

	/**
	 * Adapter
	 */
	private class SettlersPagerAdapter extends FragmentPagerAdapter {
		private SettlersPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return SettlersSoldiersFragment.newInstance();
			default:
				throw new RuntimeException("PagerAdapter count doesn't match available number of Goods menu fragments");
			}
		}
	}
}
