package jsettlers.main.android;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

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

public class GameService extends Service {
    private static final int SOUND_THREADS = 6;

    private IStartingGame startingGame;
    private MapContent mapContent;

    private GameBinder gameBinder = new GameBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return gameBinder;
    }

    public boolean isGameInProgress() {
        return startingGame != null || mapContent != null;
    }

    public void startSinglePlayerGame(IMapDefinition mapDefinition) {
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("TutorialsFace Music Player")
                .setTicker("TutorialsFace Music Player")
                .setContentText("My song")
                .build();

        startForeground(100, notification);

        startingGame = new StartScreenConnector().startSingleplayerGame(mapDefinition);
    }

    public IStartingGame getStartingGame() {
        return startingGame;
    }

    public MapInterfaceConnector gameStarted(IStartedGame game, IFragmentHandler fragmentHandler) {
        // startingGame == null ??????

        AndroidSoundPlayer soundPlayer = new AndroidSoundPlayer(SOUND_THREADS);
        mapContent = new MapContent(game, soundPlayer, new MobileControls(new AndroidMenuPutable(this, fragmentHandler)));

        // game.setGameExitListener(this);

        return mapContent.getInterfaceConnector();
    }

    public MapContent getMapContent() {
        return mapContent;
    }

    public class GameBinder extends Binder {
        public GameService getService() {
            return GameService.this;
        }
    }
}
