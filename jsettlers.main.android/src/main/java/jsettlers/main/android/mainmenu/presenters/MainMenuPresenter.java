package jsettlers.main.android.mainmenu.presenters;

import jsettlers.main.android.core.GameManager;
import jsettlers.main.android.core.resources.scanner.ResourceLocationScanner;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.views.MainMenuView;

/**
 * Created by tompr on 04/03/2017.
 */

public class MainMenuPresenter {
    private final MainMenuView view;
    private final MainMenuNavigator navigator;
    private final GameManager gameManager;
    private final ResourceLocationScanner resourceLocationScanner;

    private boolean resourcesLoaded;

    public MainMenuPresenter(
            MainMenuView view,
            MainMenuNavigator navigator,
            GameManager gameManager,
            ResourceLocationScanner resourceLocationScanner) {

        this.view = view;
        this.navigator = navigator;
        this.gameManager = gameManager;
        this.resourceLocationScanner = resourceLocationScanner;
        this.resourcesLoaded = resourceLocationScanner.scanForResources();
    }

    public void bindView() {
        if (!resourcesLoaded) {
            view.showResourcePicker();
        }
    }

    public void newSinglePlayerSelected() {
        if (resourcesLoaded) {
            navigator.showNewSinglePlayerPicker();
        }
    }

    public void loadSinglePlayerSelected() {
        if (resourcesLoaded) {
            navigator.showLoadSinglePlayerPicker();
        }
    }

    public void newMultiPlayerSelected() {
        if (resourcesLoaded) {
            navigator.showNewMultiPlayerPicker();
        }
    }

    public void joinMultiPlayerSelected() {
        if (resourcesLoaded) {
            navigator.showJoinMultiPlayerPicker();
        }
    }

    public void resumeSelected() {
        navigator.resumeGame();
    }

    public void quitSelected() {
        if (gameManager.getGameMenu().canQuitConfirm()) {
            gameManager.getGameMenu().quitConfirm();
        } else {
            gameManager.getGameMenu().quit();
        }
    }

    public void pauseSelected() {
        if (gameManager.getGameMenu().isPaused()) {
            gameManager.getGameMenu().unPause();
        } else {
            gameManager.getGameMenu().pause();
        }
        updateResumeGameView();
    }

    public void updateResumeGameView() {
        if (gameManager.isGameInProgress()) {
            view.updatePauseButton(gameManager.getGameMenu().isPaused());
            view.updateQuitButton(gameManager.getGameMenu().canQuitConfirm());
            view.showResumeGameView();
        } else {
            view.hideResumeGameView();
        }
    }

    public void resourceDirectoryChosen() {
        resourcesLoaded = resourceLocationScanner.scanForResources();
        if (resourcesLoaded) {
            view.hideResourcePicker();
        } else {
            throw new RuntimeException("Resources not found or not valid after directory chosen by user");
        }
    }
}
