package jsettlers.graphics.reader;

import jsettlers.graphics.image.Image;
import jsettlers.graphics.sequence.Sequence;

public interface SequenceList<T extends Image> {
	Sequence<T> get(int index);

	int size();
}
