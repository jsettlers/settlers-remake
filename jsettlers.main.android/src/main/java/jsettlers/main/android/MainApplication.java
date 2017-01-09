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
	@Override
	public void onCreate() {
		super.onCreate();
		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
	}
}
