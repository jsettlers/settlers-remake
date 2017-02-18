package jsettlers.main.android.mainmenu.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IMultiplayerPlayer;
import jsettlers.main.android.R;
import jsettlers.main.android.core.ui.NoChangeItemAnimator;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.NewMultiPlayerSetupPresenter;
import jsettlers.main.android.mainmenu.views.NewMultiPlayerSetupView;
import jsettlers.main.android.mainmenu.views.PlayerItemView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerSetupFragment extends MapSetupFragment implements NewMultiPlayerSetupView {
    private static final String ARG_MAP_ID = "mapid";

    private NewMultiPlayerSetupPresenter presenter;

    private PlayersAdapter adapter;
    private RecyclerView recyclerView;

    public static Fragment create(IMapDefinition mapDefinition) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MAP_ID, mapDefinition.getMapId());

        Fragment fragment = new NewMultiPlayerSetupFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected NewMultiPlayerSetupPresenter getPresenter() {
        presenter = PresenterFactory.createNewMultiPlayerSetupPresenter(getActivity(), this, getArguments().getString(ARG_MAP_ID));
        return presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        return view;
    }

    /**
     * NewMultiPlayerSetupView implementation
     */
//    @Override
//    public void setItems(List<IMultiplayerPlayer> items) {
//        getActivity().runOnUiThread(() -> {
//            if (adapter == null) {
//                adapter = new PlayersAdapter(items);
//            }
//
//            if (recyclerView.getAdapter() == null) {
//                recyclerView.setAdapter(adapter);
//            }
//
//            adapter.setItems(items);
//        });
//    }

    class PlayersAdapter extends RecyclerView.Adapter<PlayerHolder> {
        private final LayoutInflater layoutInflater;

        private List<IMultiplayerPlayer> players;

        PlayersAdapter(List<IMultiplayerPlayer> players) {
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

        void setItems(List<IMultiplayerPlayer> items) {
            //TODO use diffutil
            players = items;
            notifyDataSetChanged();
        }
    }

    class PlayerHolder extends RecyclerView.ViewHolder{
        private final TextView playerNameTextView;
        private final SwitchCompat readySwitch;

        PlayerHolder(View itemView) {
            super(itemView);
            this.playerNameTextView = (TextView) itemView.findViewById(R.id.text_view_player_name);
            this.readySwitch = (SwitchCompat) itemView.findViewById(R.id.switch_ready);
        }

        void bind(IMultiplayerPlayer multiplayerPlayer) {
            playerNameTextView.setText(multiplayerPlayer.getName());
            readySwitch.setChecked(multiplayerPlayer.isReady());

            String playerId = multiplayerPlayer.getId();
            String myId = presenter.getMyPlayerId();
            Log.d("Settlers", playerId + "-----------" + myId);
            boolean isMe = multiplayerPlayer.getId().equals(presenter.getMyPlayerId());
            readySwitch.setEnabled(isMe);
        }
    }
}
