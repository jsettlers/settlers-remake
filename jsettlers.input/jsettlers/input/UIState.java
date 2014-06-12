package jsettlers.input;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.map.UIStateData;

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
	private final UIStateData uiStateData;

	public UIState(UIStateData uiStateData) {
		this.uiStateData = uiStateData;
	}

	public UIState(ShortPoint2D startPoint) {
		this.uiStateData = new UIStateData(startPoint);
	}

	public UIStateData getUiStateData() {
		return uiStateData;
	}

	public void writeTo(ObjectOutputStream oos) throws IOException {
		oos.writeObject(this);
	}

	/**
	 * Reads the ui state from the given stream.
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static UIState readFrom(ObjectInputStream ois) throws IOException {
		try {
			return (UIState) ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}

}
