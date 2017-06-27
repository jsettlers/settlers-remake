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

package jsettlers.main.android.mainmenu.ui.fragments.setup;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ItemSelect;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import jsettlers.main.android.R;
import jsettlers.main.android.core.ui.FragmentUtil;
import jsettlers.main.android.core.ui.PreviewImageConverter;
import jsettlers.main.android.mainmenu.presenters.setup.MapSetupPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.Peacetime;
import jsettlers.main.android.mainmenu.presenters.setup.PlayerCount;
import jsettlers.main.android.mainmenu.presenters.setup.StartResources;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.Civilisation;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerSlotPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerType;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.StartPosition;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.Team;
import jsettlers.main.android.mainmenu.views.MapSetupView;
import jsettlers.main.android.mainmenu.views.PlayerSlotView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by tompr on 21/01/2017.
 */
@EFragment(R.layout.fragment_new_single_player_setup)
public abstract class MapSetupFragment<Presenter extends MapSetupPresenter> extends Fragment implements MapSetupView {

	@ViewById(R.id.recycler_view)
	RecyclerView recyclerView;
	@ViewById(R.id.image_view_map_preview)
	ImageView mapPreviewImageView;
	@ViewById(R.id.toolbar)
	Toolbar toolbar;

	@ViewById(R.id.spinner_number_of_players)
	protected Spinner numberOfPlayersSpinner;
	@ViewById(R.id.spinner_start_resources)
	protected Spinner startResourcesSpinner;
	@ViewById(R.id.spinner_peacetime)
	protected Spinner peacetimeSpinner;

	@FragmentArg("mapid")
	protected String mapId;

	protected Presenter presenter;
	PlayersAdapter adapter;
	ArrayAdapter<PlayerCount> playerCountsAdapter;
	ArrayAdapter<StartResources> startResourcesAdapter;

	boolean isSaving = false;

