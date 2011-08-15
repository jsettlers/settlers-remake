package jsettlers.graphics.map.draw.settlerimages;

/**
 * This is a map item of settler images.
 * @author michael
 *
 */
public class SettlerImageMapItem {
	private final int file;

	private final int sequenceIndex;

	private final int start;

	private final int duration;

	public SettlerImageMapItem(int file, int sequenceIndex, int start,
	        int duration) {
		this.file = file;
		this.sequenceIndex = sequenceIndex;
		this.start = start;
		this.duration = duration;
	}

	public int getFile() {
		return this.file;
	}

	public int getSequenceIndex() {
		return this.sequenceIndex;
	}

	public int getStart() {
		return this.start;
	}

	public int getDuration() {
		return this.duration;
	}
}
