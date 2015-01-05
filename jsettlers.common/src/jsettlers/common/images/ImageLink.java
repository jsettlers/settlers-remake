package jsettlers.common.images;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ImageLink {
	private static final Pattern ORIGINAL_LINK = Pattern
			.compile("original_(\\d+)_(SETTLER|GUI|LANDSCAPE)_(\\d+)");

	public static ImageLink fromName(String name, int imageIndex) {
		Matcher matcher = ORIGINAL_LINK.matcher(name);
		if (matcher.matches()) {
			return new OriginalImageLink(EImageLinkType.valueOf(matcher.group(2)), Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher
					.group(3)), imageIndex);
		} else {
			return new DirectImageLink(name + "." + imageIndex);
		}
	}
}
