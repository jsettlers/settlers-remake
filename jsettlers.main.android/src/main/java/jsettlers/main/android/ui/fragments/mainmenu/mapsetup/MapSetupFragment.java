package jsettlers.main.android.ui.fragments.mainmenu.mapsetup;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.android.PreviewImageConverter;
import jsettlers.main.android.R;
import jsettlers.main.android.menus.mainmenu.MapSetupMenu;
import jsettlers.main.android.providers.GameStarter;
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

/**
 * Created by tompr on 21/01/2017.
 */

public abstract class MapSetupFragment<TMenu extends MapSetupMenu> extends Fragment {
    private static final String ARG_MAP_ID = "mapid";

    private TMenu menu;
    private MainMenuNavigator navigator;

    private Disposable mapPreviewSubscription;

    //private TextView mapNameTextView;
    private ImageView mapPreviewImageView;
    private Spinner numberOfPlayersSpinner;
    private Spinner startResourcesSpinner;
    private Spinner peacetimeSpinner;
    private Button startGameButton;

    public static Fragment createNewSinglePlayerSetupFragment(IMapDefinition mapDefinition) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MAP_ID, mapDefinition.getMapId());

        Fragment fragment = new NewSinglePlayerSetupFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public static Fragment createNewMultiPlayerSetupFragment(IMapDefinition mapDefinition) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MAP_ID, mapDefinition.getMapId());
        Fragment fragment = new NewMultiPlayerSetupFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GameStarter gameStarter = (GameStarter) getActivity().getApplication();
        String mapId = getArguments().getString(ARG_MAP_ID);
        menu = createMenu(gameStarter, mapId);// new MapSetupMenu(gameStarter, mapId);
        navigator = (MainMenuNavigator) getActivity();

        //mapNameTextView.setText(menu.getMapName());
        startGameButton.setOnClickListener(view -> {
            menu.startGame();
            navigator.showGame();
        });

        setSpinnerAdapter(numberOfPlayersSpinner, menu.getAllowedPlayerCounts());
        setSpinnerAdapter(startResourcesSpinner, menu.getStartResourcesOptions());
        setSpinnerAdapter(peacetimeSpinner, menu.getPeaceTimeOptions());

        mapPreviewSubscription = PreviewImageConverter.toBitmap(menu.getMapImage())
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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(menu.getMapName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapPreviewSubscription != null) {
            mapPreviewSubscription.dispose();
        }
    }

    protected abstract TMenu createMenu(GameStarter gameStarter, String mapId);

    protected TMenu getMenu() {
        return menu;
    }

    private <T> void setSpinnerAdapter(Spinner spinner, T[] items) {
        ArrayAdapter<T> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}
