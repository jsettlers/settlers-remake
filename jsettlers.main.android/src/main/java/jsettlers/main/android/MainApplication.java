package jsettlers.main.android;

import android.app.Application;

import go.graphics.android.AndroidSoundPlayer;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.graphics.androidui.MobileControls;
import jsettlers.graphics.androidui.menu.AndroidMenuPutable;
import jsettlers.graphics.androidui.menu.IFragmentHandler;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.main.StartScreenConnector;



public class MainApplication extends Application {
	private static final int SOUND_THREADS = 6;

	private IStartingGame startingGame;
	private MapContent mapContent;

	@Override
	public void onCreate() {
		super.onCreate();
		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
	}

//	public void startSinglePlayerGame(IMapDefinition mapDefinition) {
//		startingGame = new StartScreenConnector().startSingleplayerGame(mapDefinition);
//	}
//
//	public IStartingGame getStartingGame() {
//		return startingGame;
//	}
//
//	public MapInterfaceConnector gameStarted(IStartedGame game, IFragmentHandler fragmentHandler) {
//		// startingGame == null ??????
//
//		AndroidSoundPlayer soundPlayer = new AndroidSoundPlayer(SOUND_THREADS);
//		mapContent = new MapContent(game, soundPlayer, new MobileControls(new AndroidMenuPutable(this, fragmentHandler)));
//
//		// game.setGameExitListener(this);
//
//		return mapContent.getInterfaceConnector();
//	}
//
//	public MapContent getMapContent() {
//		return mapContent;
//	}
}
