package jsettlers.newmain.datatypes;

import jsettlers.graphics.startscreen.interfaces.IJoinableGame;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.logic.map.save.MapList;
import networklib.common.packets.MatchInfoPacket;

/**
 * This is a simple POJO implementing the {@link IJoinableGame} interface.
 * 
 * @author Andreas Eberle
 * 
 */
public class JoinableGame implements IJoinableGame {

	private String id;
	private String name;
	private IMapDefinition map;

	public JoinableGame(MatchInfoPacket matchInfo) {
		this.id = matchInfo.getId();
		this.name = matchInfo.getMatchName();
		this.map = new MapDefinition(MapList.getDefaultList().getMapById(matchInfo.getMapInfo().getId()));
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IMapDefinition getMap() {
		return map;
	}
}
