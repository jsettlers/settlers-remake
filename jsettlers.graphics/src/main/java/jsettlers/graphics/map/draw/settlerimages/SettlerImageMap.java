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

import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.draw.ImageProvider;

/**
 * This is a settler image map that mapps the state of a settler to the sequence that is to be played.
 * <p>
 * The mapping is a function: (type, material, direction) => (file, sequence index, start, duration)
 *
 * @author michael
 */
public final class SettlerImageMap {

	static final SettlerImageMapItem DEFAULT_ITEM = new SettlerImageMapItem(10, 0, 0, 1);

	private static SettlerImageMap instance;

	private final ImageProvider imageProvider;

	final HashMap<SettlerImageFlavor, SettlerImageMapItem> map = new HashMap<>();

	private final Pattern linePattern = Pattern.compile("\\s*([\\w\\*]+)\\s*,"
			+ "\\s*([\\w\\*]+)\\s*," + "\\s*([\\w\\*]+)\\s*,"
			+ "\\s*([\\w\\*]+)\\s*" + "=\\s*(\\d+)\\s*," + "\\s*(\\d+)\\s*,"
			+ "\\s*(\\d+)\\s*," + "\\s*(-?\\d+)\\s*");

	private SettlerImageMap() {
		imageProvider = ImageProvider.getInstance();
	}

	SettlerImageMap(ImageProvider imageProvider){
		this.imageProvider = imageProvider;
	}

	public void loadFromMoveablesTextFile() {
		try {
			InputStream file = getClass().getResourceAsStream("movables.txt");
			readFromStream(file);
		} catch (IOException e) {
			System.err.println("Error reading image file. "
					+ "Settler images might not work.");
		}
	}

	/**
	 * Reads the map from the given file.
	 *
	 * @param file
	 * 		The file to read from.
	 */
	private void readFromStream(InputStream file) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(file));
		readFromReader(reader);
	}

	private void readFromReader(BufferedReader reader) throws IOException {
		HashMap<SettlerImageFlavor, Integer> priorities = new HashMap<>();
		String line = reader.readLine();
		while (line != null) {
			if (!line.isEmpty() && !line.startsWith("#")) {
				try {
					addByLine(line, priorities);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}

			line = reader.readLine();
		}
	}

	/**
	 * Adds a line to the map
	 *
	 * @param line
	 * 		The line.
	 * @param priorities
	 * @throws IllegalArgumentException
	 * 		if the line is not correct.
	 */
	private void addByLine(String line, HashMap<SettlerImageFlavor, Integer> priorities) {
		final Matcher matcher = parseLine(line);
		final String typeString = matcher.group(1);
		final String actionString = matcher.group(2);
		final String materialString = matcher.group(3);
		final String directionString = matcher.group(4);

		EMovableType type = parseType(typeString);
		EMovableAction action = parseAction(actionString);
		EMaterialType material = parseMaterial(materialString);
		EDirection direction = parseDirection(directionString);

		final int fileIndex = Integer.parseInt(matcher.group(5));
		final int sequence = Integer.parseInt(matcher.group(6));
		final int start = Integer.parseInt(matcher.group(7));
		final int duration = Integer.parseInt(matcher.group(8));

		SettlerImageFlavor flavor = new SettlerImageFlavor(type, action, material, direction);
		int priority = calculatePriority(flavor);
		if(!priorities.containsKey(flavor) || priorities.get(flavor) < priority) {
			map.put(flavor, new SettlerImageMapItem(fileIndex, sequence, start, duration));
			priorities.put(flavor, priority);
		}
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

	private int calculatePriority(SettlerImageFlavor settlerImageFlavor) {
		int priority = 1;// more than 0.
		if (settlerImageFlavor.getType() != null) {
			priority += 10;
		}
		if (settlerImageFlavor.getAction() != null) {
			priority += 100;
		}
		if (settlerImageFlavor.getMaterial() != null) {
			priority += 1000;
		}
		if (settlerImageFlavor.getDirection() != null) {
			priority += 10000;
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
	 * Gets an image for a given settler.
	 *
	 * @param movable
	 * 		The settler to get the image for
	 * @return The image or a null-image.
	 * @see SettlerImageMap#getImageForSettler(SettlerImageFlavor, float)
	 */
	public Image getImageForSettler(IMovable movable, float progress) {
		if (movable.getAction() == EMovableAction.WALKING) {
			progress = progress / 2;
			if (movable.isRightstep()) {
				progress += .5f;
			}
		}
		return getImageForSettler(SettlerImageFlavor.createFromMovable(movable), progress);
	}

	/**
	 * Gets an image for a given settler.
	 *
	 *
	 * @param settlerImageFlavor
	 * @param progress
	 * 		The progress.
	 * @return The image.
	 */
	public Image getImageForSettler(SettlerImageFlavor settlerImageFlavor, float progress) {
		SettlerImageMapItem item = getMapItem(settlerImageFlavor);
		return imageProvider.getImageSafe(item, progress);
	}

	/**
	 * Gets a map item.
	 *
	 *
	 * @param settlerImageFlavor@return The item of the map at the given position. Is not null.
	 */
	private SettlerImageMapItem getMapItem(SettlerImageFlavor settlerImageFlavor) {
		SettlerImageMapItem item = map.get(settlerImageFlavor);
		if (item == null) {
			return DEFAULT_ITEM;
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

	void loadFromMovablesText(String text) throws IOException {
		readFromReader(new BufferedReader(new StringReader(text)));
	}
}
