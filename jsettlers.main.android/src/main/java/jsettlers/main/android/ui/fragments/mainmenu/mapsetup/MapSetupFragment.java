package jsettlers.main.android.ui.fragments.mainmenu.mapsetup;

import jsettlers.main.android.PreviewImageConverter;
import jsettlers.main.android.R;
import jsettlers.main.android.presenters.MapSetupPresenter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.utils.FragmentUtil;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import jsettlers.main.android.views.MapSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public abstract class MapSetupFragment extends Fragment implements MapSetupView {
    private MapSetupPresenter presenter;
    private MainMenuNavigator navigator;

    private Disposable mapPreviewSubscription;

    //private TextView mapNameTextView;
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

        //mapNameTextView = (TextView) view.findViewById(R.id.text_view_map_name);
        mapPreviewImageView = (ImageView) view.findViewById(R.id.image_view_map_preview);
        numberOfPlayersSpinner = (Spinner) view.findViewById(R.id.spinner_number_of_players);
        startResourcesSpinner = (Spinner) view.findViewById(R.id.spinner_start_resources);
        peacetimeSpinner = (Spinner) view.findViewById(R.id.spinner_peacetime);
        startGameButton = (Button) view.findViewById(R.id.button_start_game);

        startGameButton.setOnClickListener(v -> presenter.startGame());

        setSpinnerAdapter(numberOfPlayersSpinner, presenter.getAllowedPlayerCounts());
        setSpinnerAdapter(startResourcesSpinner, presenter.getStartResourcesOptions());
        setSpinnerAdapter(peacetimeSpinner, presenter.getPeaceTimeOptions());

        mapPreviewSubscription = PreviewImageConverter.toBitmap(presenter.getMapImage())
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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(presenter.getMapName());
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
            presenter.abort();
        }
    }

    private <T> void setSpinnerAdapter(Spinner spinner, T[] items) {
        ArrayAdapter<T> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
