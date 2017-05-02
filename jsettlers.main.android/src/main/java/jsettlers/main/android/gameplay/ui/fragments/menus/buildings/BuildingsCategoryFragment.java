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
import org.androidannotations.annotations.ViewById;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.main.android.R;
import jsettlers.main.android.gameplay.presenters.BuildingsCategoryMenu;
import jsettlers.main.android.gameplay.presenters.MenuFactory;
import jsettlers.main.android.gameplay.ui.views.BuildingsCategoryView;
import jsettlers.main.android.utils.OriginalImageProvider;

import android.os.Bundle;
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
@EFragment(R.layout.menu_buildings_category)
public class BuildingsCategoryFragment extends Fragment implements BuildingsCategoryView {
	private static final String ARG_BUILDINGS_CATEGORY = "arg_buildings_category";

	private BuildingsCategoryMenu buildingsMenu;
	private BuildingsAdapter adapter;

	@ViewById(R.id.recycler_view)
	RecyclerView recyclerView;

	public static BuildingsCategoryFragment newInstance(int buildingsCategory) {
		Bundle bundle = new Bundle();
		bundle.putInt(ARG_BUILDINGS_CATEGORY, buildingsCategory);

		BuildingsCategoryFragment fragment = new BuildingsCategoryFragment_();
		fragment.setArguments(bundle);

		return fragment;
	}

	@AfterViews
	void afterViews() {
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
