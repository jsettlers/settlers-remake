package jsettlers.main.android.gameplay.ui.fragments.menus.buildings;

import static jsettlers.main.android.gameplay.presenters.BuildingsMenu.BUILDINGS_CATEGORY_FOOD;
import static jsettlers.main.android.gameplay.presenters.BuildingsMenu.BUILDINGS_CATEGORY_MILITARY;
import static jsettlers.main.android.gameplay.presenters.BuildingsMenu.BUILDINGS_CATEGORY_NORMAL;
import static jsettlers.main.android.gameplay.presenters.BuildingsMenu.BUILDINGS_CATEGORY_SOCIAL;

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

public class BuildingsMenuFragment extends Fragment {
    public static BuildingsMenuFragment newInstance() {
        return new BuildingsMenuFragment();
    }

    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_buildings, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        BottomSheetUtils.setupViewPager(viewPager);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewPager.setAdapter(new BuildingsPagerAdapter(getChildFragmentManager()));

        CircleIndicator indicator = (CircleIndicator) getView().findViewById(R.id.circle_indicator);
        indicator.setViewPager(viewPager);
    }


    /**
     * Adapter
     */
    private class BuildingsPagerAdapter extends FragmentPagerAdapter {

        private int[] buildingsCategories = { BUILDINGS_CATEGORY_NORMAL, BUILDINGS_CATEGORY_FOOD, BUILDINGS_CATEGORY_MILITARY, BUILDINGS_CATEGORY_SOCIAL };

        public BuildingsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return buildingsCategories.length;
        }

        @Override
        public Fragment getItem(int position) {
            int buildingsCategory = buildingsCategories[position];
            return BuildingsCategoryFragment.newInstance(buildingsCategory);
        }
    }
}
