package jsettlers.main.android;

import go.graphics.android.AndroidSoundPlayer;
import go.graphics.android.GOSurfaceView;
import go.graphics.android.IContextDestroyedListener;
import go.graphics.area.Area;
import go.graphics.region.Region;

import java.io.File;

import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.androidui.MobileControls;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.graphics.startscreen.interfaces.FakeMapGame;
import jsettlers.graphics.startscreen.interfaces.IGameExitListener;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import jsettlers.graphics.startscreen.interfaces.IStartedGame;
import jsettlers.graphics.startscreen.interfaces.Player;
import jsettlers.main.StartScreenConnector;
import jsettlers.main.android.bg.BgControls;
import jsettlers.main.android.bg.BgMap;
import jsettlers.main.android.fragments.GameCommandFragment;
import jsettlers.main.android.fragments.JsettlersFragment;
import jsettlers.main.android.fragments.StartScreenFragment;
import jsettlers.main.android.fragments.progress.UpdateResourcesFragment;
import jsettlers.main.android.resources.ResourceProvider;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class JsettlersActivity extends Activity implements IGameExitListener {

	private static final int SOUND_THREADS = 6;
	private GOSurfaceView goView;
	private Region goRegion;
	private AndroidSoundPlayer soundPlayer;
	private ResourceProvider provider;
	private MapContent activeBgMapContent;
	private StartScreenConnector connector;
	private AndroidPreferences prefs;
	private IMultiplayerConnector multiplayerConnector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = new AndroidPreferences(getSharedPreferences("prefs", 0));
		keepScreenOn();
		setContentView(R.layout.base);
		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
		loadImageLookups();

		goRegion = new Region(Region.POSITION_CENTER);
		Area goArea = new Area();
		goArea.add(goRegion);
		goView = new GOSurfaceView(this, goArea);
		((FrameLayout) findViewById(R.id.base_gl)).addView(goView);
		soundPlayer = new AndroidSoundPlayer(SOUND_THREADS);

		if (provider.needsUpdate()) {
			showFragment(new UpdateResourcesFragment());
		} else {
			showStartScreen();
		}

		showBgMap();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		goView.onResume();
		soundPlayer.setPaused(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		goView.onPause();
		soundPlayer.setPaused(true);

		goView.setContextDestroyedListener(new IContextDestroyedListener() {
			@Override
			public void glContextDestroyed() {
				ImageProvider.getInstance().invalidateAll();
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// TODO: Destroy current game.

		goRegion = null;
		goView = null;
		((FrameLayout) findViewById(R.id.base_gl)).removeAllViews();
		soundPlayer = null;
	}

	private void keepScreenOn() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void loadImageLookups() {
		File storage = Environment.getExternalStorageDirectory();
		File jsettlersdir = new File(storage, "JSettlers");
		File michael = new File("/mnt/sdcard/usbStorage/JSettlers");
		File[] files = new File[] {
				getExternalFilesDir(null), // <- output dir, always writable
				jsettlersdir, storage, jsettlersdir,
				new File(jsettlersdir, "GFX"), michael,
				new File(michael, "GFX") };

		for (File file : files) {
			ImageProvider.getInstance().addLookupPath(file);
			SoundManager.addLookupPath(new File(file, "Snd"));
		}
		provider = new ResourceProvider(this, files);
		ResourceManager.setProvider(provider);
	}

	/* - - - - - - - Fragment stuff - - - - - - */

	public void showStartScreen() {
		if (connector == null) {
			connector = new StartScreenConnector();
		}
		showFragment(new StartScreenFragment());
	}

	public void showFragment(JsettlersFragment fragment) {
		FragmentManager manager = getFragmentManager();
		if (!fragment.shouldAddToBackStack()) {
			manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		FragmentTransaction transaction = manager.beginTransaction();
		if (fragment.shouldAddToBackStack()) {
			transaction.addToBackStack(fragment.getName());
		}
		transaction.replace(R.id.base_menu, fragment);
		transaction.commit();
	}

	@Override
	public void onBackPressed() {
		FragmentManager manager = getFragmentManager();
		Fragment last = manager.findFragmentById(R.id.base_menu);
		if (last instanceof JsettlersFragment) {
			if (((JsettlersFragment) last).onBackButtonPressed()) {
				return;
			}
		}
		super.onBackPressed();
	}

	public StartScreenConnector getStartConnector() {
		return connector;
	}

	public MapInterfaceConnector showGameMap(IStartedGame game) {
		stopBgMapThreads();
		GameCommandFragment p = showMapFragment();

		MapContent content = new MapContent(game, soundPlayer,
				new MobileControls(p.getPutable(this)));
		goRegion.setContent(content);
		game.setGameExitListener(this);
		return content.getInterfaceConnector();
	}

	public void showBgMap() {
		stopBgMapThreads();
		IStartedGame game = new FakeMapGame(new BgMap());
		activeBgMapContent = new MapContent(game, soundPlayer, new BgControls());
		goRegion.setContent(activeBgMapContent);
	}

	private void stopBgMapThreads() {
		if (activeBgMapContent != null) {
			activeBgMapContent.stop();
			activeBgMapContent = null;
		}
	}

	private GameCommandFragment showMapFragment() {
		final GameCommandFragment cFragment = new GameCommandFragment();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showFragment(cFragment);
			}
		});
		return cFragment;
	}

	public void fireKey(String string) {
		goView.fireKey(string);
	}

	public ResourceProvider getResourceProvider() {
		return provider;
	}

	public IMultiplayerConnector getMultiplayerConnector() {
		if (multiplayerConnector == null) {
			multiplayerConnector = getStartConnector().getMultiplayerConnector(
					prefs.getServer(),
					new Player(prefs.getPlayerId(), prefs.getPlayerName()));
		}
		return multiplayerConnector;
	}
	
	public AndroidPreferences getPrefs() {
		return prefs;
	}

	@Override
	public void gameExited(IStartedGame game) {
		showStartScreen();
	}
}