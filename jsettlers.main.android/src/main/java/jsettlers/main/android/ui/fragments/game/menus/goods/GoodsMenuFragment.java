package jsettlers.main.android.ui.fragments.game.menus.goods;

import biz.laenger.android.vpbs.BottomSheetUtils;
import jsettlers.main.android.R;
import me.relex.circleindicator.CircleIndicator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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

    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_goods, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        BottomSheetUtils.setupViewPager(viewPager);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        BuildingsMenuProvider buildingsMenuProvider = (BuildingsMenuProvider)getParentFragment();
//        buildingsMenu = buildingsMenuProvider.getBuildingsMenu();

        viewPager.setAdapter(new GoodsMenuFragment.GoodsPagerAdapter(getChildFragmentManager()));

        CircleIndicator indicator = (CircleIndicator) getView().findViewById(R.id.circle_indicator);
        indicator.setViewPager(viewPager);
    }


    /**
     * Adapter
     */
    private class GoodsPagerAdapter extends FragmentPagerAdapter {

        public GoodsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return GoodsQuantitiesFragment.newInstance();
                case 1:
                    return GoodsProductionFragment.newInstance();
                case 2:
                    return GoodsDistributionFragment.newInstance();
                case 3:
                    return GoodsPrioritiesFragment.newInstance();
                default:
                    throw new RuntimeException("PagerAdapter count doesn't match available number of Goods menu fragments");
            }
        }
    }
}
