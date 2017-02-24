package jsettlers.main.android.mainmenu.presenters.setup.playeritem;

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.ECivilisation;
import jsettlers.main.android.mainmenu.views.PlayerSlotView;

/**
 * Created by tompr on 18/02/2017.
 */

public class PlayerSlotPresenter {

    private PlayerSlotView view;

    private EPlayerType[] possiblePlayerTypes;
    private EPlayerType playerType;

    private Integer[] possibleSlots;
    private Integer slot;

    private Integer[] possibleTeams;
    private Integer team;

    public PlayerSlotPresenter() {
    }

    public void bindView(PlayerSlotView view) {
        this.view = view;
        view.setName("Random");

        view.setPossibleSlots(possibleSlots);
        view.setSlot(slot);

        view.setPossibleTeams(possibleTeams);
        view.setTeam(team);

        view.setPossiblePlayerTypes(possiblePlayerTypes);
        view.setPlayerType(playerType);
    }

    public void setPossiblePlayerTypes(EPlayerType[] ePlayerTypes) {
        this.possiblePlayerTypes = ePlayerTypes;
    }

    public void setPlayerType(EPlayerType playerType) {
        this.playerType = playerType;
    }

    public void setCivilisation(ECivilisation civilisation) {

    }

    public void setPossibleSlots(int numberOfPlayers) {
        possibleSlots = new Integer[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            possibleSlots[i] = i + 1;
        }
    }

    public void setSlot(byte slot) {
        this.slot = Integer.valueOf(slot) + 1;
    }

    public void setPossibleTeams(int numberOfPlayers) {
        possibleTeams = new Integer[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            possibleTeams[i] = i + 1;
        }
    }

    public void setTeam(Byte team) {
        this.team = Integer.valueOf(team) + 1;
    }
}
