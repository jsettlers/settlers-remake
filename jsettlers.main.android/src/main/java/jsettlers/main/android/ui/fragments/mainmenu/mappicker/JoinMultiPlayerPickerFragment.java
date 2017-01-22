package jsettlers.main.android.ui.fragments.mainmenu.mappicker;

import java.util.List;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import jsettlers.common.menu.IJoinableGame;
import jsettlers.main.android.PreviewImageConverter;
import jsettlers.main.android.R;
import jsettlers.main.android.presenters.JoinMultiPlayerPickerPresenter;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
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
import jsettlers.main.android.views.JoinMultiPlayerPickerView;

/**
 * Created by tompr on 21/01/2017.
 */

public class JoinMultiPlayerPickerFragment extends Fragment implements JoinMultiPlayerPickerView {
    private JoinMultiPlayerPickerPresenter presenter;

	private JoinableGamesAdapter adapter;

    private RecyclerView recyclerView;

    public static JoinMultiPlayerPickerFragment create() {
        return new JoinMultiPlayerPickerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameStarter gameStarter = (GameStarter) getActivity().getApplication();
        MainMenuNavigator navigator = (MainMenuNavigator) getActivity();

        presenter = new JoinMultiPlayerPickerPresenter(this, gameStarter, navigator);
        adapter = new JoinableGamesAdapter(presenter.getJoinableGames());
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
        presenter.dispose();
    }


    /**
     * JoinMultiPlayerPickerView implementation
     * @param joinableGames
     */
    @Override
    public void joinableGamesChanged(List<? extends IJoinableGame> joinableGames) {
        getView().post(() -> adapter.setItems(joinableGames));

    }



    private void joinableGameSelected(IJoinableGame joinableGame) {
        presenter.joinableGameSelected(joinableGame);
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
