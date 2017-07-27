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

package jsettlers.main.android.gameplay.ui.fragments.menus.buildings;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import biz.laenger.android.vpbs.BottomSheetUtils;
import jsettlers.graphics.map.controls.original.panel.content.buildings.EBuildingsCategory;
import jsettlers.main.android.R;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by tompr on 22/11/2016.
 */
@EFragment(R.layout.menu_view_pager)
public class BuildingsMenuFragment extends Fragment {
	public static BuildingsMenuFragment newInstance() {
		return new BuildingsMenuFragment_();
	}

	@ViewById(R.id.view_pager)
	ViewPager viewPager;
	@ViewById(R.id.circle_indicator)
	CircleIndicator circleIndicator;

	@AfterViews
	void setupBottomSheet() {
		BottomSheetUtils.setupViewPager(viewPager);
		viewPager.setAdapter(new BuildingsPagerAdapter(getChildFragmentManager()));
		circleIndicator.setViewPager(viewPager);
	}

	/**
	 * Adapter
	 */
	private class BuildingsPagerAdapter extends FragmentPagerAdapter {
		public BuildingsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return EBuildingsCategory.NUMBER_OF_VALUES;
		}

		@Override
		public Fragment getItem(int position) {
			return BuildingsCategoryFragment.newInstance(EBuildingsCategory.VALUES[position]);
		}
	}
}
