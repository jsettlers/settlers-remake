package jsettlers.graphics.reader.bytereader;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class provides a little endian wrapper of a reader.
 * 
 * @author michael
 */
public class ByteReader {
	
	private static final int CACHE_SIZE = 10000;

	/**
	 * The position the input stram is currently at.
	 */
	private long inputStreamPosition = 0;
	/**
	 * The current cache position the user is reading from next.
	 * <p>
	 * It may never be greater than CACHE_SIZE.
	 * @see ByteReader#assertCacheHolds(int);
	 */
	private int cachePosition;
	/**
	 * The start of the cache in the file.
	 * <p>
	 * Initialize so that jump does good work.
	 */
	private long cacheStart = -2 * CACHE_SIZE;

	/**
	 * The data cache
	 */
	private byte[] cache = new byte[CACHE_SIZE];
	
	private final InputStream in;

	/**
	 * Creates a new reader.
	 * 
	 * @param in
	 *            The in reader.
	 * @throws IOException It an IO error occured.
	 */
	public ByteReader(InputStream in) throws IOException {
		this.in = in;
		jumpCachePosition(0);
	}

	/**
	 * Reads a 16 bit int.
	 * 
	 * @return The int.
	 * @throws IOException If an io error occured.
	 */
	public int read16() throws IOException {
		assertCacheHolds(2);

		byte byte0 = this.cache[this.cachePosition++];
		byte byte1 = this.cache[this.cachePosition++];

		return (0xff & byte0) | ((0xff & byte1) << 8);
	}

	/**
	 * Sets the cache position pointer and the cache so that the pointer is on
	 * the given position in the file.
	 * 
	 * @param newCachePosition A file position
	 * @throws IOException 
	 */
	private void jumpCachePosition(long newCachePosition) throws IOException {
		long positionInCache = newCachePosition - this.cacheStart;
		if (positionInCache >= 0 && positionInCache < CACHE_SIZE) {
			this.cachePosition = (int) positionInCache;
		} else {
			//we have to reload...
			this.inputStreamPosition += this.in.skip(newCachePosition - this.inputStreamPosition); 
			this.cacheStart = this.inputStreamPosition;
			
			this.inputStreamPosition += this.in.read(this.cache, 0, CACHE_SIZE);
			this.cachePosition = 0;
		}
	}

	/**
	 * Asserts that the cache hold bytecount valid bytes.
	 * <p>
	 * if the rest of the cache is not long enough
	 * 
	 * @param bytecount
	 * @throws IOException 
	 */
	private void assertCacheHolds(int bytecount) throws IOException {

		if (bytecount >= CACHE_SIZE) {
			throw new IllegalArgumentException(
			        "Cache buffer to small to read that many bytes");
		}

		int remaining = CACHE_SIZE - this.cachePosition;
		if (remaining < bytecount) {
			// refill buffer
			for (int i = 0; i < remaining; i++) {
				this.cache[i] = this.cache[this.cachePosition + i];
			}
			
			//todo: save until where cache is valid, if file to long.
			int length = Math.min(CACHE_SIZE - remaining, this.in.available());
			this.inputStreamPosition += this.in.read(this.cache, remaining, length);
			//TODO: store how long buffer is valid
			this.cacheStart += this.cachePosition;
			this.cachePosition = 0;
		}
	}

	/**
	 * Reads an int with 32 bit from the stram.
	 * @return The int's value.
	 * @throws IOException If an IO error occured.
	 */
	public int read32() throws IOException {
		assertCacheHolds(4);

		byte byte0 = this.cache[this.cachePosition++];
		byte byte1 = this.cache[this.cachePosition++];
		byte byte2 = this.cache[this.cachePosition++];
		byte byte3 = this.cache[this.cachePosition++];
		
		int value =
		        (0xff & byte0) | ((0xff & byte1) << 8)
		                | ((0xff & byte2) << 16)
		                | ((0xff & byte3) << 24);
		return value;
	}

	/**
	 * Assumes to read the given data.
	 * 
	 * @param toRead
	 *            The array that the read bytes should be like.
	 * @throws IOException
	 *             If the read data does not match the given data.
	 */
	public void assumeToRead(byte[] toRead) throws IOException {
		assertCacheHolds(toRead.length);
		byte[] realRead = new byte[toRead.length];

		for (int i = 0; i < toRead.length; i++) {
			byte read = this.cache[this.cachePosition++];
			if (read != toRead[i]) {
				throw new IOException("IO error: expected to read " + toRead[i]
				        + " but got " + realRead[i]);
			}
		}
	}

	/**
	 * Reads a signed 16 bit value.
	 * @return The signed 16 bit value
	 * @throws IOException If an IO error occured.
	 */
	public int read16signed() throws IOException {
		int read = read16();
		if (read < 0x8000) {
			return read;
		} else {
			return read - 0x10000;
		}
	}

	/**
	 * Reads a byte from the stream.
	 * @return The byte's value.
	 * @throws IOException If an io error occured.
	 */
	public int read8() throws IOException {
		assertCacheHolds(1);
		return  0xff & this.cache[this.cachePosition++];
	}

	/**
	 * Reads a byte stream from the stream.
	 * <p>
	 * Warning: this is not guaranteed to work for long arrays.
	 * @param b The byte array to read to.
	 * @param off The offset in the array
	 * @param len The number of bytes to read.
	 * @return The number of really read bytes.
	 * @throws IOException If an io error occurred.
	 */
	public int read(byte[] b, int off, int len) throws IOException {
		assertCacheHolds(len);
		
		for (int i = 0; i < len; i++) {
			b[off + i] = this.cache[this.cachePosition++];
		}
		return len;
	}

	/**
	 * Skipps to a given position.
	 * 
	 * @param pos
	 *            The position to go to.
	 * @return The actual position we went to.
	 * @throws IOException If an IO error occured.
	 */
	public long skipTo(long pos) throws IOException {
		jumpCachePosition(pos);
		return this.cacheStart + this.cachePosition;
	}

	/**
	 * gets the number of read or skipped bytes. It is equal to the position in
	 * the stream, as long as no {@link IOException}s occurred and reset() was
	 * not used.
	 * 
	 * @return The number.
	 */
	public long getReadBytes() {
		return this.cacheStart + this.cachePosition;
	}

	/**
	 * Closes the underlying stream.
	 * @throws IOException If the close failed.
	 */
	public void close() throws IOException {
		this.in.close();
	}
}
