package jsettlers.main.android;

import go.graphics.android.AndroidContext;
import go.graphics.android.GOSurfaceView;
import go.graphics.area.Area;
import go.graphics.region.Region;

import java.io.File;
import java.util.Date;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.resources.ResourceManager;
import jsettlers.common.statistics.IStatisticable;
import jsettlers.graphics.ISettlersGameDisplay;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.graphics.startscreen.IStartScreenConnector;
import jsettlers.graphics.startscreen.IStartScreenConnector.IGameSettings;
import jsettlers.graphics.startscreen.IStartScreenConnector.ILoadableGame;
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import jsettlers.main.ManagedJSettlers;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

public class JsettlersActivity extends Activity implements ISettlersGameDisplay {

	private GOSurfaceView glView;

	private boolean managerStarted = false;

	private ManagedJSettlers manager;

	private IStartScreenConnector displayedStartScreen;
	private Area area;

	private enum EAndroidUIState {
		SHOW_PROGRESS, SHOW_STARTSCREEN, SHOW_ACTIVE_GAME
	}

	private EAndroidUIState state = EAndroidUIState.SHOW_STARTSCREEN;

	private FrameLayout glHolderView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
	}

	@Override
	protected void onStart() {
		super.onStart();

		keepScreenOn();

		if (!managerStarted) {
			addImageLookups();

			manager = new ManagedJSettlers();
			manager.start(this);
			managerStarted = true;
		}
	}

	private void keepScreenOn() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		        WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.getWindow().addFlags(
		        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void addImageLookups() {
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
		}
		ResourceManager.setProvider(new ResourceProvider(this, files));
	}

	// private Area area;

	// private class SetAreaTask implements Runnable {
	//
	// private Area area2;
	//
	// public SetAreaTask(Area area2) {
	// this.area2 = area2;
	// }
	//
	// @Override
	// public void run() {
	// area = area2;
	// restartMapContet();
	// }
	// }

	private void restartMapContet() {
		if (glView != null) {
			ImageProvider.getInstance().invalidateAll();

			setContentView(glView);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		restartMapContet();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (glView != null) {
			glView.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (glView != null) {
			glView.onPause();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (state == EAndroidUIState.SHOW_ACTIVE_GAME) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.options_menu, menu);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (glView == null || state != EAndroidUIState.SHOW_ACTIVE_GAME) {
			return false;
		}
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.f12btn:
				glView.fireKey("F12");
				return true;
			case R.id.savebtn:
				glView.fireKey("F2");
				return true;
				// case R.id.loadbtn:
				// glView.fireKey("q");
				// return true;
				// case R.id.pausebtn:
				// glView.fireKey("PAUSE");
				// return true;
			case R.id.speedup:
				glView.fireKey("+");
				glView.fireKey("+");
				return true;
			case R.id.slowdown:
				glView.fireKey("-");
				glView.fireKey("-");
				return true;
			case R.id.kill:
				glView.fireKey("DELETE");
				return true;
			case R.id.stop:
				glView.fireKey("STOP");
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		if (glView == null || state != EAndroidUIState.SHOW_ACTIVE_GAME) {
			super.onBackPressed();
		} else {
			glView.fireKey("PAUSE");
		}
	}

	@Override
	protected void onStop() {
		System.exit(0);
	}

	@Override
	public ProgressConnector showProgress() {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				state = EAndroidUIState.SHOW_PROGRESS;

				displayedStartScreen = null;

				setContentView(R.layout.progress);

				glHolderView = (FrameLayout) findViewById(R.id.hiddenGlView);

				preloadGlView();
			}
		});
		return new AProgressConnector(this);
	}

	@Override
	public void showStartScreen(IStartScreenConnector connector) {
		state = EAndroidUIState.SHOW_STARTSCREEN;
		this.displayedStartScreen = connector;
		disposeGLView();

		setContentView(R.layout.startmenu);
	}

	/**
	 * Onclick listener
	 */
	public void startGameButtonClicked(@SuppressWarnings("unused") View target) {
		if (displayedStartScreen != null) {
			displayedStartScreen.startNewGame(new IGameSettings() {
				@Override
				public int getPlayerCount() {
					return 3;
				}

				@Override
				public IMapItem getMap() {
					return displayedStartScreen.getMaps()[0];
				}
			});
		}
	}

	/**
	 * Onclick listener
	 */
	public void loadGameButtonClicked(@SuppressWarnings("unused") View target) {
		if (displayedStartScreen != null) {
			displayedStartScreen.loadGame(new ILoadableGame() {
				@Override
				public String getName() {
					return "quicksave";
				}

				@Override
				public Date getSaveTime() {
					return null;
				}
			});
		}
	}

	/**
	 * Hides the gl view, deletes all references. Needs not be on ui thread
	 */
	private void disposeGLView() {
		glView = null;
		area = null;
		ImageProvider.getInstance().invalidateAll();
	}

	@Override
	public MapInterfaceConnector showGameMap(IGraphicsGrid map,
	        IStatisticable playerStatistics) {
		displayedStartScreen = null;
		state = EAndroidUIState.SHOW_ACTIVE_GAME;

		final MapContent content = new MapContent(map);
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Region region = new Region(Region.POSITION_CENTER);
				region.setContent(content);
				area.add(region);
				preloadGlView(); // ensures the gl view exists
				System.out.println("setting content");

				View progress = findViewById(R.id.progressAllContnet);
				//TODO: we should use progress.setVisibility(View.GONE); after the animation
				AlphaAnimation anim = new AlphaAnimation(1, 0f);
				anim.setDuration (1000);
				anim.setFillAfter(true);
				progress.startAnimation(anim);
			}
		});
		return content.getInterfaceConnector();
	}

	public void preloadGlView() {
		if (this.state != EAndroidUIState.SHOW_ACTIVE_GAME
		        && state != EAndroidUIState.SHOW_PROGRESS) {
			return;
		}

		if (glView == null) {
			System.out.println("generating gl view");
			area = new Area();
			glView = new GOSurfaceView(JsettlersActivity.this, area);

			glHolderView.addView(glView);
		}
		
		System.out.println("requesting opengl preload");
		glView.queueEvent(new Runnable() {
			@Override
			public void run() {
				System.out.println("running opengl preload");
				ImageProvider.getInstance().runPreloadTasks(
				        new AndroidContext());
			}
		});
	}
}