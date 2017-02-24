package jsettlers.main.android.mainmenu.presenters.setup.playeritem;

import jsettlers.common.ai.EPlayerType;

/**
 * Created by tompr on 24/02/2017.
 */

public class PlayerType {
    private final EPlayerType type;

    public PlayerType(EPlayerType type) {
        this.type = type;
    }

    public EPlayerType getType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PlayerType && ((PlayerType)obj).getType() == type;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
