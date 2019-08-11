/*
 * Copyright (c) 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package jsettlers.main.android.gameplay.controlsmenu.settlers;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import biz.laenger.android.vpbs.BottomSheetUtils;
import jsettlers.main.android.R;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by tompr on 22/11/2016.
 */
@EFragment(R.layout.menu_view_pager)
public class SettlersMenuFragment extends Fragment {
	public static SettlersMenuFragment newInstance() {
		return new SettlersMenuFragment_();
	}

	@ViewById(R.id.view_pager)
	ViewPager viewPager;

	@ViewById(R.id.circle_indicator)
	CircleIndicator circleIndicator;

	@AfterViews
	void setupBottomSheet() {
		BottomSheetUtils.setupViewPager(viewPager);
		viewPager.setAdapter(new SettlersPagerAdapter(getChildFragmentManager()));
		circleIndicator.setViewPager(viewPager);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
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
