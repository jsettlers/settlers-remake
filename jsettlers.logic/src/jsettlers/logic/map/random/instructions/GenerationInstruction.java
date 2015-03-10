package jsettlers.logic.map.random.instructions;

import java.util.Hashtable;
import java.util.Random;

/**
 * This is a map instruction.
 * <p>
 * <h1>positions</h1>
 * <p>
 * Each instruction contains informations on where to perform the given changes to:
 * <p>
 * the distance parameter (a range, e.g. 10..20) tells how far the object is away from the center.
 * <p>
 * The x and y parameters, which default to 0, tell the displacement of the center seen from the players starting position.
 * <p>
 * <h1>count and size</h1>
 * <p>
 * The tight-parameter (default 0, never less) tells how many free tiles there may be between two objects that have been created by this instruction,
 * and also how much space the objects should leave to other previously generated objects..
 * <p>
 * The count parameter tells how many objects should be generated because of this instruction.
 * <h1>Generation types</h1>
 * <p>
 * The generation type states what should be generated (map objects, ..). There always has to be a type given that tells what type should be generated
 * (tree, stone, buildingtype, settlertype).
 * <p>
 * <h1>randomness</h1>
 * <p>
 * Randomness is caused by giving ranges and sets to the parameters. E.g. 10..20 is evaluated to a random value between 10 and 20, STONE|TREE is
 * either a Stone or a tree. To weight the random choice it is also possible to use a key multiple times: STONE|TREE|TREE means 33% stone and the rest
 * tree, 10..20|13..17|15 is also possible. Some values may not distribute this evenly along the whole range or may add own restrictions.
 * 
 * @author michael
 */
public abstract class GenerationInstruction {
	private final Hashtable<String, String> parameters;

	public GenerationInstruction() {
		this.parameters = new Hashtable<String, String>();
	}

	protected abstract Hashtable<String, String> getDefaultValues();

	/**
	 * Gets an evaluated parameter. The result of this method may be random.
	 * 
	 * @param name
	 *            The name
	 * @param random
	 *            A random number generator to use.
	 * @return The parameter
	 */
	public String getParameter(String name, Random random) {
		String value = parameters.get(name);
		if (value == null) {
			value = getDefaultValues().get(name);
			if (value == null) {
				value = "";
			}
		}
		return evaluateValue(value, random);
	}

	public void setParameter(String key, String value) {
		parameters.put(key, value);
	}

	private static String evaluateValue(String value, Random random) {
		String[] alternatives = value.split("\\|");
		String choosen;
		if (alternatives.length > 1) {
			choosen = alternatives[random.nextInt(alternatives.length)];
		} else {
			choosen = alternatives[0];
		}
		if (choosen.matches("\\d+\\.\\.\\d+")) {
			String[] bounds = choosen.split("\\.\\.");
			int bound1 = Integer.parseInt(bounds[0]);
			int bound2 = Integer.parseInt(bounds[1]);
			if (bound2 > bound1) {
				return "" + (bound1 + random.nextInt(bound2 - bound1 + 1));
			} else {
				return "" + bound1;
			}
		} else {
			return choosen;
		}
	}

	public <T extends Enum<T>> T getParameter(String name, Random random,
			Class<T> type) {
		try {
			return Enum.valueOf(type, getParameter(name, random).toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("The value "
					+ getParameter(name, random) + " for parameter " + name
					+ " is not compatible with the given enum type "
					+ type.getName() + ".");
		}
	}

	public int getIntParameter(String name, Random random) {
		return Integer.parseInt(getParameter(name, random));
	}

	public static GenerationInstruction createByType(String typeName) {
		String name = typeName.toLowerCase();
		if (name.equals("meta")) {
			return new MetaInstruction();
		} else if (name.equals("playerland")) {
			return new PlayerBaseInstruction();
		} else if (name.equals("building")) {
			return new BuildingInstruction();
		} else if (name.equals("settler")) {
			return new SettlerInstruction();
		} else if (name.equals("stack")) {
			return new StackInstruction();
		} else if (name.equals("object")) {
			return new PlayerObjectInstruction();
		} else if (name.equals("global")) {
			return new LandBaseInstruction();
		} else if (name.equals("playerriver")) {
			return new PlayerRiverInstruction();
		} else {
			throw new IllegalArgumentException(typeName
					+ " is no valid instruction type");
		}
	}
}
