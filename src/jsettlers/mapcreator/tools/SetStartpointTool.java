package jsettlers.mapcreator.tools;

import jsettlers.common.position.ISPosition2D;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.main.IPlayerSetter;
import jsettlers.mapcreator.tools.shapes.PointShape;
import jsettlers.mapcreator.tools.shapes.ShapeType;

public class SetStartpointTool implements Tool {

	private final IPlayerSetter player;

	public SetStartpointTool(IPlayerSetter player) {
		this.player = player;
    }
	
	@Override
    public String getName() {
	    return EditorLabels.getLabel("setstartpoint");
    }

	@Override
    public ShapeType[] getShapes() {
	    return new ShapeType[] {new PointShape()};
    }

	@Override
    public void apply(MapData map, ShapeType shape, ISPosition2D start,
            ISPosition2D end, double uidx) {
		map.setStartPoint(player.getActivePlayer(), end);
    }

	@Override
    public void start(MapData data, ShapeType shape, ISPosition2D pos) {
    }
	
}
