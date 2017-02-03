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

    public void initView() {
        view.setItems(changingMaps.getItems());
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



    public abstract void itemSelected(IMapDefinition mapDefinition);

    /**
     * ChangingListListener implementation
     */
    @Override
    public void listChanged(ChangingList<? extends IMapDefinition> list) {
        view.setItems(list.getItems());
    }
}
