package jsettlers.main.android.activities;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.graphics.androidui.menu.IFragmentHandler;
import jsettlers.graphics.map.MapContent;
import jsettlers.main.android.MainApplication;
import jsettlers.main.android.R;
import jsettlers.main.android.fragmentsnew.MapFragment;
import jsettlers.main.android.fragmentsnew.LoadingFragment;
import jsettlers.main.android.navigation.GameNavigator;
import jsettlers.main.android.providers.GameProvider;

public class GameActivity extends AppCompatActivity implements GameNavigator, GameProvider, IFragmentHandler {

    private MainApplication mainApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mainApplication = (MainApplication)getApplication();

        if (savedInstanceState != null)
            return;

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_layout, LoadingFragment.newInstance())
                .commit();
    }

    /**
     * GameProvider
     * @return
     */
    @Override
    public IStartingGame getStartingGame() {
        return mainApplication.getStartingGame();
    }

    @Override
    public IMapInterfaceConnector loadFinished(IStartedGame game) {
        return mainApplication.gameStarted(game, this);
    }

    @Override
    public MapContent getMapContent() {
        return mainApplication.getMapContent();
    }

    /**
     * GameNavigator
     * @return
     */
    @Override
    public void showGame() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, MapFragment.newInstance())
                .commit();
    }

    /**
     * IFragmentHandler
     * @return
     */
    @Override
    public void showMenuFragment(Fragment fragment) {

    }

    @Override
    public void hideMenu() {

    }
}
