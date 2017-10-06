package jsettlers.main.android.mainmenu.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import jsettlers.common.utils.collections.ChangingList;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.utils.SingleLiveEvent;

/**
 * Created by Tom Pratt on 06/10/2017.
 */

public class LoadSinglePlayerPickerViewModel extends MapPickerViewModel {

    private final GameStarter gameStarter;
    private final SingleLiveEvent<Void> mapSelectedEvent = new SingleLiveEvent<>();
    private final LiveData<Boolean> showNoMapsMessage;

    public LoadSinglePlayerPickerViewModel(GameStarter gameStarter, ChangingList<? extends MapLoader> changingMaps) {
        super(gameStarter, changingMaps);
        this.gameStarter = gameStarter;

        showNoMapsMessage = Transformations.map(getMaps(), maps -> maps.length == 0);
    }

    @Override
    public void selectMap(MapLoader map) {
        MapFileHeader mapFileHeader = map.getFileHeader();
        PlayerSetting[] playerSettings = mapFileHeader.getPlayerSettings();
        byte playerId = mapFileHeader.getPlayerId();
        JSettlersGame game = new JSettlersGame(map, 4711L, playerId, playerSettings);
        gameStarter.setStartingGame(game.start());
        mapSelectedEvent.call();
    }

    public LiveData<Void> getMapSelectedEvent() {
        return mapSelectedEvent;
    }

    public LiveData<Boolean> getShowNoMapsMessage() {
        return showNoMapsMessage;
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
            if (modelClass == LoadSinglePlayerPickerViewModel.class) {
                return (T) new LoadSinglePlayerPickerViewModel(gameStarter, gameStarter.getMapList().getSavedMaps());
            }
            throw new RuntimeException("NewSinglePlayerPickerViewModel.Factory doesn't know how to create a: " + modelClass.toString());
        }
    }
}
