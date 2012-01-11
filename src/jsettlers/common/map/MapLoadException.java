package jsettlers.common.map;

/**
 * This is an eception that occured during loading/creation of the map.
 * 
 * @author michael
 */
public class MapLoadException extends Exception {

	/**
     * 
     */
	private static final long serialVersionUID = -8884862905101040114L;

	public MapLoadException() {
	}

	public MapLoadException(String arg0) {
		super(arg0);
	}

	public MapLoadException(Throwable arg0) {
		super(arg0);
	}

	public MapLoadException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
