package jsettlers.main.android;

import go.graphics.android.GOSurfaceView;
import go.graphics.area.Area;

import java.io.File;

import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.JOGLPanel;
import jsettlers.main.JSettlersApp;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;

public class JsettlersActivity extends Activity {

	private GLSurfaceView glView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		System.out.println("started");
	}

	@Override
	protected void onStart() {
		super.onStart();
		File storage = Environment.getExternalStorageDirectory();
		File jsettlersdir = new File(storage, "JSettlers");
		File[] files = new File[] {
				getExternalFilesDir(null),storage,jsettlersdir,new File(jsettlersdir, "GFX"),
		};
		
		ResourceManager.setProvider(new ResourceProvider(files));
		SettlersGame game = new SettlersGame();
		
		super.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		for (File file : files) {
			game.addImagePath(file);
		}
		
		new Thread(game).start();
		System.out.println("got on start");

	}

	private class SettlersGame extends JSettlersApp {
		public SettlersGame() {
			super();
		}

		@Override
		protected void startGui(JOGLPanel content) {
			System.out.println("request adding of GL view");
			runOnUiThread(new SetAreaTask(content.getArea()));
		}
	}

	private class SetAreaTask implements Runnable {
		private final Area area;

		public SetAreaTask(Area area) {
			this.area = area;
		}

		@Override
		public void run() {
			System.out.println("added GL view");
			glView = new GOSurfaceView(JsettlersActivity.this, area);
			glView.setDebugFlags(GLSurfaceView.DEBUG_LOG_GL_CALLS | GLSurfaceView.DEBUG_CHECK_GL_ERROR);
			setContentView(glView);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// glView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// glView.onPause();
	}
}