	protected abstract Presenter createPresenter();

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		presenter = createPresenter();
	}

	@AfterViews
	void setupView() {
		recyclerView.setHasFixedSize(true);
		FragmentUtil.setActionBar(this, toolbar);

		// Disable these for now, as these features are not implemented yet.
		startResourcesSpinner.setEnabled(false);
		peacetimeSpinner.setEnabled(false);

		presenter.initView();
	}

	@Override
	public void onResume() {
		super.onResume();
		presenter.updateViewTitle();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		isSaving = true;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		presenter.dispose();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (isRemoving() && !isSaving) {
			presenter.viewFinished();
		}
	}

	@Click(R.id.button_start_game)
	protected void onStartGameClicked() {
		if (!presenter.startGame()) {
			Toast.makeText(this.getContext(), R.string.multiplayer_not_all_players_ready, Toast.LENGTH_LONG).show();
		}
	}

	@ItemSelect(R.id.spinner_number_of_players)
	void playerSelected(boolean selected, int position) {
		presenter.playerCountSelected(playerCountsAdapter.getItem(position));
	}

	@ItemSelect(R.id.spinner_start_resources)
	void startResourcesSelected(boolean selected, int position) {
		presenter.startResourcesSelected(startResourcesAdapter.getItem(position));
	}

	/**
	 * MapSetupView implementation
	 */
	@Override
	public void setNumberOfPlayersOptions(PlayerCount[] numberOfPlayersOptions) {
		playerCountsAdapter = getSpinnerAdapter(numberOfPlayersOptions);
		numberOfPlayersSpinner.setAdapter(playerCountsAdapter);
	}

	@Override
	public void setPlayerCount(PlayerCount playerCount) {
		numberOfPlayersSpinner.setSelection(playerCountsAdapter.getPosition(playerCount));
	}

	@Override
	public void setStartResourcesOptions(StartResources[] startResources) {
		startResourcesAdapter = getSpinnerAdapter(startResources);
		startResourcesSpinner.setAdapter(startResourcesAdapter);
	}

	@Override
	public void setStartResources(StartResources startResources) {
		startResourcesSpinner.setSelection(startResourcesAdapter.getPosition(startResources));
	}

	@Override
	public void setPeaceTimeOptions(Peacetime[] peaceTimeOptions) {
		peacetimeSpinner.setAdapter(getSpinnerAdapter(peaceTimeOptions));
	}

	@Override
	public void setMapName(String mapName) {
		getActivity().setTitle(mapName);
	}

	@Override
	@Background
	public void setMapImage(short[] image) {
		setMapImage(PreviewImageConverter.convert(image));
	}

	@UiThread
	public void setMapImage(Bitmap bitmap) {
		if (mapPreviewImageView != null) {
			mapPreviewImageView.setImageBitmap(bitmap);
		}
	}

	private <T> ArrayAdapter<T> getSpinnerAdapter(T[] items) {
		ArrayAdapter<T> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return adapter;
	}

	@Override
	@UiThread
	public void setItems(List<PlayerSlotPresenter> items, int playerCount) {
		if (adapter == null) {
			adapter = new PlayersAdapter(items);
		}

		if (recyclerView.getAdapter() == null) {
			recyclerView.setAdapter(adapter);
		}

		adapter.setItems(items, playerCount);
	}

	protected int getListItemLayoutId() {
		return R.layout.item_playerslot;
	}

	class PlayersAdapter extends RecyclerView.Adapter<PlayerHolder> {
		private final LayoutInflater layoutInflater;

		private List<PlayerSlotPresenter> players;
		private int playerCount;

		PlayersAdapter(List<PlayerSlotPresenter> players) {
			this.layoutInflater = LayoutInflater.from(getActivity());
			this.players = players;
		}

		@Override
		public int getItemCount() {
			return playerCount;
		}

		@Override
		public PlayerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = layoutInflater.inflate(getListItemLayoutId(), parent, false);
			return new PlayerHolder(view);
		}

		@Override
		public void onBindViewHolder(PlayerHolder holder, int position) {
			holder.bind(players.get(position));
		}

		void setItems(List<PlayerSlotPresenter> items, int playerCount) {
			// TODO use diffutil
			this.players = items;
			this.playerCount = playerCount;
			notifyDataSetChanged();
		}
	}

	class PlayerHolder extends RecyclerView.ViewHolder implements PlayerSlotView {
		private PlayerSlotPresenter presenter;

		private final TextView playerNameTextView;
		private final SwitchCompat readySwitch;
		private final Spinner civilisationSpinner;
		private final Spinner playerTypeSpinner;
		private final Spinner startPositionSpinner;
		private final Spinner teamSpinner;

		private ArrayAdapter<Civilisation> civilisationsAdapter;
		private ArrayAdapter<PlayerType> playerTypesAdapter;
		private ArrayAdapter<StartPosition> startPositionsAdapter;
		private ArrayAdapter<Team> teamsAdapter;

		PlayerHolder(View itemView) {
			super(itemView);
			this.playerNameTextView = (TextView) itemView.findViewById(R.id.text_view_player_name);
			this.readySwitch = (SwitchCompat) itemView.findViewById(R.id.switch_ready);
			this.civilisationSpinner = (Spinner) itemView.findViewById(R.id.spinner_civilisation);
			this.playerTypeSpinner = (Spinner) itemView.findViewById(R.id.spinner_type);
			this.startPositionSpinner = (Spinner) itemView.findViewById(R.id.spinner_slot);
			this.teamSpinner = (Spinner) itemView.findViewById(R.id.spinner_team);

			readySwitch.setOnCheckedChangeListener((compoundButton, checked) -> {
				presenter.readyChanged(checked);
			});

			civilisationSpinner.setOnItemSelectedListener(new SpinnerListener() {
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
					Civilisation civilisation = civilisationsAdapter.getItem(position);
					presenter.setCivilisation(civilisation);
				}
			});

			playerTypeSpinner.setOnItemSelectedListener(new SpinnerListener() {
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
					PlayerType playerType = playerTypesAdapter.getItem(position);
					presenter.setPlayerType(playerType);
				}
			});

			startPositionSpinner.setOnItemSelectedListener(new SpinnerListener() {
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
					StartPosition slot = startPositionsAdapter.getItem(position);
					presenter.startPositionSelected(slot);
				}
			});

			teamSpinner.setOnItemSelectedListener(new SpinnerListener() {
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
					Team team = teamsAdapter.getItem(position);
					presenter.teamSelected(team);
				}
			});
		}

		@Override
		public void setName(String name) {
			playerNameTextView.setText(name);
		}

		@Override
		public void setReady(boolean ready) {
			readySwitch.setChecked(ready);
		}

		@Override
		public void setPossibleCivilisations(Civilisation[] possibleCivilisations) {
			civilisationsAdapter = getSpinnerAdapter(possibleCivilisations);
			civilisationSpinner.setAdapter(civilisationsAdapter);
		}

		@Override
		public void setCivilisation(Civilisation civilisation) {
			civilisationSpinner.setSelection(civilisationsAdapter.getPosition(civilisation));
		}

		@Override
		public void setPossiblePlayerTypes(PlayerType[] ePlayerTypes) {
			playerTypesAdapter = getSpinnerAdapter(ePlayerTypes);
			playerTypeSpinner.setAdapter(playerTypesAdapter);
		}

		@Override
		public void setPlayerType(PlayerType playerType) {
			playerTypeSpinner.setSelection(playerTypesAdapter.getPosition(playerType));

		}

		@Override
		public void setPossibleStartPositions(StartPosition[] possibleSlots) {
			startPositionsAdapter = getSpinnerAdapter(possibleSlots);
			startPositionSpinner.setAdapter(startPositionsAdapter);
		}

		@Override
		public void setStartPosition(StartPosition slot) {
			startPositionSpinner.setSelection(startPositionsAdapter.getPosition(slot));
		}

		@Override
		public void setPossibleTeams(Team[] possibleTeams) {
			teamsAdapter = getSpinnerAdapter(possibleTeams);
			teamSpinner.setAdapter(teamsAdapter);
		}

		@Override
		public void setTeam(Team team) {
			teamSpinner.setSelection(teamsAdapter.getPosition(team));
		}

		@Override
		public void showReadyControl() {
			readySwitch.setVisibility(View.VISIBLE);
		}

		@Override
		public void hideReadyControl() {
			readySwitch.setVisibility(View.GONE);
		}

		@Override
		public void setControlsEnabled() {
			readySwitch.setClickable(true);
			readySwitch.setFocusable(true);
			playerTypeSpinner.setEnabled(true);
			civilisationSpinner.setEnabled(true);
			startPositionSpinner.setEnabled(true);
			teamSpinner.setEnabled(true);
		}

		@Override
		public void setControlsDisabled() {
			readySwitch.setClickable(false);
			readySwitch.setFocusable(false);
			playerTypeSpinner.setEnabled(false);
			civilisationSpinner.setEnabled(false);
			startPositionSpinner.setEnabled(false);
			teamSpinner.setEnabled(false);
		}

		void bind(PlayerSlotPresenter playerSlotPresenter) {
			this.presenter = playerSlotPresenter;
			playerSlotPresenter.initView(this);
		}
	}

	private abstract class SpinnerListener implements AdapterView.OnItemSelectedListener {
		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {
			// No op
		}
	}
}
