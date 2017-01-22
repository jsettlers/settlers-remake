package jsettlers.main.android.presenters;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import jsettlers.common.menu.IJoinableGame;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.views.MapPickerView;

/**
 * Created by tompr on 22/01/2017.
 */

public abstract class MapPickerPresenter implements IChangingListListener<IMapDefinition> {
    private final MapPickerView view;
    private final GameStarter gameStarter;
    private final MainMenuNavigator navigator;
    private final ChangingList<? extends IMapDefinition> changingMaps;

    public MapPickerPresenter(MapPickerView view, GameStarter gameStarter, MainMenuNavigator navigator, ChangingList<? extends IMapDefinition> changingMaps) {
        this.view = view;
        this.gameStarter = gameStarter;
        this.navigator = navigator;

        this.changingMaps = changingMaps;
        changingMaps.setListener(this);
    }


    protected ChangingList<? extends IMapDefinition> getMaps() {
        return changingMaps;
    }

    public abstract void itemSelected(IMapDefinition mapDefinition);

    public List<? extends IMapDefinition> getItems() {
        return changingMaps.getItems();
    }

    public void abort() {
    }

    public void dispose() {
        changingMaps.removeListener(this);
    }

    /**
     * ChangingListListener implementation
     */
    @Override
    public void listChanged(ChangingList<? extends IMapDefinition> list) {
        view.setItems(list.getItems());
    }
}
