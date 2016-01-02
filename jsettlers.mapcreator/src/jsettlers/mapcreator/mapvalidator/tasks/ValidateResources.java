package jsettlers.mapcreator.mapvalidator.tasks;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.localization.EditorLabels;

/**
 * Validate resources on wrong lanscape
 * 
 * @author Andreas Butti
 *
 */
public class ValidateResources extends AbstractValidationTask {

	/**
	 * Constructor
	 */
	public ValidateResources() {
	}

	@Override
	public void doTest() {
		addHeader("resource.header");

		for (short x = 0; x < data.getWidth(); x++) {
			for (short y = 0; y < data.getHeight(); y++) {
				ELandscapeType landacape = data.getLandscape(x, y);
				EResourceType resource = data.getResourceType(x, y);
				if (data.getResourceAmount(x, y) > 0 && !mayHoldResource(landacape, resource)) {
					String landscapeName = EditorLabels.getLabel("landscape." + landacape.name());
					String resourceName = Labels.getName(resource);
					addErrorMessage("resource.text", new ShortPoint2D(x, y), landscapeName, resourceName);
				}
			}
		}
	}

	/**
	 * Check resource type on landacape
	 * 
	 * @param landscape
	 *            Landacape
	 * @param resourceType
	 *            Resource
	 * @return true if valid
	 */
	public static boolean mayHoldResource(ELandscapeType landscape, EResourceType resourceType) {
		if (resourceType == EResourceType.FISH) {
			return landscape.isWater();
		} else {
			return landscape == ELandscapeType.MOUNTAIN || landscape == ELandscapeType.MOUNTAINBORDER;
		}
	}

}
