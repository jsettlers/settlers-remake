package jsettlers.mapcreator.mapvalidator.tasks;

import jsettlers.common.position.ShortPoint2D;

/**
 * Check if all border positions are blocking
 * 
 * @author Andreas Butti
 *
 */
public class ValidateBlockingBorderPositions extends AbstractValidationTask {

	/**
	 * Constructor
	 */
	public ValidateBlockingBorderPositions() {
	}

	@Override
	public void doTest() {
		addHeader("blockingborder.header");

		int width = data.getWidth();
		int height = data.getHeight();

		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {
				if (1 <= y && y < height - 1 && 1 <= x && x < width - 1) {
					continue;
				}

				if (!data.getLandscape(x, y).isBlocking) {
					addErrorMessage("at-position", new ShortPoint2D(x, y), x, y);
				}
			}
		}
	}

}
