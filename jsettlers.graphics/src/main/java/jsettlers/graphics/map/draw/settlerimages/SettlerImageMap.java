/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.map.draw.settlerimages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.player.ECivilisation;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.draw.ImageProvider;

/**
 * This is a settler image map that mapps the state of a settler to the sequence that is to be played.
 * <p>
 * The mapping is a function: (type, material, direction) => (file, sequence index, start, duration)
 *
 * @author michael
 */
public final class SettlerImageMap {

	private static final SettlerImageMapItem DEFAULT_ITEM = new SettlerImageMapItem(10, 0, 0, 1);

	private static SettlerImageMap instance;

	private final ImageProvider imageProvider = ImageProvider.getInstance();

	private final SettlerImageMapItem[][][][][] map;

	private final Pattern linePattern = Pattern.compile("\\s*([\\w\\*]+)\\s*,"
			+ "\\s*([\\w\\*]+)\\s*," + "\\s*([\\w\\*]+)\\s*,"
			+ "\\s*([\\w\\*]+)\\s*" + "=\\s*(c?\\d+)\\s*," + "\\s*(\\d+)\\s*,"
			+ "\\s*(\\d+)\\s*," + "\\s*(-?\\d+)\\s*");

	private final int civilisations;

	private final int types;

	private final int actions;

	private final int materials;

	private final int directions;

	/**
	 * Creates a new settler image map.
	 */
	private SettlerImageMap() {
		this.civilisations = ECivilisation.values().length;
		this.types = EMovableType.NUMBER_OF_MOVABLETYPES;
		this.actions = EMovableAction.values().length;
		this.materials = EMaterialType.NUMBER_OF_MATERIALS;
		this.directions = EDirection.VALUES.length;
		this.map = new SettlerImageMapItem[this.civilisations][this.types][this.actions][this.materials][this.directions];

		MovableParser parser = new MovableParser();

		int[][][][][] priorities = new int[this.civilisations][this.types][this.actions][this.materials][this.directions];
		// add pseudo entry.
		addEntryToMap(priorities, null, null, null, null, null, DEFAULT_ITEM, -1);

		readFromFile(parser, priorities, null, "movables.txt");
		for(ECivilisation civ : ECivilisation.values()) {
			readFromFile(parser, priorities, civ, "movables-" + civ + ".txt");
		}
	}

	/**
	 * Reads the map from the given file.
	 *
	 * @param name
	 * 		The file to read from.
	 */
	private void readFromFile(MovableParser parser, int[][][][][] priorities, ECivilisation civ, String name) {
		try {
			parser.parseFile(name, line -> addByLine(priorities, civ, line));
		} catch (IOException e) {
			System.err.println("Error reading image file. Settler images might not work.");
		}
	}

	/**
	 * Adds a line to the map
	 *
	 * @param priorities
	 * 		The priority table to use.
	 * @param line
	 * 		The line.
	 * @throws IllegalArgumentException
	 * 		if the line is not correct.
	 */
	private void addByLine(int[][][][][] priorities, ECivilisation civ, String line) {
		final Matcher matcher = parseLine(line);
		final String typeString = matcher.group(1);
		final String actionString = matcher.group(2);
		final String materialString = matcher.group(3);
		final String directionString = matcher.group(4);

		EMovableType type = parseType(typeString);
		EMovableAction action = parseAction(actionString);
		EMaterialType material = parseMaterial(materialString);
		EDirection direction = parseDirection(directionString);

		int priority = calculatePriority(civ, type, action, material, direction);

		final String fileString = matcher.group(5);
		final int sequence = Integer.parseInt(matcher.group(6));
		final int start = Integer.parseInt(matcher.group(7));
		final int duration = Integer.parseInt(matcher.group(8));

		ECivilisation[] civs = (civ == null) ? ECivilisation.values() : new ECivilisation[] {civ};

		for(ECivilisation c : civs) {
			final int fileIndex = parseFile(fileString, c);
			addEntryToMap(priorities, c, type, action, material, direction, new SettlerImageMapItem(fileIndex, sequence, start, duration), priority);
		}
	}

	private int parseFile(String fileString, ECivilisation civ) {
		if(fileString.contains("c")) {
			fileString = fileString.replace("c", Integer.toString(civ.getFileIndex()));
		}

		return Integer.parseInt(fileString);
	}

	private EMovableType parseType(final String typeString) {
		EMovableType type;
		if ("*".equals(typeString)) {
			type = null;
		} else {
			type = EMovableType.valueOf(typeString);
		}
		return type;
	}

	private EMovableAction parseAction(final String actionString) {
		EMovableAction action;
		if ("*".equals(actionString)) {
			action = null;
		} else {
			action = EMovableAction.valueOf(actionString);
		}
		return action;
	}

	private EMaterialType parseMaterial(final String materialString) {
		EMaterialType material;
		if ("*".equals(materialString)) {
			material = null;
		} else {
			material = EMaterialType.valueOf(materialString);
		}
		return material;
	}

	private EDirection parseDirection(final String directionString) {
		EDirection direction;
		if ("*".equals(directionString)) {
			direction = null;
		} else {
			direction = EDirection.valueOf(directionString);
		}
		return direction;
	}

	private int calculatePriority(ECivilisation civ, EMovableType type, EMovableAction action, EMaterialType material, EDirection direction) {
		int priority = 1;// more than 0.
		if(civ != null) {
			priority += 10;
		}
		if (type != null) {
			priority += 100;
		}
		if (action != null) {
			priority += 1000;
		}
		if (material != null) {
			priority += 10000;
		}
		if (direction != null) {
			priority += 100000;
		}
		return priority;
	}

