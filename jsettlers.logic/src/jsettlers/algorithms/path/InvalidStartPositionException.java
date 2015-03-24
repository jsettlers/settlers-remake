package jsettlers.algorithms.path;

public class InvalidStartPositionException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;

	public InvalidStartPositionException(String text, int x, int y) {
		super("invalid start position (" + x + "|" + y + "): " + text);
	}
}