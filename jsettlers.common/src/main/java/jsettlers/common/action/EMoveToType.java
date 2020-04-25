package jsettlers.common.action;

public enum EMoveToType {
	DEFAULT(true, false), FORCED(false, false),
	/**
	 * Work at the target or patrol (for soldiers)
	 */
	WORK(true, true);
	
	public static EMoveToType[] VALUES = values();

	private final boolean attackOnTheWay;
	private final boolean workOnDestination;

	EMoveToType(boolean attackOnTheWay, boolean workOnDestination) {
		this.attackOnTheWay = attackOnTheWay;
		this.workOnDestination = workOnDestination;
	}

	public boolean isAttackOnTheWay() {
		return attackOnTheWay;
	}

	public boolean isWorkOnDestination() {
		return workOnDestination;
	}
}