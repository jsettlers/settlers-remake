package jsettlers.main.android;

import go.graphics.android.GOSurfaceView;
import go.graphics.area.Area;
import go.graphics.region.Region;

import java.io.File;

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
import jsettlers.graphics.startscreen.IStartScreenConnector.IMapItem;
import jsettlers.main.ManagedJSettlers;
import android.app.Activity;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class JsettlersActivity extends Activity implements ISettlersGameDisplay {

	private GOSurfaceView glView;

	private boolean managerStarted = false;

	private ManagedJSettlers manager;

	private IStartScreenConnector displayedStartScreen;

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
		        getExternalFilesDir(null), // <- output dir
		        storage,
		        jsettlersdir,
		        new File(jsettlersdir, "GFX"),
		        michael,
		        new File(michael, "GFX")
		};

		for (File file : files) {
			ImageProvider.getInstance().addLookupPath(file);
		}
		ResourceManager.setProvider(new ResourceProvider(files));
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
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.f12btn:
				glView.fireKey("F12");
				return true;
			case R.id.savebtn:
				glView.fireKey("F2");
				return true;
			case R.id.loadbtn:
				glView.fireKey("q");
				return true;
			case R.id.pausebtn:
				glView.fireKey("PAUSE");
				return true;
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
	protected void onStop() {
		System.exit(0);
	}

	@Override
	public ProgressConnector showProgress() {
		disposeGLView();
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				displayedStartScreen = null;
				
				setContentView(R.layout.progress);
			}
		});
		return new AProgressConnector(this);
	}

	@Override
	public void showStartScreen(IStartScreenConnector connector) {
		this.displayedStartScreen = connector;
		disposeGLView();

		setContentView(R.layout.startmenu);
	}
	
	/**
	 * Onclick listener
	 */
	public void startGameButtonClicked(View target) {
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
	 * Hides the gl view, deletes all references. Needs not be on ui thread
	 */
	private void disposeGLView() {
	    glView = null;
    }

	@Override
	public MapInterfaceConnector showGameMap(IGraphicsGrid map,
	        IStatisticable playerStatistics) {
		displayedStartScreen = null;
		
		final MapContent content = new MapContent(map);
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Area area = new Area();
				Region region = new Region(Region.POSITION_CENTER);
				region.setContent(content);
				area.add(region);
				glView = new GOSurfaceView(JsettlersActivity.this, area);
				glView.setDebugFlags(GLSurfaceView.DEBUG_LOG_GL_CALLS
				        | GLSurfaceView.DEBUG_CHECK_GL_ERROR);
				
				setContentView(glView);
			}
		});
		return content.getInterfaceConnector();
	}

	// @Override
	// public void startGui(JOGLPanel content) {
	// this.runOnUiThread(new SetAreaTask(content.getArea()));
	// };
}