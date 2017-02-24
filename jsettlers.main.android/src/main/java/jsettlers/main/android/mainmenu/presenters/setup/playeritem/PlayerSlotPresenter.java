package jsettlers.main.android.mainmenu.presenters.setup.playeritem;

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.ECivilisation;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.android.mainmenu.views.PlayerSlotView;

/**
 * Created by tompr on 18/02/2017.
 */

public class PlayerSlotPresenter {
    private final PositionChangedListener positionChangedListener;

    private PlayerSlotView view;

    private ECivilisation[] possibleCivilisations;
    private ECivilisation civilisation;

    private EPlayerType[] possiblePlayerTypes;
    private EPlayerType playerType;

    private StartPosition[] possiblePositions;
    private StartPosition position;

    private Integer[] possibleTeams;
    private Integer team;

    public PlayerSlotPresenter(PositionChangedListener positionChangedListener) {
        this.positionChangedListener = positionChangedListener;
    }

    public void bindView(PlayerSlotView view) {
        this.view = view;
        view.setName("Random");

        view.setPossibleCivilisations(possibleCivilisations);
        view.setCivilisation(civilisation);

        view.setPossibleStartPositions(possiblePositions);
        view.setStartPosition(position);

        view.setPossibleTeams(possibleTeams);
        view.setTeam(team);

        view.setPossiblePlayerTypes(possiblePlayerTypes);
        view.setPlayerType(playerType);
    }

    public PlayerSetting getPlayerSettings() {
        return new PlayerSetting(playerType, civilisation, (byte) (team - 1));
    }

    // Civilisation
    public void setPossibleCivilisations(ECivilisation[] possibleCivilisations) {
        this.possibleCivilisations = possibleCivilisations;
    }

    public void setCivilisation(ECivilisation civilisation) {
        this.civilisation = civilisation;
    }

    // Player stpe
    public void setPossiblePlayerTypes(EPlayerType[] ePlayerTypes) {
        this.possiblePlayerTypes = ePlayerTypes;
    }

    public void setPlayerType(EPlayerType playerType) {
        this.playerType = playerType;
    }

    // Position
    public void setPossiblePositions(int numberOfPlayers) {
        possiblePositions = new StartPosition[numberOfPlayers];
        for (byte i = 0; i < numberOfPlayers; i++) {
            possiblePositions[i] = new StartPosition(i);
        }
    }

    public void setPosition(StartPosition position) {
        this.position = position;
        if (view != null) {
            view.setStartPosition(position);
        }
    }

    public void positionSelected(StartPosition position) {
        positionChangedListener.positionChanged(this, this.position, position);
        this.position = position;
    }

    public StartPosition getPosition() {
        // this should be wrapped in something better than Integer, same for team
        return position;
    }

    public byte getPlayerId() {
        // this should be wrapped in something better than Integer, same for team
        return position.asByte();
    }


    // Team
    public void setPossibleTeams(int numberOfPlayers) {
        possibleTeams = new Integer[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            possibleTeams[i] = i + 1;
        }
    }

    public void setTeam(byte team) {
        this.team = (int) team + 1;
    }

    public void teamSelected(Integer team) {
        this.team = team;
    }
}
