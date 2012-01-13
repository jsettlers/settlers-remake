package jsettlers.graphics.action;

import jsettlers.common.movable.EMovableType;

/**
 * This action is used to convert any movables to the given {@link EMovableType}
 * 
 * @author Andreas Eberle
 */
public class ConvertAction extends Action {

	private final EMovableType toType;
	private final short amount;

	/**
	 * This action is used to convert any movables to the given type.
	 * 
	 * @param toType
	 *            target type to convert the movables to
	 * @param amount
	 *            number of movables that should be converted. <br>
	 *            if amount == {@link Short}.MAX_VALUE all selected movables
	 *            will be converted.
	 */
	public ConvertAction(EMovableType toType, short amount) {
		super(EActionType.CONVERT);
		this.toType = toType;
		this.amount = amount;
	}

	/**
	 * @return {@link EMovableType} the movables should become.
	 */
	public EMovableType getTargetType() {
		return toType;
	}

	/**
	 * @return number of movables to convert. If value == {@link Short}
	 *         .MAX_VALUE all movables should be converted.
	 */
	public short getAmount() {
		return amount;
	}

}
