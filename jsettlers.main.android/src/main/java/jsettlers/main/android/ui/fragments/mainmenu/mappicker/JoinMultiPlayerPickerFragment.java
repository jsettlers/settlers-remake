package jsettlers.main.android.ui.fragments.mainmenu.mappicker;

import java.util.List;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import jsettlers.common.menu.IJoinableGame;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.main.android.PreviewImageConverter;
import jsettlers.main.android.R;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.dialogs.JoiningGameProgressDialog;
import jsettlers.main.android.utils.FragmentUtil;
import jsettlers.main.android.utils.NoChangeItemAnimator;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.reactivex.disposables.Disposable;

/**
 * Created by tompr on 21/01/2017.
 */

public class JoinMultiPlayerPickerFragment extends Fragment implements IChangingListListener<IJoinableGame> {
    private GameStarter gameStarter;
    private ChangingList<IJoinableGame> changingJoinableGames;

	private JoinableGamesAdapter adapter;

    private RecyclerView recyclerView;

    public static JoinMultiPlayerPickerFragment create() {
        return new JoinMultiPlayerPickerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameStarter = (GameStarter) getActivity().getApplication();

        changingJoinableGames = gameStarter.getMultiPlayerConnector().getJoinableMultiplayerGames();
        changingJoinableGames.setListener(this);

        adapter = new JoinableGamesAdapter(changingJoinableGames.getItems());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_picker, container, false);
        FragmentUtil.setActionBar(this, view);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        recyclerView.setItemAnimator(new NoChangeItemAnimator());
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.join_multi_player_game);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        changingJoinableGames.removeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRemoving()) {
            closeJoiningGame();
            gameStarter.closeMultiPlayerConnector();
        }
    }

    /**
     * ChangingListListener implementation
     */
    @Override
    public void listChanged(ChangingList<? extends IJoinableGame> list) {
        getView().post(() -> adapter.setItems(list.getItems()));
    }



    private void joinableGameSelected(IJoinableGame joinableGame) {
        closeJoiningGame();

        IJoiningGame joiningGame = gameStarter.getMultiPlayerConnector().joinMultiplayerGame(joinableGame);
        gameStarter.setJoiningGame(joiningGame);

        JoiningGameProgressDialog.create().show(getChildFragmentManager(), null);
    }

    private void closeJoiningGame() {
        if (gameStarter.getJoiningGame() != null) {
            gameStarter.getJoiningGame().abort();
            gameStarter.setJoiningGame(null);
        }
    }


    /**
     * RecyclerView Adapter for displaying list of maps
     */
    private class JoinableGamesAdapter extends RecyclerView.Adapter<JoinableGameHolder> {
        private List<? extends IJoinableGame> joinableGames;

        public JoinableGamesAdapter(List<? extends IJoinableGame> joinableGames) {
            this.joinableGames = joinableGames;
        }

        @Override
        public int getItemCount() {
            return joinableGames.size();
        }

        @Override
        public JoinableGameHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = getActivity().getLayoutInflater().inflate(R.layout.item_map, parent, false);
            final JoinableGameHolder mapHolder = new JoinableGameHolder(itemView);

            itemView.setOnClickListener(view -> {
                RecyclerView.ViewHolder viewHolder = recyclerView.findContainingViewHolder(itemView);
                int position = viewHolder.getAdapterPosition();
                joinableGameSelected(joinableGames.get(position));
            });

            return mapHolder;
        }

        @Override
        public void onBindViewHolder(JoinableGameHolder holder, int position) {
            IJoinableGame joinableGame = joinableGames.get(position);
            holder.bind(joinableGame);
        }

        void setItems(List<? extends IJoinableGame> joinableGames) {
            this.joinableGames = joinableGames;
            notifyDataSetChanged();
        }
    }

    private class JoinableGameHolder extends RecyclerView.ViewHolder {
        final TextView nameTextView;
        final TextView playerCountTextView;
        final ImageView mapPreviewImageView;

        Disposable subscription;

        public JoinableGameHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.text_view_name);
            playerCountTextView = (TextView) itemView.findViewById(R.id.text_view_player_count);
            mapPreviewImageView = (ImageView) itemView.findViewById(R.id.image_view_map_preview);
        }

        public void bind(IJoinableGame joinableGame) {
            nameTextView.setText(joinableGame.getName());
            playerCountTextView.setText(joinableGame.getMap().getMinPlayers() + "-" + joinableGame.getMap().getMaxPlayers());

            if (subscription != null) {
                subscription.dispose();
            }

            subscription = PreviewImageConverter.toBitmap(joinableGame.getMap().getImage())
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
    }
}
