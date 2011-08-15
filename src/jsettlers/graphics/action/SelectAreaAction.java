package jsettlers.graphics.action;

import jsettlers.common.map.shapes.IMapArea;

/**
 * This class hold special information for the action type
 * {@link EActionType#SELECT_AREA}.
 * 
 * @author michael
 */
public class SelectAreaAction extends Action {
	private final IMapArea area;

	/**
	 * Creates a new select area action.
	 * @param area The area.
	 */
	public SelectAreaAction(IMapArea area) {
		super(EActionType.SELECT_AREA);
		this.area = area;
	}

	public IMapArea getArea() {
	    return this.area;
    }
}
