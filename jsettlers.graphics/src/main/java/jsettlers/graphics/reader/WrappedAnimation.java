package jsettlers.graphics.reader;

import jsettlers.common.images.AnimationSequence;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.NullImage;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.sequence.Sequence;

/**
 * Mapps an {@link AnimationSequence} to the acutal images using an
 * {@link ImageProvider}
 * 
 * @author michael
 *
 */
public class WrappedAnimation implements Sequence<Image> {

	private final ImageProvider imageProvider;
	private final AnimationSequence sequence;

	public WrappedAnimation(ImageProvider imageProvider, AnimationSequence sequence) {
		this.imageProvider = imageProvider;
		this.sequence = sequence;
	}

	@Override
	public int length() {
		return sequence.getLength();
	}

	@Override
	public Image getImage(int index) {
		return imageProvider.getImage(sequence.getImage(index));
	}

	@Override
	public Image getImageSafe(int index) {
		return index < 0 || index >= length() ? NullImage.getInstance() : getImage(index);
	}

}
