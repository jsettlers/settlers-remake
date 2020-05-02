package jsettlers.graphics.map.draw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.player.ECivilisation;

public final class ImageLinkMap {
	private static final Pattern BEGIN_PATTERN = Pattern.compile("\\s*([\\w\\*]+)\\s*,\\s*([\\w\\*]+)\\s*\\{\\s*");
	private static final Pattern LINE_PATTERN = Pattern.compile("\\s*([\\w\\*]+)\\s*:\\s*([\\w\\*]+)\\s*");
	private static final Pattern CLONE_PATTERN = Pattern.compile("\\s*clone\\s*([\\w\\*]+)\\s*,\\s*([\\w\\*]+)\\s*to\\s*([\\w\\*]+)\\s*,\\s*([\\w\\*]+)\\s*");

	public static final ImageLinkMap INSTANCE = new ImageLinkMap("linkmap.txt");

	private EnumMap<ECivilisation, EnumMap<ECommonLinkType, HashMap<EMovableType, ImageLink>>> map = new EnumMap<>(ECivilisation.class);

	private ImageLinkMap(String file) {
		for(ECivilisation civ : ECivilisation.values()) {
			EnumMap<ECommonLinkType, HashMap<EMovableType, ImageLink>> typeMap = new EnumMap<>(ECommonLinkType.class);
			for(ECommonLinkType type : ECommonLinkType.values()) typeMap.put(type, new HashMap<>());
			map.put(civ, typeMap);
		}

		try(BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(file)))) {
			readFile(reader);
		} catch(IOException ex) {
			throw new Error(ex);
		}
	}

	private void readFile(BufferedReader reader) throws IOException {

		EMovableType movable = null;
		ECommonLinkType type = null;

		String line;
		while((line = reader.readLine()) != null) {
			if(line.isEmpty()) continue;
			if(line.startsWith("#")) continue;

			if(line.contains("{")) {
				if(movable != null) throw new IllegalStateException("Section not closed!");

				Matcher begin = BEGIN_PATTERN.matcher(line);
				if(!begin.matches()) throw new IllegalStateException("Section marker not matching!");

				type = ECommonLinkType.valueOf(begin.group(1));
				movable = EMovableType.valueOf(begin.group(2));
			} else if(line.equals("}")) {
				if(movable == null) throw new IllegalStateException("Only Sections can be closed!");

				movable = null;
				type = null;
			} else if(movable == null && line.startsWith("clone ")) {
				Matcher clone = CLONE_PATTERN.matcher(line);
				if(!clone.matches()) throw new IllegalStateException("Illegal clone marker!");

				ECommonLinkType origType = ECommonLinkType.valueOf(clone.group(1));
				EMovableType origMovable = EMovableType.valueOf(clone.group(2));

				ECommonLinkType dstType = ECommonLinkType.valueOf(clone.group(3));
				EMovableType dstMovable = EMovableType.valueOf(clone.group(4));

				for(EnumMap<ECommonLinkType, HashMap<EMovableType, ImageLink>> civMap : map.values()) {
					civMap.get(dstType).put(dstMovable, civMap.get(origType).get(origMovable));
				}
			} else {
				if(movable == null) throw new IllegalStateException("Declaration outside of section!");

				Matcher declaration = LINE_PATTERN.matcher(line);
				if(!declaration.matches()) throw new IllegalStateException("Declaration does not match!");

				ECivilisation civ = ECivilisation.valueOf(declaration.group(1));
				ImageLink link = ImageLink.fromName(declaration.group(2));
				if(civ != ECivilisation.ROMAN && link instanceof OriginalImageLink) {
					ImageLink fallback = map.get(ECivilisation.ROMAN).get(type).get(movable);
					if(fallback instanceof OriginalImageLink) ((OriginalImageLink)link).setFallback((OriginalImageLink)fallback);
				}

				map.get(civ).get(type).put(movable, link);
			}
		}

		reader.close();

	}

	public static ImageLink get(ECivilisation civ, ECommonLinkType type, EMovableType movable) {
		return INSTANCE.map.get(civ).get(type).get(movable);
	}
}