	/**
	 * Parses a line.
	 *
	 * @param line
	 * 		The line.
	 * @return The line matched against the line pattern.
	 * @throws IllegalArgumentException
	 * 		if the line is not correct.
	 */
	private Matcher parseLine(String line) {
		final Matcher matcher = this.linePattern.matcher(line);
		final boolean matches = matcher.matches();
		if (!matches) {
			throw new IllegalArgumentException("Invalid line syntax: " + line); // ignore
		}
		return matcher;
	}

	/**
	 * Adds an entry to the map. Overrides cells with lower priorities.
	 *
	 * @param priorities
	 * 		The priority table to use.
	 * @param type
	 * @param action
	 * @param material
	 * @param direction
	 * @param item
	 * @param priority
	 */
	private void addEntryToMap(int[][][][][] priorities, ECivilisation civ, EMovableType type, EMovableAction action, EMaterialType material, EDirection direction, SettlerImageMapItem item, int priority) {
		int minCiv, maxCiv;
		if(civ == null) {
			minCiv = 0;
			maxCiv = this.civilisations;
		} else {
			minCiv = civ.ordinal();
			maxCiv = minCiv + 1;
		}

		int minType, maxType;
		if (type == null) {
			minType = 0;
			maxType = this.types;
		} else {
			minType = type.ordinal();
			maxType = minType + 1;
		}

		int minAction, maxAction;
		if (action == null) {
			minAction = 0;
			maxAction = this.actions;
		} else {
			minAction = action.ordinal();
			maxAction = minAction + 1;
		}

		int minMaterial, maxMaterial;
		if (material == null) {
			minMaterial = 0;
			maxMaterial = this.materials;
		} else {
			minMaterial = material.ordinal();
			maxMaterial = minMaterial + 1;
		}

		int minDirection, maxDirection;
		if (direction == null) {
			minDirection = 0;
			maxDirection = this.directions;
		} else {
			minDirection = direction.ordinal();
			maxDirection = minDirection + 1;
		}

		for (int civIndex = minCiv; civIndex < maxCiv; civIndex++) {
			for (int typeIndex = minType; typeIndex < maxType; typeIndex++) {
				for (int actionIndex = minAction; actionIndex < maxAction; actionIndex++) {
					for (int materialIndex = minMaterial; materialIndex < maxMaterial; materialIndex++) {
						for (int direcitonIndex = minDirection; direcitonIndex < maxDirection; direcitonIndex++) {
							if (priorities[civIndex][typeIndex][actionIndex][materialIndex][direcitonIndex] < priority) {
								map[civIndex][typeIndex][actionIndex][materialIndex][direcitonIndex] = item;
								priorities[civIndex][typeIndex][actionIndex][materialIndex][direcitonIndex] = priority;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Gets an image for a given settler.
	 *
	 * @param movable
	 * 		The settler to get the image for
	 * @return The image or an null-image.
	 * @see SettlerImageMap#getImageForSettler(ECivilisation, EMovableType, EMovableAction, EMaterialType, EDirection, float)
	 */
	public Image getImageForSettler(IMovable movable, float progress) {
		if (movable.getAction() == EMovableAction.WALKING) {
			progress = progress / 2;
			if (movable.isRightstep()) {
				progress += .5f;
			}
		}
		return getImageForSettler(movable.getPlayer().getCivilisation(), movable.getMovableType(),
				movable.getAction(), movable.getMaterial(),
				movable.getDirection(), progress);
	}

	/**
	 * Gets an image for a given settler.
	 *
	 * @param civilisation
	 * 		The civilisation of the settler
	 * @param movableType
	 * 		The type of the settler.
	 * @param action
	 * 		The action the settler is doing.
	 * @param material
	 * 		The material that is assigned to the settler.
	 * @param direction
	 * 		Its direction.
	 * @param progress
	 * 		The progress.
	 * @return The image.
	 */
	public Image getImageForSettler(ECivilisation civilisation, EMovableType movableType, EMovableAction action, EMaterialType material, EDirection direction, float progress) {
		SettlerImageMapItem item = getMapItem(civilisation, movableType, action, material, direction);

		int duration = item.getDuration();
		int imageIndex;
		if (duration >= 0) {
			imageIndex = item.getStart() + Math.min((int) (progress * duration), duration - 1);
		} else {
			imageIndex = item.getStart() + Math.max((int) (progress * duration), duration + 1);
		}
		return this.imageProvider.getSettlerSequence(item.getFile(), item.getSequenceIndex()).getImageSafe(imageIndex, () -> Labels.getName(movableType) + "-" + action + "-" + Labels.getName(material, false ) + "-" + direction + "%" + progress);
	}

	/**
	 * Gets a map item.
	 *
	 * @param civilisation
	 * @param movableType
	 * @param action
	 * @param material
	 * @param direction
	 * @return The item of the map at the given position. Is not null.
	 */
	private SettlerImageMapItem getMapItem(ECivilisation civilisation, EMovableType movableType, EMovableAction action, EMaterialType material, EDirection direction) {
		SettlerImageMapItem item = this.map[civilisation.ordinal()][movableType.ordinal()][action.ordinal()][material.ordinal][direction.ordinal];
		if (item == null) {
			if(civilisation != ECivilisation.ROMAN) {
				return getMapItem(ECivilisation.ROMAN, movableType, action, material, direction);
			} else {
				return DEFAULT_ITEM;
			}
		} else {
			return item;
		}
	}

	public static SettlerImageMap getInstance() {
		if (instance == null) {
			instance = new SettlerImageMap();
		}
		return instance;
	}
}
