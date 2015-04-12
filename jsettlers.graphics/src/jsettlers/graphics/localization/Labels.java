package jsettlers.graphics.localization;

import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.progress.EProgressState;

/**
 * This class provides access to all messages.
 * 
 * @author michael
 */
public final class Labels {

	private Labels() {
	}

	private static ResourceBundle labels;
	private static boolean labelsLoaded;

	private static synchronized ResourceBundle getLabels() {
		if (!labelsLoaded) {
			loadLabels();
			labelsLoaded = true;
		}
		return labels;
	}

	private static void loadLabels() {
		Locale currentLocale = Locale.getDefault();
		String[] locales = new String[] {
				"_" + currentLocale.getLanguage() + "_" + currentLocale.getCountry(),
				"_" + currentLocale.getLanguage(),
				"_en_US",
		};

		for (String locale : locales) {
			String filename = "localization/labels" + locale + ".properties";
			try {
				labels = new PropertyResourceBundle(ResourceManager.getFile(filename));
				break;
			} catch (IOException e) {
				System.err.println("Warning: Could not find labels" + locale + ".properties. Falling back to next file.");
			}
		}
	}

	/**
	 * Gets a string
	 * 
	 * @param key
	 *            The name of the string
	 * @return The localized string
	 */
	public static String getString(String key) {
		ResourceBundle labels = getLabels();
		if (labels == null) {
			return key;
		} else {
			try {
				return labels.getString(key);
			} catch (MissingResourceException e) {
				return key;
			}
		}
	}

	/**
	 * Gets the name of a movable
	 * 
	 * @param type
	 *            The movable type
	 * @return The name.
	 */
	public static String getName(EMovableType type) {
		return getString("movable_" + type);
	}

	/**
	 * Gets the name of a resource
	 * 
	 * @param type
	 *            The resource type
	 * @return The name.
	 */
	public static String getName(EResourceType type) {
		return getString("resource_" + type);
	}

	/**
	 * Gets the name of a building
	 * 
	 * @param type
	 *            The building type
	 * @return The name.
	 */
	public static String getName(EBuildingType type) {
		return getString("building_" + type);
	}

	/**
	 * Gets the name of a material
	 * 
	 * @param type
	 *            The material type
	 * @param plural
	 *            If the plural name should be returned.
	 * @return The localized name.
	 */
	public static String getName(EMaterialType type, boolean plural) {
		return getString("material_" + type + (plural ? "p" : ""));
	}

	/**
	 * Gets the name for an action type.
	 * 
	 * @param action
	 *            The action type.
	 * @return The localized name.
	 */
	public static String getName(EActionType action) {
		return getString("action_" + action);
	}

	public static String getProgress(EProgressState loading) {
		return getString("progress_" + loading);
	}
}
