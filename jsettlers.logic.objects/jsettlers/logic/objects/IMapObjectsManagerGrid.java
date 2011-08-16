package jsettlers.logic.objects;

public interface IMapObjectsManagerGrid {

	IMapObjectsManagerTile getTile(short x, short y);

	short getWidth();

	short getHeight();

}
