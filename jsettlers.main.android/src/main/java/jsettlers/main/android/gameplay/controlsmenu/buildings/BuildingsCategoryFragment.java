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

package jsettlers.main.android.gameplay.controlsmenu.buildings;

import java.util.List;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.map.controls.original.panel.content.buildings.EBuildingsCategory;
import jsettlers.main.android.R;
import jsettlers.main.android.core.resources.OriginalImageProvider;

/**
 * Created by Tom Pratt on 24/11/2016.
 */
@EFragment(R.layout.menu_buildings_category)
public class BuildingsCategoryFragment extends Fragment {
	private static final String ARG_BUILDINGS_CATEGORY = "arg_buildings_category";

	public static BuildingsCategoryFragment newInstance(EBuildingsCategory buildingsCategory) {
		return BuildingsCategoryFragment_.builder().buildingsCategory(buildingsCategory).build();
	}

	private BuildingsCategoryViewModel viewModel;

	@ViewById(R.id.recycler_view)
	RecyclerView recyclerView;
	@FragmentArg(ARG_BUILDINGS_CATEGORY)
	EBuildingsCategory buildingsCategory;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel = ViewModelProviders.of(this, new BuildingsCategoryViewModel.Factory(getActivity(), buildingsCategory)).get(BuildingsCategoryViewModel.class);

		BuildingsAdapter adapter = new BuildingsAdapter();
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(adapter);

		viewModel.getBuildingStates().observe(this, adapter::setBuildingViewStates);
	}

	private void buildingSelected(EBuildingType buildingType) {
		viewModel.showConstructionMarkers(buildingType);
	}

	/**
	 * Adapter
	 */
	private class BuildingsAdapter extends RecyclerView.Adapter<BuildingsCategoryFragment.BuildingViewHolder> {
		private final LayoutInflater layoutInflater;

		private BuildingViewState[] buildingViewStates;

		BuildingsAdapter() {
			this.layoutInflater = getActivity().getLayoutInflater();
			this.buildingViewStates = new BuildingViewState[0];
		}

		@Override
		public int getItemCount() {
			return buildingViewStates.length;
		}

		@Override
		public BuildingsCategoryFragment.BuildingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			final View itemView = layoutInflater.inflate(R.layout.item_building, parent, false);
			final BuildingsCategoryFragment.BuildingViewHolder buildingViewHolder = new BuildingsCategoryFragment.BuildingViewHolder(itemView);

			itemView.setOnClickListener(view -> {
				int position = buildingViewHolder.getLayoutPosition();
				buildingSelected(buildingViewStates[position].getBuildingType());
			});

			return buildingViewHolder;
		}

		@Override
		public void onBindViewHolder(BuildingsCategoryFragment.BuildingViewHolder holder, int position) {
			holder.setBuilding(buildingViewStates[position]);
		}

		@Override
		public void onBindViewHolder(BuildingViewHolder holder, int position, List<Object> payloads) {
			if (payloads == null || payloads.size() == 0) {
				onBindViewHolder(holder, position);
			} else {
				holder.updateCounts(buildingViewStates[position]);
			}
		}

		void setBuildingViewStates(BuildingViewState[] buildingViewStates) {
			DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BuildingsDiffCallback(this.buildingViewStates, buildingViewStates));
			diffResult.dispatchUpdatesTo(this);

			this.buildingViewStates = buildingViewStates;
		}
	}

	private class BuildingViewHolder extends RecyclerView.ViewHolder {
		private final ImageView imageView;
		private final TextView nameTextView;
		private final TextView buildingCountTextView;
		private final TextView buildingConstructionCountTextView;

		BuildingViewHolder(View itemView) {
			super(itemView);
			imageView = itemView.findViewById(R.id.image_view);
			nameTextView = itemView.findViewById(R.id.text_view_building_name);
			buildingCountTextView = itemView.findViewById(R.id.text_view_building_count);
			buildingConstructionCountTextView = itemView.findViewById(R.id.text_view_building_construction_count);
		}

		void setBuilding(BuildingViewState buildingViewState) {
			OriginalImageProvider.get(buildingViewState.getBuildingType()).setAsImage(imageView);
			nameTextView.setText(buildingViewState.getName());
			buildingCountTextView.setText(buildingViewState.getCount());
			buildingConstructionCountTextView.setText(buildingViewState.getConstructionCount());
		}

		void updateCounts(BuildingViewState buildingViewState) {
			buildingCountTextView.setText(buildingViewState.getCount());
			buildingConstructionCountTextView.setText(buildingViewState.getConstructionCount());
		}
	}

	/**
	 * Diff callback
	 */
	private class BuildingsDiffCallback extends DiffUtil.Callback {

		private final BuildingViewState[] oldBuildingViewStates;
		private final BuildingViewState[] newBuildingViewStates;

		BuildingsDiffCallback(BuildingViewState[] oldBuildingViewStates, BuildingViewState[] newBuildingViewStates) {
			this.oldBuildingViewStates = oldBuildingViewStates;
			this.newBuildingViewStates = newBuildingViewStates;
		}

		@Override
		public int getOldListSize() {
			return oldBuildingViewStates.length;
		}

		@Override
		public int getNewListSize() {
			return newBuildingViewStates.length;
		}

		@Override
		public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
			return oldBuildingViewStates[oldItemPosition].equals(newBuildingViewStates[newItemPosition]);
		}

		@Override
		public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
			BuildingViewState oldState = oldBuildingViewStates[oldItemPosition];
			BuildingViewState newState = newBuildingViewStates[newItemPosition];
			boolean constructedCountsEqual = oldState.getCount().equals(newState.getCount());
			boolean constructingCountsEqual = oldState.getConstructionCount().equals(newState.getConstructionCount());
			return constructedCountsEqual && constructingCountsEqual;
		}

		@Nullable
		@Override
		public Object getChangePayload(int oldItemPosition, int newItemPosition) {
			return true;
		}
	}
}
