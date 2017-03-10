package jsettlers.main.android.gameplay.ui.fragments.menus.buildings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.main.android.R;
import jsettlers.main.android.gameplay.presenters.BuildingsCategoryMenu;
import jsettlers.main.android.gameplay.presenters.MenuFactory;
import jsettlers.main.android.gameplay.ui.views.BuildingsCategoryView;

/**
 * Created by tompr on 24/11/2016.
 */

public class BuildingsCategoryFragment extends Fragment implements BuildingsCategoryView {
    private static final String ARG_BUILDINGS_CATEGORY = "arg_buildings_category";

    private BuildingsCategoryMenu buildingsMenu;

    private BuildingsAdapter adapter;
    private RecyclerView recyclerView;

    public static BuildingsCategoryFragment newInstance(int buildingsCategory) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_BUILDINGS_CATEGORY, buildingsCategory);

        BuildingsCategoryFragment fragment = new BuildingsCategoryFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_buildings_category, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int buildingsCategory = getArguments().getInt(ARG_BUILDINGS_CATEGORY);
        buildingsMenu = new MenuFactory(getActivity()).buildingsMenu(this, buildingsCategory);
        buildingsMenu.start();
    }

    /**
     * BuildingsCategoryView implementation
     */
    @Override
    public void setBuildings(List<EBuildingType> buildings) {
        if (adapter == null) {
            adapter = new BuildingsCategoryFragment.BuildingsAdapter(buildings);
        }

        if (recyclerView.getAdapter() == null) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            recyclerView.setAdapter(adapter);
        }
    }


    private void buildingSelected(EBuildingType buildingType) {
        buildingsMenu.buildingSelected(buildingType);
    }

    /**
     * Adapter
     */
    private class BuildingsAdapter extends RecyclerView.Adapter<BuildingsCategoryFragment.BuildingViewHolder> {
        private List<EBuildingType> buildingTypes;

        private LayoutInflater layoutInflater;

        BuildingsAdapter(List<EBuildingType> buildingTypes) {
            this.buildingTypes = buildingTypes;
            layoutInflater = getActivity().getLayoutInflater();
        }

        @Override
        public int getItemCount() {
            return buildingTypes.size();
        }

        @Override
        public BuildingsCategoryFragment.BuildingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = layoutInflater.inflate(R.layout.item_building, parent, false);
            final BuildingsCategoryFragment.BuildingViewHolder buildingViewHolder = new BuildingsCategoryFragment.BuildingViewHolder(itemView);

            itemView.setOnClickListener(view -> {
                int position = buildingViewHolder.getLayoutPosition();
                EBuildingType buildingType = buildingTypes.get(position);
                buildingSelected(buildingType);
            });

            return buildingViewHolder;
        }

        @Override
        public void onBindViewHolder(BuildingsCategoryFragment.BuildingViewHolder holder, int position) {
            EBuildingType buildingType = buildingTypes.get(position);
            holder.setBuildingType(buildingType);
        }
    }

    private class BuildingViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        BuildingViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }

        void setBuildingType(EBuildingType buildingType) {
            OriginalImageProvider.get(buildingType).setAsImage(imageView);
        }
    }
}
