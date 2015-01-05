package jsettlers.mapcreator.tools.shapes;

import jsettlers.mapcreator.localization.EditorLabels;

public class GridCircleShape extends LineCircleShape {

	@Override
	protected byte getFieldRating(int x, int y, double distance) {
		if (x % 2 == 0 && y % 2 == 1) {
			return super.getFieldRating(x, y, distance);
		} else {
			return 0;
		}
	}

	@Override
	public String getName() {
		return EditorLabels.getLabel("grid_circle");
	}

}
