package jsettlers.mapcreator.mapvalidator.tasks;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.mapvalidator.result.fix.FreeBorderFix;

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
		FreeBorderFix fix = new FreeBorderFix();
		addHeader("blockingborder.header", fix);

		int width = data.getWidth();
		int height = data.getHeight();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (1 <= y && y < height - 2 && 1 <= x && x < width - 2) {
					continue;
				}

				if (!data.getLandscape(x, y).isBlocking) {
					ShortPoint2D p = new ShortPoint2D(x, y);
					addErrorMessage("at-position", p, x, y);
					fix.addPosition(p);
				}
			}
		}
	}

}
