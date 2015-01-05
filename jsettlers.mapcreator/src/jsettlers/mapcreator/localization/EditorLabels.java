package jsettlers.mapcreator.localization;

import java.io.IOException;
import java.util.PropertyResourceBundle;

public class EditorLabels {
	private static boolean load;
	private static PropertyResourceBundle resource;

	public synchronized static String getLabel(String name) {
		if (!load) {
			try {
				resource = new PropertyResourceBundle(
						EditorLabels.class.getResourceAsStream("labels_de.properties"));
			} catch (IOException e) {
			}
			load = true;
		}
		if (resource == null || !resource.containsKey(name)) {
			return name;
		} else {
			return resource.getString(name);
		}
	}
}
