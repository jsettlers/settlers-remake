package jsettlers.graphics.map;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import jsettlers.common.position.ISPosition2D;

public class UIState {

	/**
	 * The player that uses the ui.
	 */
	private final int player;
	
	/**
	 * The center point of the screen.
	 */
	private final ISPosition2D screenCenter;

	public UIState(int player, ISPosition2D startPoint) {
		this.player = player;
		this.screenCenter = startPoint;
    }
	
	public ISPosition2D getScreenCenter() {
	    return screenCenter;
    }
	
	public int getPlayer() {
	    return player;
    }
	
	public void writeTo(OutputStream stream) throws IOException {
		new ObjectOutputStream(stream).writeObject(this);
	}

	/**
	 * Reads the ui state from the given stream.
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
