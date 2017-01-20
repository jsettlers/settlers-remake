package jsettlers.main.android.ui.fragments.mainmenu;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.android.PreviewImageConverter;
import jsettlers.main.android.R;
import jsettlers.main.android.providers.GameStarter;
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
import android.widget.TextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class NewSinglePlayerSetupFragment extends Fragment {
	private static final String ARG_MAP_ID = "mapid";

	private GameStarter gameStarter;
	private IMapDefinition map;

	private Disposable mapPreviewSubscription;

	private TextView mapNameTextView;
	private ImageView mapPreviewImageView;
	private Spinner numberOfPlayersSpinner;
	private Button startGameButton;

	public static Fragment newInstance(IMapDefinition mapDefinition) {
		Bundle bundle = new Bundle();
		bundle.putString(ARG_MAP_ID, mapDefinition.getMapId());

		Fragment fragment = new NewSinglePlayerSetupFragment();
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

		mapNameTextView = (TextView) view.findViewById(R.id.text_view_name);
		mapPreviewImageView = (ImageView) view.findViewById(R.id.image_view_map_preview);
		numberOfPlayersSpinner = (Spinner) view.findViewById(R.id.spinner_number_of_players);
		startGameButton = (Button) view.findViewById(R.id.button_start_game);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		gameStarter = (GameStarter) getActivity();
		map = getMap(gameStarter, getArguments().getString(ARG_MAP_ID));

		ArrayAdapter<String> numberOfPlayersAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, new String[] { "on dsf sdf sdf sd e" , "two" });
		numberOfPlayersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		numberOfPlayersSpinner.setAdapter(numberOfPlayersAdapter);

		mapNameTextView.setText(map.getMapName());



		startGameButton.setOnClickListener(startGameClickListener);

		mapPreviewSubscription = PreviewImageConverter.toBitmap(map.getImage())
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

	private static IMapDefinition getMap(GameStarter gameStarter, String mapId) {
		if (mapId != null) {
			for (IMapDefinition map : gameStarter.getStartScreenConnector().getSingleplayerMaps().getItems()) {
				if (map.getMapId().equals(mapId)) {
					return map;
				}
			}
		}
		throw new RuntimeException("Couldn't get selected map.");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mapPreviewSubscription != null) {
			mapPreviewSubscription.dispose();
		}
	}

	private View.OnClickListener startGameClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			gameStarter.startSinglePlayerGame(map);
		}
	};
}
