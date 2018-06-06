package jsettlers.main.android.mainmenu.viewmodels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import java.io.File;

import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import jsettlers.main.android.core.GameManager;
import jsettlers.main.android.core.controls.GameMenu;
import jsettlers.main.android.core.resources.scanner.AndroidResourcesLoader;
import jsettlers.main.android.utils.SingleLiveEvent;

/**
 * Created by Tom Pratt on 02/10/2017.
 */

public class MainMenuViewModel extends ViewModel {

    private final GameManager gameManager;
    private final AndroidResourcesLoader androidResourcesLoader;

    private final ResumeStateData resumeStateData = new ResumeStateData();
    private final MutableLiveData<Boolean> areResourcesLoaded = new MutableLiveData<>();
    private final SingleLiveEvent<Void> showSinglePlayer = new SingleLiveEvent<>();
    private final SingleLiveEvent<Void> showLoadSinglePlayer = new SingleLiveEvent<>();
    private final SingleLiveEvent<Void> showMultiplayerPlayer = new SingleLiveEvent<>();
    private final SingleLiveEvent<Void> showJoinMultiplayerPlayer = new SingleLiveEvent<>();

    public MainMenuViewModel(GameManager gameManager, AndroidResourcesLoader androidResourcesLoader) {
        this.gameManager = gameManager;
        this.androidResourcesLoader = androidResourcesLoader;

        areResourcesLoaded.setValue(androidResourcesLoader.setup());
    }

    public LiveData<ResumeViewState> getResumeState() {
        return resumeStateData;
    }

    public MutableLiveData<Boolean> getAreResourcesLoaded() {
        return areResourcesLoaded;
    }

    public LiveData<Void> getShowSinglePlayer() {
        return showSinglePlayer;
    }

    public LiveData<Void> getShowLoadSinglePlayer() {
        return showLoadSinglePlayer;
    }

    public LiveData<Void> getShowMultiplayerPlayer() {
        return showMultiplayerPlayer;
    }

    public LiveData<Void> getShowJoinMultiplayerPlayer() {
        return showJoinMultiplayerPlayer;
    }

    public void resourceDirectoryChosen(File resourceDirectory) {
        androidResourcesLoader.setResourcesDirectory(resourceDirectory.getAbsolutePath());

        Disposable resourceSetupSubscription = androidResourcesLoader.setupSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    areResourcesLoaded.postValue(true);
                });
    }
    public void quitSelected() {
        if (gameManager.getGameMenu().getGameState().getValue() == GameMenu.GameState.CONFIRM_QUIT) {
            gameManager.getGameMenu().quitConfirm();
        } else {
            gameManager.getGameMenu().quit();
        }
    }

    public void pauseSelected() {
        if (gameManager.getGameMenu().isPausedState().getValue()) {
            gameManager.getGameMenu().unPause();
        } else {
            gameManager.getGameMenu().pause();
        }
    }

    public void newSinglePlayerSelected() {
        if (areResourcesLoaded.getValue() == Boolean.TRUE) {
            showSinglePlayer.call();
        }
    }

    public void loadSinglePlayerSelected() {
        if (areResourcesLoaded.getValue() == Boolean.TRUE) {
            showLoadSinglePlayer.call();
        }
    }

    public void newMultiPlayerSelected() {
        if (areResourcesLoaded.getValue() == Boolean.TRUE) {
            showMultiplayerPlayer.call();
        }
    }

    public void joinMultiPlayerSelected() {
        if (areResourcesLoaded.getValue() == Boolean.TRUE) {
            showJoinMultiplayerPlayer.call();
        }
    }


    /**
     * ViewState for resume state
     */
    public static class ResumeViewState {
        private final boolean isPaused;
        private final boolean confirmQuit;

        public ResumeViewState(boolean isPaused, boolean confirmQuit) {
            this.isPaused = isPaused;
            this.confirmQuit = confirmQuit;
        }

        public boolean isPaused() {
            return isPaused;
        }

        public boolean isConfirmQuit() {
            return confirmQuit;
        }
    }


    /**
     * LiveData for resume state
     * It monitors the current GameManager and relays state changes
     */
    private class ResumeStateData extends MediatorLiveData<ResumeViewState> {
        private GameMenu gameMenu;

        @Override
        protected void onActive() {
            super.onActive();
            if (gameManager.isGameInProgress()) {
                GameMenu newGameMenu = gameManager.getGameMenu();

                if (gameMenu == newGameMenu) {
                    return;
                }

                if (gameMenu != null) {
                    removeSource(gameMenu.isPausedState());
                    removeSource(gameMenu.getGameState());
                }

                gameMenu = newGameMenu;
                if (gameMenu != null) {
                    addSource(gameMenu.isPausedState(), paused -> update());
                    addSource(gameMenu.getGameState(), state -> update());
                } else {
                    setValue(null);
                }
            } else {
                setValue(null);
                gameMenu = null;
            }
        }

        private void update() {
            if (gameMenu.getGameState().getValue() == GameMenu.GameState.QUITTED) {
                setValue(null);
            } else {
                boolean paused = gameMenu.isPausedState().getValue();
                boolean confirmQuit = gameMenu.getGameState().getValue() == GameMenu.GameState.CONFIRM_QUIT;
                setValue(new ResumeViewState(paused, confirmQuit));
            }
        }
    }

    /**
     * ViewModel factory
     */
    public static class Factory implements ViewModelProvider.Factory {

        private final Application application;
        private final GameManager gameManager;

        public Factory(Application application) {
            this.application = application;
            gameManager = (GameManager)application;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass == MainMenuViewModel.class) {
                return (T) new MainMenuViewModel(
                        gameManager,
                        new AndroidResourcesLoader(application));
            }
            throw new RuntimeException("MainMenuViewModel.Factory doesn't know how to create a: " + modelClass.toString());
        }
    }
}
