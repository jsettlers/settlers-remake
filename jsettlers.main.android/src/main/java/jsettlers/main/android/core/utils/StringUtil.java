package jsettlers.main.android.core.utils;

/**
 * Created by Tom on 19/01/2016.
 */
public class StringUtil {
	public static boolean isNullOrWhiteSpace(CharSequence value) {
		if (value == null) {
			return true;
		}

		for (int i = 0; i < value.length(); i++) {
			if (!Character.isWhitespace(value.charAt(i))) {
				return false;
			}
		}

		return true;
	}
}
