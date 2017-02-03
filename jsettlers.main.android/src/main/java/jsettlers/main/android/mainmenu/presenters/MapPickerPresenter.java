package jsettlers.main.android.mainmenu.presenters;

import java.util.List;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.views.MapPickerView;

/**
 * Created by tompr on 22/01/2017.
 */

public abstract class MapPickerPresenter implements IChangingListListener<IMapDefinition> {
    private final MapPickerView view;
    private final GameStarter gameStarter;
    private final MainMenuNavigator navigator;
    private final ChangingList<? extends IMapDefinition> changingMaps;

    public MapPickerPresenter(MapPickerView view, MainMenuNavigator navigator, GameStarter gameStarter, ChangingList<? extends IMapDefinition> changingMaps) {
        this.view = view;
        this.gameStarter = gameStarter;
        this.navigator = navigator;
        this.changingMaps = changingMaps;

        changingMaps.setListener(this);
    }

    public void viewFinished() {
        if (gameStarter.getStartingGame() == null) {
            abort();
        }
    }

    protected void abort() {
    }

    public void dispose() {
        changingMaps.removeListener(this);
    }



    protected ChangingList<? extends IMapDefinition> getMaps() {
        return changingMaps;
    }

    public abstract void itemSelected(IMapDefinition mapDefinition);

    public List<? extends IMapDefinition> getItems() {
        return changingMaps.getItems();
    }

    /**
     * ChangingListListener implementation
     */
    @Override
    public void listChanged(ChangingList<? extends IMapDefinition> list) {
        view.setItems(list.getItems());
    }
}
