package jsettlers.main.android;

import go.graphics.android.GOSurfaceView;
import go.graphics.area.Area;

import java.io.File;

import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.JOGLPanel;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.main.JSettlersApp;
import android.app.Activity;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class JsettlersActivity extends Activity {

	private GOSurfaceView glView;

	private boolean started = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!started) {
			startGame();
			started = true;
		}
	}

	private void startGame() {
		File storage = Environment.getExternalStorageDirectory();
		File jsettlersdir = new File(storage, "JSettlers");
		File michael = new File("/mnt/sdcard/usbStorage/JSettlers");
		File[] files =
		        new File[] {
		                getExternalFilesDir(null),
		                storage,
		                jsettlersdir,
		                new File(jsettlersdir, "GFX"),
		                michael,
		                new File(michael, "GFX")
		        };

		ResourceManager.setProvider(new ResourceProvider(files));
		SettlersGame game = new SettlersGame();

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		        WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.getWindow().addFlags(
		        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		for (File file : files) {
			game.addImagePath(file);
		}

		new Thread(game).start();
	}

	private class SettlersGame extends JSettlersApp {
		public SettlersGame() {
			super();
		}

		@Override
		protected void startGui(JOGLPanel content) {
			runOnUiThread(new SetAreaTask(content.getArea()));
		}
	}

	private Area area;

	private class SetAreaTask implements Runnable {

		public SetAreaTask(Area area2) {
			area = area2;
		}

		@Override
		public void run() {
			initView();
		}
	}

	private void initView() {
		if (area != null) {
			ImageProvider.getInstance().invalidateAll();
			glView = new GOSurfaceView(JsettlersActivity.this, area);
			glView.setDebugFlags(GLSurfaceView.DEBUG_LOG_GL_CALLS
			        | GLSurfaceView.DEBUG_CHECK_GL_ERROR);
			setContentView(glView);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		initView();
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
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}