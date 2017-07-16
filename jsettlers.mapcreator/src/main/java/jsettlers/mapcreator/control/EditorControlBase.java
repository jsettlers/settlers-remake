package jsettlers.mapcreator.control;

import java.util.Date;

import jsettlers.algorithms.previewimage.PreviewImageCreator;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.mapvalidator.MapValidator;

/**
 * Base class for editor control, handle property access / notification
 * 
 * @author Andreas Butti
 *
 */
public abstract class EditorControlBase {

	/**
	 * Validates the map for errors
	 */
	protected final MapValidator validator = new MapValidator();

	/**
	 * Map data
	 */
	protected MapData mapData;

	/**
	 * Header of the current open map
	 */
	private MapFileHeader header;

	/**
	 * Constructor
	 */
	public EditorControlBase() {
	}

	/**
	 * Generate new map header
	 * 
	 * @return New header
	 */
	protected MapFileHeader generateMapHeader() {
		short[] image = new PreviewImageCreator(header.getWidth(), header.getHeight(), MapFileHeader.PREVIEW_IMAGE_SIZE,
				mapData.getPreviewImageDataSupplier()).getPreviewImage();
		MapFileHeader imagedHeader = new MapFileHeader(header.getType(), header.getName(), header.getBaseMapId(), header.getDescription(),
				header.getWidth(), header.getHeight(), header.getMinPlayers(), header.getMaxPlayers(), new Date(), image);
		return imagedHeader;
	}

	/**
	 * Create a new header with a given name
	 * 
	 * @param name
	 *            Name
	 */
	protected void createNewHeaderWithName(String name) {
		setHeader(new MapFileHeader(header.getType(), name, null, header.getDescription(), header.getWidth(),
				header.getHeight(), header.getMinPlayers(), header.getMaxPlayers(), new Date(), header.getPreviewImage().clone()));
	}

	/**
	 * @param header
	 *            Header of the current open map
	 */
	public void setHeader(MapFileHeader header) {
		this.header = header;
		validator.setHeader(header);
	}

	/**
	 * @return Header of the current open map
	 */
	public MapFileHeader getHeader() {
		return header;
	}

}
