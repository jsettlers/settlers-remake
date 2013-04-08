package jsettlers.main.android.bg;

import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IPartitionSettings;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;

public class BgMap implements IGraphicsGrid {

	public static final ShortPoint2D startCenter = new ShortPoint2D(30, 0);
	private static final int TREE_BORDER = 20;
	private static final int MOVABLE_BORDER = 60;
	private static final double MOVABLE_PROBABILITY = .001;
	private static final double TREE_PROBABILITY = 0.1;

	private final BgFish fish = new BgFish(EMapObjectType.FISH_DECORATION);;
	private final BgFish tree = new BgFish(EMapObjectType.TREE_ADULT);

	private final double[] randoms = new double[17];

	public BgMap() {
		for (int i = 0; i < randoms.length; i++) {
			randoms[i] = Math.random();
		}

	}

	private double getRand(int x, int y) {
		double rand1 = randoms[x % randoms.length];
		double rand2 = randoms[(y * 7) % randoms.length];
		return (rand1 * 3 + rand2) % 1;
	}

	@Override
	public short getWidth() {
		return Short.MAX_VALUE;
	}

	@Override
	public short getHeight() {
		return 30;
	}

	@Override
	public IMovable getMovableAt(int x, int y) {
		if (x > MOVABLE_BORDER
		        && getLandscapeTypeAt(x, y) == ELandscapeType.GRASS
		        && getRand(x, y) > 1 - MOVABLE_PROBABILITY) {
			return new BgMovable(new ShortPoint2D(x, y));
		} else {
			return null;
		}
	}

	@Override
	public IMapObject getMapObjectsAt(int x, int y) {
		if (x > TREE_BORDER && ((x + y) & 3) == 0 && ((x - y) & 3) == 0
		        && getLandscapeTypeAt(x, y) == ELandscapeType.GRASS
		        && getRand(x, y) < TREE_PROBABILITY) {
			return tree;
		} else {
			return null;
		}
	}

	@Override
	public byte getHeightAt(int x, int y) {
		return 0;
	}

	@Override
	public ELandscapeType getLandscapeTypeAt(int x, int y) {
		return ELandscapeType.GRASS;
	}

	@Override
	public int getDebugColorAt(int x, int y) {
		return 0;
	}

	@Override
	public boolean isBorder(int x, int y) {
		return false;
	}

	@Override
	public byte getPlayerIdAt(int x, int y) {
		return 0;
	}

	@Override
	public byte getVisibleStatus(int x, int y) {
		return CommonConstants.FOG_OF_WAR_VISIBLE;
	}

	@Override
	public boolean isFogOfWarVisible(int x, int y) {
		return true;
	}

	@Override
	public void setBackgroundListener(
	        IGraphicsBackgroundListener backgroundListener) {
	}

	@Override
	public int nextDrawableX(int x, int y, int maxX) {
		return x + 1;
	}

	@Override
	public IPartitionSettings getPartitionSettings(int x, int y) {
		return null;
	}

}
