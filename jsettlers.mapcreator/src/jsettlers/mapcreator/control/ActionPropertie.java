package jsettlers.mapcreator.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import jsettlers.common.landscape.ELandscapeType;

/**
 * Helper to create a property file with all information to start a new editor instance
 * 
 * @author Andreas Butti
 *
 */
public class ActionPropertie {

	/**
	 * Properties to load / save
	 */
	private final Properties prop = new Properties();

	/**
	 * Read an action property
	 * 
	 * @param in
	 *            Input stream
	 * @throws IOException
	 */
	public ActionPropertie(FileInputStream in) throws IOException {
		prop.load(in);
	}

	/**
	 * Start with an empty property
	 */
	public ActionPropertie() {
	}

	/**
	 * @return The action
	 */
	public String getAction() {
		return prop.getProperty("action");
	}

	/**
	 * @param action
	 *            The action
	 */
	public void setAction(String action) {
		prop.setProperty("action", action);
	}

	/**
	 * @return Map ID
	 */
	public String getMapId() {
		return prop.getProperty("map-id");
	}

	/**
	 * @param mapId
	 *            Map ID
	 */
	public void setMapId(String mapId) {
		prop.setProperty("map-id", mapId);
	}

	/**
	 * @return Map Name
	 */
	public String getMapName() {
		return prop.getProperty("map-name");
	}

	/**
	 * @param name
	 *            Map Name
	 */
	public void setMapName(String name) {
		prop.setProperty("map-name", name);
	}

	/**
	 * @return Map Description
	 */
	public String getMapDescription() {
		return prop.getProperty("map-description");
	}

	/**
	 * @param description
	 *            Map Description
	 */
	public void setMapDescription(String description) {
		prop.setProperty("map-description", description);
	}

	/**
	 * @return ELandscapeType
	 */
	public ELandscapeType getLanscapeType() {
		return ELandscapeType.valueOf(prop.getProperty("lanscape-type"));
	}

	/**
	 * @param type
	 *            ELandscapeType
	 */
	public void setLanscapeType(ELandscapeType type) {
		prop.setProperty("lanscape-type", type.toString());
	}

	/**
	 * @return Width
	 */
	public int getWidth() {
		return Integer.parseInt(prop.getProperty("width"));
	}

	/**
	 * @param width
	 *            Width
	 */
	public void setWidth(int width) {
		prop.setProperty("width", String.valueOf(width));
	}

	/**
	 * @return Width
	 */
	public int getHeight() {
		return Integer.parseInt(prop.getProperty("height"));
	}

	/**
	 * @param height
	 *            Height
	 */
	public void setHeight(int height) {
		prop.setProperty("height", String.valueOf(height));
	}

	/**
	 * @return Count
	 */
	public int getMinPlayerCount() {
		return Integer.parseInt(prop.getProperty("min-player"));
	}

	/**
	 * @param minPlayer
	 *            Count
	 */
	public void setMinPlayerCount(int minPlayer) {
		prop.setProperty("min-player", String.valueOf(minPlayer));
	}

	/**
	 * @return Count
	 */
	public int getMaxPlayerCount() {
		return Integer.parseInt(prop.getProperty("max-player"));
	}

	/**
	 * @param maxPlayer
	 *            Count
	 */
	public void setMaxPlayerCount(int maxPlayer) {
		prop.setProperty("max-player", String.valueOf(maxPlayer));
	}

	/**
	 * Save the property to a file
	 * 
	 * @param file
	 *            File
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void saveToFile(File file) throws FileNotFoundException, IOException {
		prop.store(new FileOutputStream(file), "Action property");
	}
}
