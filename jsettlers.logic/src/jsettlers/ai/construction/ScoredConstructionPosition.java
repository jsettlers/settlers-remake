package jsettlers.ai.construction;

import jsettlers.common.position.ShortPoint2D;

class ScoredConstructionPosition {
	ShortPoint2D point;
	double score;
	
	public ScoredConstructionPosition(ShortPoint2D point, double score) {
		this.point = point;
		this.score = score;
	}
	
}