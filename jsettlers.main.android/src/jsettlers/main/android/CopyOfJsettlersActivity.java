package jsettlers.main.android;

public class CopyOfJsettlersActivity {
	//
	// private static final String PREFS_NAME = "PREFS";
	//
	// private static final int SOUND_THREADS = 7;
	//
	// private GOSurfaceView glView;
	//
	// private boolean managerStarted = false;
	//
	// private ManagedJSettlers manager;
	//
	// private IStartScreenConnector displayedStartScreen;
	// private Area area;
	//
	// private enum EAndroidUIState {
	// SHOW_PROGRESS,
	// SHOW_STARTSCREEN,
	// SHOW_ACTIVE_GAME,
	// SHOW_GAMELIST
	// }
	//
	// private EAndroidUIState state = EAndroidUIState.SHOW_STARTSCREEN;
	//
	// private FrameLayout glHolderView;
	//
	// /** Called when the activity is first created. */
	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	//
	// System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
	// }
	//
	// @Override
	// protected void onStart() {
	// super.onStart();
	//
	// keepScreenOn();
	//
	// if (!managerStarted) {
	// addImageLookups();
	//
	// manager = new ManagedJSettlers();
	// manager.start(this);
	// managerStarted = true;
	// }
	//
	// new LanServerAddressBroadcastListener().start();
	// }
	//
	// private void keepScreenOn() {
	// requestWindowFeature(Window.FEATURE_NO_TITLE);
	// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	//
	// super.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	// }
	//
	// private void addImageLookups() {
	// File storage = Environment.getExternalStorageDirectory();
	// File jsettlersdir = new File(storage, "JSettlers");
	// File michael = new File("/mnt/sdcard/usbStorage/JSettlers");
	// File[] files = new File[] { getExternalFilesDir(null), // <- output dir, always writable
	// jsettlersdir, storage, jsettlersdir, new File(jsettlersdir, "GFX"), michael, new File(michael, "GFX") };
	//
	// for (File file : files) {
	// ImageProvider.getInstance().addLookupPath(file);
	// SoundManager.addLookupPath(new File(file, "Snd"));
	// }
	// provider = new ResourceProvider(this, files);
	// ResourceManager.setProvider(provider);
	// }
	//
	// // private Area area;
	//
	// // private class SetAreaTask implements Runnable {
	// //
	// // private Area area2;
	// //
	// // public SetAreaTask(Area area2) {
	// // this.area2 = area2;
	// // }
	// //
	// // @Override
	// // public void run() {
	// // area = area2;
	// // restartMapContet();
	// // }
	// // }
	//
	// private void restartMapContet() {
	// if (glView != null) {
	// ImageProvider.getInstance().invalidateAll();
	//
	// setContentView(glView);
	// }
	// }
	//
	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	// restartMapContet();
	// }
	//
	// private boolean gameWasPaused = false;
	//
	// private ResourceProvider provider;
	//
	// @Override
	// protected void onPause() {
	// super.onPause();
	// if (glView != null) {
	// glView.onPause();
	// }
	// if (state == EAndroidUIState.SHOW_ACTIVE_GAME) {
	// gameWasPaused = manager.isPaused();
	// manager.setPaused(true);
	// }
	// }
	//
	// @Override
	// protected void onResume() {
	// super.onResume();
	// if (glView != null) {
	// glView.onPause();
	// }
	// if (state == EAndroidUIState.SHOW_ACTIVE_GAME) {
	// manager.setPaused(gameWasPaused);
	// }
	// }
	//
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// if (state == EAndroidUIState.SHOW_ACTIVE_GAME) {
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.options_menu, menu);
	// return true;
	// } else {
	// return false;
	// }
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// if (glView == null || state != EAndroidUIState.SHOW_ACTIVE_GAME) {
	// return false;
	// }
	// // Handle item selection
	// switch (item.getItemId()) {
	// case R.id.f12btn:
	// glView.fireKey("F12");
	// return true;
	// case R.id.savebtn:
	// glView.fireKey("F2");
	// return true;
	// // case R.id.loadbtn:
	// // glView.fireKey("q");
	// // return true;
	// // case R.id.pausebtn:
	// // glView.fireKey("PAUSE");
	// // return true;
	// case R.id.speedup:
	// glView.fireKey("+");
	// glView.fireKey("+");
	// return true;
	// case R.id.slowdown:
	// glView.fireKey("-");
	// glView.fireKey("-");
	// return true;
	// case R.id.kill:
	// glView.fireKey("DELETE");
	// return true;
	// case R.id.stop:
	// glView.fireKey("STOP");
	// return true;
	//
	// default:
	// return super.onOptionsItemSelected(item);
	// }
	// }
	//
	// @Override
	// public void onBackPressed() {
	// if (glView != null && state == EAndroidUIState.SHOW_ACTIVE_GAME) {
	// glView.fireKey("BACK");
	// } else if (state == EAndroidUIState.SHOW_GAMELIST) {
	// showStartscreenContent();
	// } else {
	// super.onBackPressed();
	// }
	// }
	//
	// @Override
	// protected void onStop() {
	// super.onStop();
	// if (state == EAndroidUIState.SHOW_ACTIVE_GAME) {
	// String saveid = manager.saveAndStopCurrentGame();
	// if (saveid == null) {
	// saveid = "";
	// }
	//
	// setGameToResume(saveid);
	// }
	// System.exit(0);
	// }
	//
	// private void setGameToResume(String saveid) {
	// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	// Editor editor = settings.edit();
	// editor.putString("resumegame", saveid);
	// editor.commit();
	// }
	//
	// @Override
	// public ProgressConnector showProgress() {
	// return showProgress(true);
	// }
	//
	// public ProgressConnector showProgress(final boolean gameStart) {
	// runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// state = EAndroidUIState.SHOW_PROGRESS;
	//
	// if (gameStart) {
	// displayedStartScreen = null;
	// }
	//
	// setContentView(R.layout.progress);
	//
	// if (gameStart) {
	// glHolderView = (FrameLayout) findViewById(R.id.hiddenGlView);
	//
	// preloadGlView();
	// }
	// }
	// });
	// return new AProgressConnector(this);
	// }
	//
	// @Override
	// public void showStartScreen(IStartScreenConnector connector) {
	// this.displayedStartScreen = connector;
	// state = EAndroidUIState.SHOW_STARTSCREEN;
	// doStartUpdate(new Runnable() {
	// @Override
	// public void run() {
	// showStartscreenContent();
	// }
	// });
	//
	// }
	//
	// /**
	// * Starts a resource update
	// *
	// * @param run
	// * A process to be run on the UI thread when we finished. If there is no update to be done, it is run immeadiately.
	// */
	// private void doStartUpdate(final Runnable showStartscreen) {
	// if (provider.needsUpdate()) {
	// runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// ProgressConnector c = showProgress(false);
	//
	// provider.startUpdate(new UpdateListener() {
	// @Override
	// public void resourceUpdateFinished() {
	// runOnUiThread(showStartscreen);
	// }
	// }, c);
	// }
	// });
	// } else {
	// runOnUiThread(showStartscreen);
	// }
	// }
	//
	// /**
	// * Shows the start screen {@link #displayedStartScreen}
	// */
	// private void showStartscreenContent() {
	// disposeGLView();
	//
	// doStartUpdate(new Runnable() {
	// @Override
	// public void run() {
	// setContentView(R.layout.startmenu);
	//
	// // really look if there is a saved game with that name
	// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	// String name = settings.getString("resumegame", "");
	//
	// state = EAndroidUIState.SHOW_STARTSCREEN;
	//
	// if (!name.isEmpty()) {
	// findViewById(R.id.resume_game_button).setVisibility(View.INVISIBLE);
	// }
	// }
	// });
	// }
	//
	// /**
	// * Onclick listener
	// * <p>
	// * Run on UI Thread.
	// */
	// public void startGameButtonClicked(@SuppressWarnings("unused") View target) {
	// doStartUpdate(new Runnable() {
	// @Override
	// public void run() {
	// if (displayedStartScreen != null) {
	// setContentView(R.layout.maplist);
	// View root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
	// new MapList(root, displayedStartScreen, MapList.STARTMODE_SINGLE, (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE));
	// }
	// }
	// });
	// }
	//
	// public void startNetworkGameButtonClicked(@SuppressWarnings("unused") View target) {
	// doStartUpdate(new Runnable() {
	// @Override
	// public void run() {
	// if (displayedStartScreen != null) {
	// setContentView(R.layout.maplist);
	// View root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
	// new MapList(root, displayedStartScreen, MapList.STARTMODE_MULTIPLAYER, (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE));
	// }
	// }
	// });
	// }
	//
	// /**
	// * Onclick listener
	// */
	// public void resumeGameButtonClicked(@SuppressWarnings("unused") View target) {
	// doStartUpdate(new Runnable() {
	// @Override
	// public void run() {
	// if (displayedStartScreen != null) {
	// SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	// final String name = settings.getString("resumegame", "");
	// ILoadableGame foundMap = null;
	// for (ILoadableGame map : displayedStartScreen.getLoadableGames()) {
	// if (name.equals(map.getName())) {
	// foundMap = map;
	// }
	// }
	//
	// if (foundMap != null) {
	// displayedStartScreen.loadGame(foundMap);
	// } else {
	// gameStartedError();
	// }
	// }
	// }
	// });
	// }
	//
	// private void gameStartedError() {
	// Toast.makeText(this, R.string.local_game_resume_error, Toast.LENGTH_LONG).show();
	// }
	//
	// /**
	// * Onclick listener
	// */
	// public void loadGameButtonClicked(@SuppressWarnings("unused") View target) {
	// doStartUpdate(new Runnable() {
	// @Override
	// public void run() {
	// if (displayedStartScreen != null) {
	// setContentView(R.layout.maplist);
	// View root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
	// new MapList(root, displayedStartScreen, MapList.STARTMODE_LOAD_SINGLE, (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE));
	// }
	// }
	// });
	// }
	//
	// /**
	// * Hides the gl view, deletes all references. Needs not be on ui thread
	// */
	// private void disposeGLView() {
	// glView = null;
	// area = null;
	// ImageProvider.getInstance().invalidateAll();
	// }
	//
	// @Override
	// public MapInterfaceConnector showGameMap(IGraphicsGrid map, IStatisticable playerStatistics) {
	// displayedStartScreen = null;
	// state = EAndroidUIState.SHOW_ACTIVE_GAME;
	//
	// AndroidSoundPlayer player = new AndroidSoundPlayer(SOUND_THREADS);
	// FrameLayout parentView = (FrameLayout) findViewById(R.id.menuContainer);
	// final MapContent content = new MapContent(map, player, null);
	//
	// this.runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// setGameToResume("");
	//
	// Region region = new Region(Region.POSITION_CENTER);
	// region.setContent(content);
	// area.add(region);
	// preloadGlView(); // ensures the gl view exists
	// System.out.println("setting content");
	//
	// View progress = findViewById(R.id.progressAllContnet);
	// // TODO: we should use progress.setVisibility(View.GONE); after
	// // the animation
	// AlphaAnimation anim = new AlphaAnimation(1, 0f);
	// anim.setDuration(1000);
	// anim.setFillAfter(true);
	// progress.startAnimation(anim);
	// }
	// });
	// return content.getInterfaceConnector();
	// }
	//
	// public void preloadGlView() {
	// if (this.state != EAndroidUIState.SHOW_ACTIVE_GAME && state != EAndroidUIState.SHOW_PROGRESS) {
	// return;
	// }
	//
	// if (glView == null) {
	// System.out.println("generating gl view");
	// area = new Area();
	// glView = new GOSurfaceView(CopyOfJsettlersActivity.this, area);
	//
	// glHolderView.addView(glView);
	// }
	//
	// System.out.println("requesting opengl preload");
	// glView.queueEvent(new Runnable() {
	// @Override
	// public void run() {
	// System.out.println("running opengl preload");
	// ImageProvider.getInstance().runPreloadTasks(glView.getDrawContext());
	// }
	// });
	// }
	//
	// @Override
	// public void showNetworkScreen(final INetworkScreenAdapter networkScreen) {
	// doStartUpdate(new Runnable() {
	// @Override
	// public void run() {
	// disposeGLView();
	// setContentView(R.layout.networkinit);
	// View root = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
	// new NetworkView(root, networkScreen);
	// }
	// });
	// }
	//
	// @Override
	// public void showErrorMessage(final String string) {
	// runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// Toast t = Toast.makeText(CopyOfJsettlersActivity.this, string, Toast.LENGTH_LONG);
	// t.show();
	// }
	// });
	// }
}