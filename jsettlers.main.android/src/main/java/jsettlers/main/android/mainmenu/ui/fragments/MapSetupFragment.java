package jsettlers.main.android.mainmenu.ui.fragments;

import java.util.List;

import jsettlers.logic.map.loading.EMapStartResources;
import jsettlers.main.android.R;
import jsettlers.main.android.core.ui.FragmentUtil;
import jsettlers.main.android.core.ui.PreviewImageConverter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.presenters.MapSetupPresenter;
import jsettlers.main.android.mainmenu.presenters.PlayerItemPresenter;
import jsettlers.main.android.mainmenu.views.MapSetupView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import jsettlers.main.android.mainmenu.views.PlayerItemView;

/**
 * Created by tompr on 21/01/2017.
 */

public abstract class MapSetupFragment extends Fragment implements MapSetupView {
    private MapSetupPresenter presenter;
    private MainMenuNavigator navigator;

    private PlayersAdapter adapter;
    private Disposable mapPreviewSubscription;

    private RecyclerView recyclerView;
    private ImageView mapPreviewImageView;
    private Spinner numberOfPlayersSpinner;
    private Spinner startResourcesSpinner;
    private Spinner peacetimeSpinner;
    private Button startGameButton;

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

        mapPreviewImageView = (ImageView) view.findViewById(R.id.image_view_map_preview);
        numberOfPlayersSpinner = (Spinner) view.findViewById(R.id.spinner_number_of_players);
        startResourcesSpinner = (Spinner) view.findViewById(R.id.spinner_start_resources);
        peacetimeSpinner = (Spinner) view.findViewById(R.id.spinner_peacetime);
        startGameButton = (Button) view.findViewById(R.id.button_start_game);

        startGameButton.setOnClickListener(v -> presenter.startGame());

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
    public void setNumberOfPlayersOptions(Integer[] numberOfPlayersOptions) {
        setSpinnerAdapter(numberOfPlayersSpinner, numberOfPlayersOptions);
    }

    @Override
    public void setStartResourcesOptions(EMapStartResources[] startResourcesOptions) {
        setSpinnerAdapter(startResourcesSpinner, startResourcesOptions);
    }

    @Override
    public void setPeaceTimeOptions(String[] peaceTimeOptions) {
        setSpinnerAdapter(peacetimeSpinner, peaceTimeOptions);
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

    private <T> void setSpinnerAdapter(Spinner spinner, T[] items) {
        ArrayAdapter<T> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void setItems(List<PlayerItemPresenter> items) {
        getActivity().runOnUiThread(() -> {
            if (adapter == null) {
                adapter = new PlayersAdapter(items);
            }

            if (recyclerView.getAdapter() == null) {
                recyclerView.setAdapter(adapter);
            }

            adapter.setItems(items);
        });
    }



    class PlayersAdapter extends RecyclerView.Adapter<PlayerHolder> {
        private final LayoutInflater layoutInflater;

        private List<PlayerItemPresenter> players;

        PlayersAdapter(List<PlayerItemPresenter> players) {
            this.layoutInflater = LayoutInflater.from(getActivity());
            this.players = players;
        }

        @Override
        public int getItemCount() {
            return players.size();
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

        void setItems(List<PlayerItemPresenter> items) {
            //TODO use diffutil
            players = items;
            notifyDataSetChanged();
        }
    }

    class PlayerHolder extends RecyclerView.ViewHolder implements PlayerItemView {
        private final TextView playerNameTextView;
        private final SwitchCompat readySwitch;

        PlayerHolder(View itemView) {
            super(itemView);
            this.playerNameTextView = (TextView) itemView.findViewById(R.id.text_view_player_name);
            this.readySwitch = (SwitchCompat) itemView.findViewById(R.id.switch_ready);
        }

        @Override
        public void setName(String name) {
            playerNameTextView.setText(name);
        }

        void bind(PlayerItemPresenter player) {
            player.bindView(this);
        }
    }
}
