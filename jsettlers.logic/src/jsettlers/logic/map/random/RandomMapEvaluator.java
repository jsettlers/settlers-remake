package jsettlers.logic.map.random;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import jsettlers.common.map.IMapData;
import jsettlers.logic.map.random.generation.PlayerConnection;
import jsettlers.logic.map.random.generation.PlayerStart;
import jsettlers.logic.map.random.grid.MapGrid;
import jsettlers.logic.map.random.instructions.GenerationInstruction;
import jsettlers.logic.map.random.instructions.LandInstruction;
import jsettlers.logic.map.random.instructions.MetaInstruction;
import jsettlers.logic.map.random.instructions.ObjectInstruction;
import jsettlers.logic.map.random.landscape.LandscapeMesh;
import jsettlers.logic.map.random.landscape.MeshEdge;
import jsettlers.logic.map.random.landscape.MeshLandscapeType;
import jsettlers.logic.map.random.landscape.MeshSite;
import jsettlers.logic.map.random.landscape.Vertex;
import jsettlers.logic.map.random.settings.MapSettings;
import jsettlers.logic.map.random.settings.PlayerSetting;

/**
 * <h1>generation progress</h1>
 * <p>
 * The generation progress has 4 steps:
 * </p>
 * <ol>
 * <li>Set the base land type, map size, player positions (meta)</li>
 * <li>Set all the landmark types and areas, add rivers, set heights</li>
 * <li>Set all map objects: Trees, stones, players, ... the order they are defined.</li>
 * <li>Final cleanup. This step cannot be influenced, it just converts the temporary data, adds mountain and sea borders, ...</li>
 * </ol>
 * 
 * @author michael
 */
public class RandomMapEvaluator {
	private List<LandInstruction> landInstructions =
			new ArrayList<LandInstruction>();

	private GenerationInstruction metaInstruction = new MetaInstruction();

	private List<ObjectInstruction> objectInstructions =
			new ArrayList<ObjectInstruction>();

	private Random random;

	private int width;

	private int height;

	private PlayerStart[] playerStarts;

	private final MapSettings settings;

	private IMapData grid;

	public RandomMapEvaluator(List<GenerationInstruction> instructions,
			int players) {
		this.settings = new MapSettings(players);
		for (GenerationInstruction instruction : instructions) {
			if (instruction instanceof LandInstruction) {
				landInstructions.add((LandInstruction) instruction);
			} else if (instruction instanceof MetaInstruction) {
				metaInstruction = instruction;
			} else if (instruction instanceof ObjectInstruction) {
				objectInstructions.add((ObjectInstruction) instruction);
			}
		}
	}

	public void createMap(Random random) {
		this.random = random;
		createBase();

		LandscapeMesh landscapeMesh = createLandscapeMesh();

		addLandscapes(landscapeMesh);

		computeHeights(landscapeMesh);

		MapGrid grid = MapGrid.createFromLandscapeMesh(landscapeMesh, random, playerStarts);

		addObjects(grid);

		this.grid = grid;
	}

	private static void computeHeights(LandscapeMesh landscapeMesh) {
		PriorityQueue<Vertex> settled = new PriorityQueue<Vertex>();

		for (MeshSite site : landscapeMesh.getSites()) {
			if (site.getLandscape() == MeshLandscapeType.SEA) {
				for (MeshEdge edge : site.getEdges()) {
					Vertex setVertex = edge.getClockPoint(site);
					setVertex.setHeight(0);
					settled.add(setVertex);
				}
			}
		}

		settleAll(settled);

		while (true) {
			Vertex unsettled = getUnsettled(landscapeMesh);
			if (unsettled == null) {
				break;
			}

			unsettled.setHeight(0);
			settled.add(unsettled);
			settleAll(settled);
		}
	}

	private static Vertex getUnsettled(LandscapeMesh landscapeMesh) {
		for (MeshEdge edge : landscapeMesh.getEdges()) {
			if (edge.getEnd().getHeight() == Float.POSITIVE_INFINITY) {
				return edge.getEnd();
			}
			if (edge.getStart().getHeight() == Float.POSITIVE_INFINITY) {
				return edge.getStart();
			}
		}
		return null;
	}

	private static void settleAll(PriorityQueue<Vertex> settled) {
		while (!settled.isEmpty()) {
			Vertex vertex = settled.poll();
			for (MeshEdge edge : vertex.getEdges()) {
				Vertex newVertex = edge.getOppositePoint(vertex);
				float newHeight = vertex.getHeight() + edge.getMaxIncline();
				if (newVertex.getHeight() > newHeight) {
					newVertex.setHeight(newHeight);
					settled.add(newVertex);
				}
			}
		}
	}

	private void addObjects(MapGrid grid) {
		for (ObjectInstruction instruction : objectInstructions) {
			instruction.execute(grid, playerStarts, random);
		}
	}

	private LandscapeMesh createLandscapeMesh() {
		return LandscapeMesh.getRandomMesh(width, height,
				new Random(random.nextLong()));
	}

	private void createBase() {
		width = metaInstruction.getIntParameter("width", random);
		height = metaInstruction.getIntParameter("height", random);

		int players = settings.getPlayers().size();
		createPlayerStarts(players);

		connectPlayers(players);
	}

	private void createPlayerStarts(int players) {
		playerStarts = new PlayerStart[players];
		int i = 0;
		for (PlayerSetting playerSetting : settings.getPlayers()) {
			double alpha = (double) i / players * 2 * Math.PI;
			double x = width / 2 + Math.sin(alpha) * width / 3;
			double y = height / 2 + Math.cos(alpha) * height / 3;
			playerStarts[i] =
					new PlayerStart((short) x, (short) y,
							playerSetting.getPlayerId(),
							playerSetting.getAlliance());
			i++;
		}
	}

	private void connectPlayers(int players) {
		PlayerConnection[] playerConnections;
		if (players > 1) {
			playerConnections = new PlayerConnection[players];
			for (int j = 0; j < players; j++) {
				playerConnections[j] =
						new PlayerConnection(playerStarts[j],
								playerStarts[(j + 1) % players]);
			}
		} else {
			playerConnections = new PlayerConnection[0];
		}
	}

	/**
	 * The real landscape adding
	 * 
	 * @param landscapeMesh
	 */
	private void addLandscapes(LandscapeMesh landscapeMesh) {
		for (LandInstruction instruction : landInstructions) {
			instruction.execute(landscapeMesh, playerStarts, random);
		}
	}

	public IMapData getGrid() {
		if (grid == null) {
			throw new IllegalStateException("Grid was not created");
		}
		return grid;
	}

}
