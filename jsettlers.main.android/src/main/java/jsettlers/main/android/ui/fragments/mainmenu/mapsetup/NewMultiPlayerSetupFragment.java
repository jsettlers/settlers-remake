package jsettlers.main.android.ui.fragments.mainmenu.mapsetup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import jsettlers.common.menu.IMultiplayerPlayer;
import jsettlers.main.android.R;
import jsettlers.main.android.presenters.NewMultiPlayerSetupPresenter;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.views.NewMultiPlayerSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerSetupFragment extends MapSetupFragment implements NewMultiPlayerSetupView {
    public static Fragment create() {
        return new NewMultiPlayerSetupFragment();
    }

    private PlayersAdapter adapter;

    private NewMultiPlayerSetupPresenter presenter;

    @Override
    protected NewMultiPlayerSetupPresenter getPresenter() {
        GameStarter gameStarter = (GameStarter) getActivity().getApplication();
        MainMenuNavigator navigator = (MainMenuNavigator) getActivity();
        presenter = new NewMultiPlayerSetupPresenter(this, gameStarter, navigator);
        return presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PlayersAdapter(presenter.getPlayers());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    /**
     * NewMultiPlayerSetupView implementation
     */
    @Override
    public void setItems(List<IMultiplayerPlayer> items) {
        getActivity().runOnUiThread(() -> adapter.setItems(items));
    }

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

    class PlayerHolder extends RecyclerView.ViewHolder {
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
