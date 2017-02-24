package jsettlers.main.android.mainmenu.views;

import jsettlers.common.ai.EPlayerType;

/**
 * Created by tompr on 18/02/2017.
 */

public interface PlayerSlotView {
    void setName(String name);
    void setPossiblePlayerTypes(EPlayerType[] ePlayerTypes);

    void setPlayerType(EPlayerType playerType);

    void setPossibleSlots(Integer[] possibleSlots);

    void setSlot(Integer slot);

    void setPossibleTeams(Integer[] possibleTeams);

    void setTeam(Integer team);
}
