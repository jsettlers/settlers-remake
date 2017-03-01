package jsettlers.main.android.mainmenu.ui.fragments.setup;

import java.util.List;

import jsettlers.main.android.R;
import jsettlers.main.android.core.ui.FragmentUtil;
import jsettlers.main.android.core.ui.PreviewImageConverter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.presenters.setup.MapSetupPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.Civilisation;
import jsettlers.main.android.mainmenu.presenters.setup.Peacetime;
import jsettlers.main.android.mainmenu.presenters.setup.PlayerCount;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerSlotPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerType;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.StartPosition;
import jsettlers.main.android.mainmenu.presenters.setup.StartResources;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.Team;
import jsettlers.main.android.mainmenu.views.MapSetupView;
import jsettlers.main.android.mainmenu.views.PlayerSlotView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tompr on 21/01/2017.
 */

public abstract class MapSetupFragment extends Fragment implements MapSetupView {
    private MapSetupPresenter presenter;
    private MainMenuNavigator navigator;

    private Disposable mapPreviewSubscription;

    private PlayersAdapter adapter;
    private ArrayAdapter<PlayerCount> playerCountsAdapter;
    private ArrayAdapter<StartResources> startResourcesAdapter;

    private RecyclerView recyclerView;
    private ImageView mapPreviewImageView;
    private Button startGameButton;
    private Spinner numberOfPlayersSpinner;
    private Spinner startResourcesSpinner;
    private Spinner peacetimeSpinner;

    private boolean isSaving = false;

    protected abstract MapSetupPresenter getPresenter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = getPresenter();
        navigator = (MainMenuNavigator) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_single_player_setup, container, false);
        FragmentUtil.setActionBar(this, view);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        startGameButton = (Button) view.findViewById(R.id.button_start_game);
        startGameButton.setOnClickListener(v -> presenter.startGame());

        mapPreviewImageView = (ImageView) view.findViewById(R.id.image_view_map_preview);
        numberOfPlayersSpinner = (Spinner) view.findViewById(R.id.spinner_number_of_players);
        startResourcesSpinner = (Spinner) view.findViewById(R.id.spinner_start_resources);
        peacetimeSpinner = (Spinner) view.findViewById(R.id.spinner_peacetime);

        numberOfPlayersSpinner.setOnItemSelectedListener(new SpinnerListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                presenter.playerCountSelected(playerCountsAdapter.getItem(position));
            }
        });

        startResourcesSpinner.setOnItemSelectedListener(new SpinnerListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                presenter.startResourcesSelected(startResourcesAdapter.getItem(position));
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        if (mapPreviewSubscription != null) {
            mapPreviewSubscription.dispose();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (isRemoving() && !isSaving) {
            presenter.viewFinished();
        }
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
    public void setMapImage(short[] image) {
        mapPreviewSubscription = PreviewImageConverter.toBitmap(image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Bitmap>() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        mapPreviewImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mapPreviewImageView.setImageDrawable(null);
                    }
                });
    }

    private <T> ArrayAdapter<T> getSpinnerAdapter(T[] items) {
        ArrayAdapter<T> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    @Override
    public void setItems(List<PlayerSlotPresenter> items, int playerCount) {
        getActivity().runOnUiThread(() -> {
            if (adapter == null) {
                adapter = new PlayersAdapter(items);
            }

            if (recyclerView.getAdapter() == null) {
                recyclerView.setAdapter(adapter);
            }

            adapter.setItems(items, playerCount);
        });
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
            View view = layoutInflater.inflate(R.layout.item_multi_player, parent, false);
            PlayerHolder playerHolder = new PlayerHolder(view);

            return playerHolder;
        }

        @Override
        public void onBindViewHolder(PlayerHolder holder, int position) {
            holder.bind(players.get(position));
        }

        void setItems(List<PlayerSlotPresenter> items, int playerCount) {
            //TODO use diffutil
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
            readySwitch.setEnabled(true);
            playerTypeSpinner.setEnabled(true);
            civilisationSpinner.setEnabled(true);
            startPositionSpinner.setEnabled(true);
            teamSpinner.setEnabled(true);
        }

        @Override
        public void setControlsDisabled() {
            readySwitch.setEnabled(false);
            playerTypeSpinner.setEnabled(false);
            civilisationSpinner.setEnabled(false);
            startPositionSpinner.setEnabled(false);
            teamSpinner.setEnabled(false);
        }

        void bind(PlayerSlotPresenter playerSlotPresenter) {
            this.presenter = playerSlotPresenter;
            playerSlotPresenter.bindView(this);
        }
    }

    private abstract class SpinnerListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            // No op
        }
    }
}
