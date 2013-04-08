package jsettlers.main.android;

import go.graphics.android.AndroidSoundPlayer;
import go.graphics.android.GOSurfaceView;
import go.graphics.android.IContextDestroyedListener;
import go.graphics.area.Area;
import go.graphics.region.Region;

import java.io.File;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.statistics.IStatisticable;
import jsettlers.graphics.androidui.MobileControls;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.graphics.startscreen.IStartScreenConnector;
import jsettlers.main.ManagedJSettlers;
import jsettlers.main.android.bg.BgControls;
import jsettlers.main.android.bg.BgMap;
import jsettlers.main.android.bg.BgStats;
import jsettlers.main.android.fragments.GameCommandFragment;
import jsettlers.main.android.fragments.JsettlersFragment;
import jsettlers.main.android.fragments.StartScreenFragment;
import jsettlers.main.android.fragments.UpdateResourcesFragment;
import jsettlers.main.android.resources.ResourceProvider;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class JsettlersActivity extends Activity {

	private static final int SOUND_THREADS = 6;
	private ManagedJSettlers manager;
	private IStartScreenConnector connector;
	private GOSurfaceView goView;
	private Region goRegion;
	private AndroidSoundPlayer soundPlayer;
	private boolean glInForeground;
	private ResourceProvider provider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
			showFragment(new UpdateResourcesFragment(provider));
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

		getManager().stop();
		manager = null;

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
		        jsettlersdir,
		        storage,
		        jsettlersdir,
		        new File(jsettlersdir, "GFX"),
		        michael,
		        new File(michael, "GFX")
		};

		for (File file : files) {
			ImageProvider.getInstance().addLookupPath(file);
			SoundManager.addLookupPath(new File(file, "Snd"));
		}
		provider = new ResourceProvider(this, files);
		ResourceManager.setProvider(provider);
	}

	private ManagedJSettlers getManager() {
		if (manager == null) {
			manager = new ManagedJSettlers();
		}

		return manager;

	}

	/* - - - - - - - Fragment stuff - - - - - - */

	public void showStartScreen() {
		if (connector != null) {
			showStartScreen(connector);
		} else {
			getManager().start(new JsettlersActivityDisplay(this));
		}
	}

	/**
	 * Shows the start screen.
	 * 
	 * @param connector
	 */
	public void showStartScreen(IStartScreenConnector connector) {
		this.connector = connector;
		getFragmentManager().popBackStack(null,
		        FragmentManager.POP_BACK_STACK_INCLUSIVE);
		showFragment(new StartScreenFragment());
	}

	public void showFragment(JsettlersFragment fragment) {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		if (fragment.shouldAddToBackStack()) {
			transaction.addToBackStack(fragment.getName());
		}
		transaction.replace(R.id.base_menu, fragment);
		transaction.commit();
	}

	public IStartScreenConnector getStartConnector() {
		return connector;
	}

	public MapInterfaceConnector showGameMap(IGraphicsGrid map,
	        IStatisticable playerStatistics) {
		GameCommandFragment p = showMapFragment();

		MapContent content =
		        new MapContent(map, playerStatistics, soundPlayer,
		                new MobileControls(p.getPutable(this)));
		goRegion.setContent(content);
		return content.getInterfaceConnector();
	}

	public void showBgMap() {
		MapContent content =
		        new MapContent(new BgMap(), new BgStats(), soundPlayer,
		                new BgControls());
		goRegion.setContent(content);
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

}