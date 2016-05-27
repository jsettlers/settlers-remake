package jsettlers.main.android.providers;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;

/**
 * Created by tingl on 27/05/2016.
 */
public interface GameStarter {
    ChangingList<? extends IMapDefinition> getSinglePlayerMaps();
}
