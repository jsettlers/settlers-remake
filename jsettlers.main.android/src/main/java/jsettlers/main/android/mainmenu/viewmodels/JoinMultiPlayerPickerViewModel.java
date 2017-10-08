package jsettlers.main.android.mainmenu.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import java.util.List;

import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoinableGame;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IJoiningGameListener;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.viewstates.JoiningViewState;
import jsettlers.main.android.utils.SingleLiveEvent;

import static java8.util.stream.StreamSupport.stream;

/**
 * Created by Tom Pratt on 06/10/2017.
 */

public class JoinMultiPlayerPickerViewModel extends ViewModel implements IJoiningGameListener {

    private final GameStarter gameStarter;
    private final ChangingList<IJoinableGame> changingJoinableGames;

    private final JoinableGamesData joinableGames = new JoinableGamesData();
    private final SingleLiveEvent<String> mapSelectedEvent = new SingleLiveEvent<>();
    private final MutableLiveData<JoiningViewState> joiningState = new MutableLiveData<>();
    private final LiveData<Boolean> showNoGamesMessage;

    private IJoiningGame joiningGame;
    private IMapDefinition mapDefinition;

    public JoinMultiPlayerPickerViewModel(GameStarter gameStarter, ChangingList<IJoinableGame> changingJoinableGames) {
        this.gameStarter = gameStarter;
        this.changingJoinableGames = changingJoinableGames;

        showNoGamesMessage = Transformations.map(joinableGames, joinableGames -> joinableGames.length == 0);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (joiningGame != null) {
            joiningGame.setListener(null);
        }

        if (gameStarter.getStartingGame() == null) {
            abort();
        }
    }

    /**
     * IJoiningGameListener imeplementation
     */
    @Override
    public void joinProgressChanged(EProgressState state, float progress) {
        String stateString = Labels.getProgress(state);
        int progressPercentage = (int) (progress * 100);

        joiningState.postValue(new JoiningViewState(stateString, progressPercentage));
    }

    @Override
    public void gameJoined(IJoinPhaseMultiplayerGameConnector connector) {
        joiningGame.setListener(null);
        gameStarter.setJoiningGame(null);
        joiningState.postValue(null);

        gameStarter.setJoinPhaseMultiPlayerConnector(connector);
        mapSelectedEvent.postValue(mapDefinition.getMapId());
    }

    public void joinableGameSelected(IJoinableGame joinableGame) {
        abort();
        mapDefinition = joinableGame.getMap();

        joiningGame = gameStarter.getMultiPlayerConnector().joinMultiplayerGame(joinableGame);
        joiningGame.setListener(this);

        gameStarter.setJoiningGame(joiningGame);
    }

    public LiveData<IJoinableGame[]> getJoinableGames() {
        return joinableGames;
    }

    public LiveData<String> getMapSelectedEvent() {
        return mapSelectedEvent;
    }

    public LiveData<Boolean> getShowNoGamesMessage() {
        return showNoGamesMessage;
    }

    public LiveData<JoiningViewState> getJoiningState() {
        return joiningState;
    }

    private void abort() {
        if (joiningGame != null) {
            joiningGame.abort();
        }
        gameStarter.setJoiningGame(null);
        gameStarter.closeMultiPlayerConnector();
        joiningGame = null;
        mapDefinition = null;
    }



    /**
     * Maps list live data
     */
    class JoinableGamesData extends LiveData<IJoinableGame[]> implements IChangingListListener<IJoinableGame> {

        @Override
        protected void onActive() {
            super.onActive();
            changingJoinableGames.setListener(this);
            setValue(sortedMaps(changingJoinableGames.getItems()));
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            changingJoinableGames.removeListener(this);
        }

        @Override
        public void listChanged(ChangingList<? extends IJoinableGame> list) {
            postValue(sortedMaps(list.getItems()));
        }

        private IJoinableGame[] sortedMaps(List<? extends IJoinableGame> items) {
            return stream(items)
                    .toArray(IJoinableGame[]::new);
        }
    }

    /**
     * ViewModel factory
     */
    public static class Factory implements ViewModelProvider.Factory {

        private final Activity activity;
        private final GameStarter gameStarter;

        public Factory(Activity activity) {
            this.activity = activity;
            gameStarter = (GameStarter) activity.getApplication();
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass == JoinMultiPlayerPickerViewModel.class) {
                return (T) new JoinMultiPlayerPickerViewModel(gameStarter, gameStarter.getMultiPlayerConnector().getJoinableMultiplayerGames());
            }
            throw new RuntimeException("NewSinglePlayerPickerViewModel.Factory doesn't know how to create a: " + modelClass.toString());
        }
    }
}
