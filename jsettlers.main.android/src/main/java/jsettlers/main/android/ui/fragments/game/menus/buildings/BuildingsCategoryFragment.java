package jsettlers.main.android.ui.fragments.game.menus.buildings;

import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.main.android.R;
import jsettlers.main.android.menus.BuildingsMenu;
import jsettlers.main.android.providers.ControlsProvider;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by tompr on 24/11/2016.
 */

public class BuildingsCategoryFragment extends Fragment {
    private static final String ARG_BUILDINGS_CATEGORY = "arg_buildings_category";

    private BuildingsMenu buildingsMenu;

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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buildingsMenu = ((ControlsProvider)getActivity()).getControls().getBuildingsMenu();

        int buildingsCategory = getArguments().getInt(ARG_BUILDINGS_CATEGORY);
        List<EBuildingType> buildingTypes = buildingsMenu.getBuildingTypesForCategory(buildingsCategory);

        recyclerView.setAdapter(new BuildingsCategoryFragment.BuildingsAdapter(buildingTypes));
    }

    private void buildingSelected(EBuildingType buildingType) {
        buildingsMenu.showConstructionMarkers(buildingType);
    }


    /**
     * Adapter
     */
    private class BuildingsAdapter extends RecyclerView.Adapter<BuildingsCategoryFragment.BuildingViewHolder> {
        private List<EBuildingType> buildingTypes;

        private LayoutInflater layoutInflater;

        public BuildingsAdapter(List<EBuildingType> buildingTypes) {
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = buildingViewHolder.getLayoutPosition();
                    EBuildingType buildingType = buildingTypes.get(position);
                    buildingSelected(buildingType);
                }
            });

            return buildingViewHolder;
        }

        @Override
        public void onBindViewHolder(BuildingsCategoryFragment.BuildingViewHolder holder, int position) {
            EBuildingType buildingType = buildingTypes.get(position);
            holder.setBuildingType(buildingType);
         //   OriginalImageProvider
        }
    }

    private class BuildingViewHolder extends RecyclerView.ViewHolder {
    //    private final TextView textView;
        private final ImageView imageView;

        public BuildingViewHolder(View itemView) {
            super(itemView);
        //    textView = (TextView) itemView.findViewById(R.id.text_view);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
        }

        public void setBuildingType(EBuildingType buildingType) {
        //    textView.setText(buildingType.name());
            OriginalImageProvider.get(buildingType).setAsImage(imageView);
        }
    }
}
