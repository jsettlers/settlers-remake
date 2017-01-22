package jsettlers.main.android.menus.mainmenu;

import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IJoiningGameListener;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IOpenMultiplayerGameInfo;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.views.NewMultiPlayerSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerSetupMenu extends MapSetupMenu implements IJoiningGameListener {
    private final NewMultiPlayerSetupView view;
    private final IJoiningGame joiningGame;

    private IJoinPhaseMultiplayerGameConnector joinPhaseMultiplayerGameConnector;

    public NewMultiPlayerSetupMenu(NewMultiPlayerSetupView view, GameStarter gameStarter, String mapId) {
        super(gameStarter, findMap(gameStarter.getStartScreen().getMultiplayerMaps(), mapId));
        this.view = view;

        joiningGame = getGameStarter().getMultiPlayerConnector().openNewMultiplayerGame(new IOpenMultiplayerGameInfo() {
            @Override
            public String getMatchName() {
                return "test name";
            }

            @Override
            public IMapDefinition getMapDefinition() {
                return NewMultiPlayerSetupMenu.this.getMapDefinition();
            }

            @Override
            public int getMaxPlayers() {
                return NewMultiPlayerSetupMenu.this.getMapDefinition().getMaxPlayers();
            }
        });

        joiningGame.setListener(this);
    }

    @Override
    public void startGame() {

//        IStartingGame startingGame = getGameStarter().get
//        getGameStarter().setStartingGame(startingGame);
    }

    @Override
    public void dispose() {
        super.dispose();
        joiningGame.setListener(null);
    }

    public void abort() {
        joiningGame.abort();
    }


    /**
     * IJoiningGameListener implementation
     */
    @Override
    public void joinProgressChanged(EProgressState state, float progress) {
        // This doesnt happen when you are the game creator

//        String stateString = Labels.getProgress(state);
//        int progressPercentage = (int) (progress * 100);
//
//        view.setJoiningProgress(stateString, progressPercentage);
    }

    @Override
    public void gameJoined(IJoinPhaseMultiplayerGameConnector connector) {
        joinPhaseMultiplayerGameConnector = connector;

        // subscribe to player change events and update view
        connector.getPlayers();
    }
}
