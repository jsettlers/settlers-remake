package jsettlers.main.android.mainmenu.views;

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.ECivilisation;

/**
 * Created by tompr on 18/02/2017.
 */

public interface PlayerSlotView {
    void setName(String name);

    void setPossibleCivilisations(ECivilisation[] possibleCivilisations);
    void setCivilisation(ECivilisation civilisation);

    void setPossiblePlayerTypes(EPlayerType[] ePlayerTypes);
    void setPlayerType(EPlayerType playerType);

    void setPossibleSlots(Integer[] possibleSlots);
    void setSlot(Integer slot);

    void setPossibleTeams(Integer[] possibleTeams);
    void setTeam(Integer team);
}
