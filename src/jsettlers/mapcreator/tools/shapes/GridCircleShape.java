package jsettlers.mapcreator.tools.shapes;



public class GridCircleShape extends LineCircleShape implements ShapeType {

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
		return "spaced circle";
	}

}
