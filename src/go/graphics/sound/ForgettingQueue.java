package go.graphics.sound;

/**
 * This is sort of a blocking queue. If there is nobody to use the request, the request is thrown away.
 * @author michael
 *
 * @param <T>
 */
public class ForgettingQueue<T> {
	private T current = null;
	private float lvolume;
	private float rvolume;;
	
	public synchronized void offer(T e, float lvolume, float rvolume) {
		current = e;
		this.lvolume = lvolume;
		this.rvolume = rvolume;
		this.notify();
	}
	
	public synchronized Sound<T> take() throws InterruptedException {
		while (current == null) {
			this.wait();
		}
		Sound<T> r = new Sound<T>(current, lvolume, rvolume);
		current = null;
		return r;
	}
	
	public static class Sound<T2> {
		private final T2 data;
		private final float lvolume2;
		private final float rvolume2;

		private Sound(T2 data, float lvolume, float rvolume) {
			this.data = data;
			lvolume2 = lvolume;
			rvolume2 = rvolume;
		}
		
		public T2 getStart() {
	        return data;
        }
		
		public float getLvolume() {
	        return lvolume2;
        }
		public float getRvolume() {
	        return rvolume2;
        }
	}
}
