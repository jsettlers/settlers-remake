package jsettlers.graphics.reader;

import jsettlers.graphics.image.Image;
import jsettlers.graphics.sequence.Sequence;

public class ArraySequenceList<T extends Image> implements SequenceList<T> {
	private final Sequence<T>[] images;

	public ArraySequenceList(Sequence<T>[] sequences) {
		this.images = sequences;
	}

	@Override
	public Sequence<T> get(int index) {
		return images[index];
	}

	@Override
	public int size() {
		return images.length;
	}
}