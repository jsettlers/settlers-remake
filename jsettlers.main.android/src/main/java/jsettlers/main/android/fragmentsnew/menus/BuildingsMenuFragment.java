package jsettlers.main.android.fragmentsnew.menus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.map.controls.original.panel.content.BuildingBuildContent;
import jsettlers.main.android.R;

/**
 * Created by tompr on 22/11/2016.
 */

public class BuildingsMenuFragment extends Fragment {
    public static BuildingsMenuFragment newInstance() {
        return new BuildingsMenuFragment();
    }

    private BuildingsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_buildings, container, false);

        List<EBuildingType> buildingTypes = Arrays.asList(BuildingBuildContent.normalBuildings);
        adapter = new BuildingsAdapter(buildingTypes);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }


    /**
     * Adapter
     */
    private class BuildingsAdapter extends RecyclerView.Adapter<BuildingViewHolder> {
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
        public BuildingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_building, parent, false);
            BuildingViewHolder buildingViewHolder = new BuildingViewHolder(itemView);
            return buildingViewHolder;
        }

        @Override
        public void onBindViewHolder(BuildingViewHolder holder, int position) {
            EBuildingType buildingType = buildingTypes.get(position);
            holder.setBuildingName(buildingType.name());
        }
    }

    private class BuildingViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public BuildingViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text_view);
        }

        public void setBuildingName(String name) {
            textView.setText(name);
        }
    }
}
