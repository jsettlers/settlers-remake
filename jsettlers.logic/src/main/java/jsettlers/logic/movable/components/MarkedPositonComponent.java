package jsettlers.logic.movable.components;

import jsettlers.common.position.ShortPoint2D;

/**
 * @author homoroselaps
 */
public class MarkedPositonComponent extends Component {
    private static final long serialVersionUID = -3582535279041109009L;

    private ShortPoint2D markedPosition;

    @Override
    protected void onDestroy() {
        clearMark();
    }

    public void setMark(ShortPoint2D position) {
        clearMark();
        markedPosition = position;
        entity.gameFieldComponent().movableGrid.setMarked(position, true);
    }

    public void clearMark() {
        if (markedPosition != null) {
            entity.gameFieldComponent().movableGrid.setMarked(markedPosition, false);
            markedPosition = null;
        }
    }
}
