package jsettlers.mapcreator.localization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.PropertyResourceBundle;

import jsettlers.graphics.localization.Labels;

public class EditorLabels {
	private static boolean load;
	private static PropertyResourceBundle resource;

	public synchronized static String getLabel(String name) {
		if (!load) {
			String[] locales = Labels.getLocaleSuffixes();

			for (String locale : locales) {
				String filename = "labels" + locale + ".properties";
				try {
					InputStream stream = EditorLabels.class.getResourceAsStream(filename);
					if (stream == null) {
						continue;
					}
					resource = new PropertyResourceBundle(new InputStreamReader(
							stream, "UTF-8"));
					break;
				} catch (IOException e) {
				}
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
