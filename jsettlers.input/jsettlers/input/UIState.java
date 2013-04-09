package jsettlers.input;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import jsettlers.common.position.ShortPoint2D;

/**
 * This class holds a state of the GUI that can be saved.
 * 
 * @author Andreas Eberle
 */
public class UIState implements Serializable {
	private static final long serialVersionUID = 1481484536611544925L;

	/**
	 * The center point of the screen.
	 */
	private final ShortPoint2D screenCenter;

	public UIState(ShortPoint2D startPoint) {
		this.screenCenter = startPoint;
	}

	public ShortPoint2D getScreenCenter() {
		return screenCenter;
	}

	public void writeTo(OutputStream stream) throws IOException {
		new ObjectOutputStream(stream).writeObject(this);
	}

	/**
	 * Reads the ui state from the given stream.
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static UIState readFrom(InputStream stream) throws IOException {
		try {
			return (UIState) new ObjectInputStream(stream).readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}

}
