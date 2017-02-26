package jsettlers.main.android.mainmenu.presenters.setup.playeritem;

import jsettlers.common.menu.IMultiplayerPlayer;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.android.mainmenu.views.PlayerSlotView;

/**
 * Created by tompr on 18/02/2017.
 */

public class PlayerSlotPresenter {
    private final PositionChangedListener positionChangedListener;

    private PlayerSlotView view;

    private String name;
    private boolean ready = false;
    private boolean showReadyControl = false;
    private boolean controlsEnabled = true;

    private Civilisation[] possibleCivilisations;
    private Civilisation civilisation;

    private PlayerType[] possiblePlayerTypes;
    private PlayerType playerType;

    private StartPosition[] possibleStartPositions;
    private StartPosition startPosition;

    private Team[] possibleTeams;
    private Team team;

    public PlayerSlotPresenter(PositionChangedListener positionChangedListener) {
        this.positionChangedListener = positionChangedListener;
    }

    public void bindView(PlayerSlotView view) {
        this.view = view;

        view.setName(name);
        view.setReady(ready);

        if (showReadyControl) {
            view.showReadyControl();
        } else {
            view.hideReadyControl();
        }

        if (controlsEnabled) {
            view.setControlsEnabled();
        } else {
            view.setControlsDisabled();
        }

        view.setPossibleCivilisations(possibleCivilisations);
        view.setCivilisation(civilisation);

        view.setPossibleStartPositions(possibleStartPositions);
        view.setStartPosition(startPosition);

        view.setPossibleTeams(possibleTeams);
        view.setTeam(team);

        view.setPossiblePlayerTypes(possiblePlayerTypes);
        view.setPlayerType(playerType);
    }

    public PlayerSetting getPlayerSettings() {
        return new PlayerSetting(playerType.getType(), civilisation.getType(), team.asByte());
    }

    /**
     * Civilisations
     */
    public void setPossibleCivilisations(Civilisation[] possibleCivilisations) {
        this.possibleCivilisations = possibleCivilisations;
    }

    public void setCivilisation(Civilisation civilisation) {
        this.civilisation = civilisation;
    }

    /**
     * Players types
     */
    public void setPossiblePlayerTypes(PlayerType[] ePlayerTypes) {
        this.possiblePlayerTypes = ePlayerTypes;
    }

    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }

    /**
     * Start positions
     */
    public void setPossibleStartPositions(int numberOfPlayers) {
        possibleStartPositions = new StartPosition[numberOfPlayers];
        for (byte i = 0; i < numberOfPlayers; i++) {
            possibleStartPositions[i] = new StartPosition(i);
        }
    }

    public void setStartPosition(StartPosition startPosition) {
        this.startPosition = startPosition;
        if (view != null) {
            view.setStartPosition(startPosition);
        }
    }

    public void startPositionSelected(StartPosition position) {
        positionChangedListener.positionChanged(this, this.startPosition, position);
        this.startPosition = position;
    }

    public StartPosition getStartPosition() {
        return startPosition;
    }

    public byte getPlayerId() {
        return startPosition.asByte();
    }


    /**
     * Teams
     */
    public void setPossibleTeams(int numberOfPlayers) {
        possibleTeams = new Team[numberOfPlayers];
        for (byte i = 0; i < numberOfPlayers; i++) {
            possibleTeams[i] = new Team(i);
        }
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public void teamSelected(Team team) {
        this.team = team;
    }


    /**
     * Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Ready
     */
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setShowReadyControl(boolean showReadyControl) {
        this.showReadyControl = showReadyControl;
    }


    /**
     * Controls enable
     */
    public void setControlsEnabled(boolean enabled) {
        controlsEnabled = true;
    }
}
