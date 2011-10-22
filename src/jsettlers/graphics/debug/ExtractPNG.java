package jsettlers.graphics.debug;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This file extracts PNGs out of a file.
 * @author michael
 *
 */
public class ExtractPNG {

	private static final String NAME = "out";

	private static byte[] START = { (byte) 0x89, 0x50, 0x4e, 0x47 };

	private static byte[] END =
	        { 0x45, 0x4e, 0x44, (byte) 0xae, 0x42, 0x60, (byte) 0x82 };

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/media/2F35-CAAF/siedler-bild");
		FileInputStream stream = new FileInputStream(file);
		byte[] bytes = new byte[stream.available()];
		stream.read(bytes);
		stream.close();

		int imageNumber = 0;
		boolean nextImageFound = true;
		int currentPos = 0;

		while (nextImageFound) {
			int start = searchFor(START, bytes, currentPos);
			int end = searchFor(END, bytes, start) + END.length;
			currentPos = end;

			nextImageFound = start >= 0 && end >= 7;

			if (nextImageFound) {
				System.out.println("Start: " + start + ", end: " + end
				        + ", name: " + NAME + "-" + imageNumber + ".png");

				
//				File out =
//				        new File("/media/2F35-CAAF/" + NAME + "-" + imageNumber
//				                + ".png, data:");
				byte[] outData = new byte[end - start];

				hexdumpLine(file, start-12, 12);
				
				FileInputStream in = new FileInputStream(file);
				in.skip(start);
				in.read(outData);
				in.close();

//				
//				FileOutputStream outstream = new FileOutputStream(out);
//				outstream.write(outData);
//				outstream.close();

				imageNumber++;
			}
		}
		System.out.print("done.");
		stream.close();
	}

	private static int searchFor(byte[] needle, byte[] haystack, int start) {
		for (int i = start; i < haystack.length - needle.length; i++) {
			if (compareBytes(needle, haystack, i)) {
				return i;
			}
		}
		return -1;
	}

	private static boolean compareBytes(byte[] toSearch, byte[] original,
	        int originalOffset) {
		for (int i = 0; i < toSearch.length; i++) {
			if (toSearch[i] != original[i + originalOffset]) {
				return false;
			}
		}
		return true;
	}
	
	private static void hexdumpLine(File file, int offset, int len) throws IOException {
		byte[] bytes = new byte[len];
		
		FileInputStream stream = new FileInputStream(file);
		stream.skip(offset);
		stream.read(bytes);
		stream.close();
		
		for (int i = 0; i < len; i++) {
			String asHex = Integer.toHexString(bytes[i]);
			System.out.print(asHex + " ");
		}
		System.out.println("");
	}

}
