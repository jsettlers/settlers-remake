package jsettlers.mapcreator.localization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.PropertyResourceBundle;

import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.localization.Labels.LocaleSuffix;

public class EditorLabels {
	private static boolean load;
	private static PropertyResourceBundle resource;

	public synchronized static String getLabel(String name) {
		if (!load) {
			LocaleSuffix[] locales = Labels.getLocaleSuffixes();

			for (LocaleSuffix locale : locales) {
				String filename = locale.getFileName("labels", ".properties");
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
