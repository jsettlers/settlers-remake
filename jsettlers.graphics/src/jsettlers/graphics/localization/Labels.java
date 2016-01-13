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
package jsettlers.graphics.localization;

import java.io.IOException;
import java.io.InputStream;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierType;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.progress.EProgressState;

/**
 * This class provides access to all messages.
 * 
 * @author michael
 */
public final class Labels extends AbstractLabels {
	private static final Labels instance = new Labels();

	private Labels() {
	}

	@Override
	protected InputStream getLocaleStream(LocaleSuffix locale) throws IOException {
		String filename = locale.getFileName("localization/labels", ".properties");
		InputStream inputStream = ResourceManager.getResourcesFileStream(filename);
		return inputStream;
	}

	/**
	 * Gets a string
	 * 
	 * @param key
	 *            The name of the string
	 * @return The localized string
	 */
	public static String getString(String key) {
		return instance.getSingleString(key);
	}

	public static String getString(String string, Object... args) {
		String parsedString = getString(string);
		return String.format(instance.usedLocale, parsedString, args);
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
	 * Gets the name of a {@link ESoldierType}
	 *
	 * @param type
	 *            The soldier type
	 * @return The localized name.
	 */
	public static String getName(ESoldierType type) {
		return getString("soldier_" + type);
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
