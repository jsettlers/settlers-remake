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

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.map.controls.original.panel.content.EBuildingsCategory;
import jsettlers.main.android.R;
import jsettlers.main.android.gameplay.presenters.Building;
import jsettlers.main.android.gameplay.presenters.BuildingsCategoryMenu;
import jsettlers.main.android.gameplay.presenters.MenuFactory;
import jsettlers.main.android.gameplay.ui.views.BuildingsCategoryView;
import jsettlers.main.android.utils.OriginalImageProvider;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by tompr on 24/11/2016.
 */
@EFragment(R.layout.menu_buildings_category)
public class BuildingsCategoryFragment extends Fragment implements BuildingsCategoryView {
	private static final String ARG_BUILDINGS_CATEGORY = "arg_buildings_category";

	public static BuildingsCategoryFragment newInstance(EBuildingsCategory buildingsCategory) {
		return BuildingsCategoryFragment_.builder().buildingsCategory(buildingsCategory).build();
	}

	@ViewById(R.id.recycler_view)
	RecyclerView recyclerView;
	@FragmentArg(ARG_BUILDINGS_CATEGORY)
	EBuildingsCategory buildingsCategory;

	private BuildingsCategoryMenu buildingsMenu;
	private BuildingsAdapter adapter;

	@AfterViews
	void afterViews() {
		buildingsMenu = new MenuFactory(getActivity()).buildingsMenu(this, buildingsCategory);
		buildingsMenu.start();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		buildingsMenu.finish();
	}

	/**
	 * BuildingsCategoryView implementation
	 */
	@UiThread
	@Override
	public void setBuildings(List<Building> buildings) {
		if (adapter == null) {
			adapter = new BuildingsCategoryFragment.BuildingsAdapter(buildings);
		} else {
			adapter.setBuildings(buildings);
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
		private List<Building> buildings;

		private LayoutInflater layoutInflater;

		BuildingsAdapter(List<Building> buildings) {
			this.buildings = buildings;
			layoutInflater = getActivity().getLayoutInflater();
		}

		@Override
		public int getItemCount() {
			return buildings.size();
		}

		@Override
		public BuildingsCategoryFragment.BuildingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			final View itemView = layoutInflater.inflate(R.layout.item_building, parent, false);
			final BuildingsCategoryFragment.BuildingViewHolder buildingViewHolder = new BuildingsCategoryFragment.BuildingViewHolder(itemView);

			itemView.setOnClickListener(view -> {
				int position = buildingViewHolder.getLayoutPosition();
				buildingSelected(buildings.get(position).getBuildingType());
			});

			return buildingViewHolder;
		}

		@Override
		public void onBindViewHolder(BuildingsCategoryFragment.BuildingViewHolder holder, int position) {
			holder.setBuilding(buildings.get(position));
		}

		@Override
		public void onBindViewHolder(BuildingViewHolder holder, int position, List<Object> payloads) {
			if (payloads == null || payloads.size() == 0) {
				onBindViewHolder(holder, position);
			} else {
				holder.updateCounts(buildings.get(position));
			}
		}

		public void setBuildings(List<Building> buildings) {
			DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BuildingsDiffCallback(this.buildings, buildings));
			diffResult.dispatchUpdatesTo(this);

			this.buildings = buildings;
		}
	}

	private class BuildingViewHolder extends RecyclerView.ViewHolder {
		private final ImageView imageView;
		private final TextView nameTextView;
		private final TextView buildingCountTextView;
		private final TextView buildingConstructionCountTextView;

		BuildingViewHolder(View itemView) {
			super(itemView);
			imageView = (ImageView) itemView.findViewById(R.id.image_view);
			nameTextView = (TextView) itemView.findViewById(R.id.text_view_building_name);
			buildingCountTextView = (TextView) itemView.findViewById(R.id.text_view_building_count);
			buildingConstructionCountTextView = (TextView) itemView.findViewById(R.id.text_view_building_construction_count);
		}

		void setBuilding(Building building) {
			OriginalImageProvider.get(building.getBuildingType()).setAsImage(imageView);
			nameTextView.setText(building.getName());
			buildingCountTextView.setText(building.getCount());
			buildingConstructionCountTextView.setText(building.getConstructionCount());
		}

		void updateCounts(Building building) {
			buildingCountTextView.setText(building.getCount());
			buildingConstructionCountTextView.setText(building.getConstructionCount());
		}
	}

	/**
	 * Diff callback
	 */
	private class BuildingsDiffCallback extends DiffUtil.Callback  {

		private final List<Building> oldBuildings;
		private final List<Building> newBuildings;

		BuildingsDiffCallback(List<Building> oldBuildings, List<Building> newBuildings) {
			this.oldBuildings = oldBuildings;
			this.newBuildings = newBuildings;
		}

		@Override
		public int getOldListSize() {
			return oldBuildings.size();
		}

		@Override
		public int getNewListSize() {
			return newBuildings.size();
		}

		@Override
		public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
			return oldBuildings.get(oldItemPosition).equals(newBuildings.get(newItemPosition));
		}

		@Override
		public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
			boolean constructedCountsEqual = oldBuildings.get(oldItemPosition).getCount().equals(newBuildings.get(newItemPosition).getCount());
			boolean constructingCountsEqual = oldBuildings.get(oldItemPosition).getConstructionCount().equals(newBuildings.get(newItemPosition).getConstructionCount());
			return constructedCountsEqual && constructingCountsEqual;
		}

		@Nullable
		@Override
		public Object getChangePayload(int oldItemPosition, int newItemPosition) {
			return true;
		}
	}
}
