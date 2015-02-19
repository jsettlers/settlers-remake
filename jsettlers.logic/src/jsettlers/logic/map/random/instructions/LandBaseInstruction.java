package jsettlers.logic.map.random.instructions;

import java.util.Hashtable;
import java.util.Random;

import jsettlers.logic.map.random.generation.PlayerStart;
import jsettlers.logic.map.random.landscape.HillPolicy;
import jsettlers.logic.map.random.landscape.LandscapeMesh;
import jsettlers.logic.map.random.landscape.MeshLandscapeType;
import jsettlers.logic.map.random.landscape.MeshSite;

/**
 * Creates a base land everywhere.
 * <p>
 * Only needs to be used once
 * 
 * @author michael
 */
public class LandBaseInstruction extends LandInstruction {
	private static Hashtable<String, String> defaults =
			new Hashtable<String, String>();

	static {
		defaults.put("type", "grass");
		defaults.put("on", "");
	}

	@Override
	public void execute(LandscapeMesh landscape, PlayerStart[] starts,
			Random random) {
		MeshLandscapeType onLandscape =
				MeshLandscapeType.parse(getParameter("on", random), null);
		MeshLandscapeType type =
				getParameter("type", random, MeshLandscapeType.class);
		for (MeshSite site : landscape.getSites()) {
			if (!site.isFixed()
					&& (onLandscape == null || site.getLandscape() == onLandscape)) {
				site.setLandscape(type, HillPolicy.HILLY);
			}
		}
	}

	@Override
	protected Hashtable<String, String> getDefaultValues() {
		return defaults;
	}

}
