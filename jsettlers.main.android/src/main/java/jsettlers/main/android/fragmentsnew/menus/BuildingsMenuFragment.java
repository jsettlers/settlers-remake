package jsettlers.main.android.fragmentsnew.menus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private List<EBuildingType> buildingTypes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildingTypes = Arrays.asList(BuildingBuildContent.normalBuildings);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_buildings, container, false);
    }


    /**
     * Adapter
     */
    private class BuildingsAdapter extends RecyclerView.Adapter<BuildingViewHolder> {

        @Override
        public BuildingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(BuildingViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    private class BuildingViewHolder extends RecyclerView.ViewHolder {

        public BuildingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
