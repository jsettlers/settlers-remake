package jsettlers.main.android.mainmenu.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import jsettlers.main.android.core.GameManager;
import jsettlers.main.android.core.controls.GameMenu;

/**
 * Created by Tom Pratt on 02/10/2017.
 */

public class MainMenuViewModel extends ViewModel {

    private final GameManager gameManager;

    private final ResumeStateData resumeStateData = new ResumeStateData();

    public MainMenuViewModel(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public LiveData<ResumeViewState> getResumeState() {
        return resumeStateData;
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

        private final GameManager gameManager;

        public Factory(Activity activity) {
            gameManager = (GameManager)activity.getApplication();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass == MainMenuViewModel.class) {
                return (T) new MainMenuViewModel(gameManager);
            }
            throw new RuntimeException("MainMenuViewModel.Factory doesn't know how to create a: " + modelClass.toString());
        }
    }
}
