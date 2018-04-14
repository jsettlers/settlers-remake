package jsettlers.graphics.debug;

import java.io.File;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.reader.AdvancedDatFileReader;
import jsettlers.graphics.reader.DatFileType;
import jsettlers.graphics.reader.SequenceList;

/**
 * Small application that finds the mapping differences between dat files.
 * <p>
 * Outputs the image index map of the first to the second dat file.
 * <p>
 * @author Michael Zangl
 */
public class DatFileMappingComparator {
	public static void main(String[] args) {
		if (args.length != 2) {
			usage();
		}
		File f1 = new File(args[0]);
		File f2 = new File(args[1]);
		
		ensureReadable(f1);
		ensureReadable(f2);
		
		AdvancedDatFileReader reader1 = new AdvancedDatFileReader(f1, DatFileType.getForPath(f1));
		AdvancedDatFileReader reader2 = new AdvancedDatFileReader(f2, DatFileType.getForPath(f2));
		
		List<Long> hashes1 = getSettlersHashes(reader1);
		List<Long> hashes2 = getSettlersHashes(reader2);
		System.out.println("File 1: Found " + hashes1.size() + " settlers");
		System.out.println("File 2: Found " + hashes2.size() + " settlers");
		
		for (int i1 = 0; i1 < hashes1.size(); i1++) {
			Long h1 = hashes1.get(i1);
			int i2 = i1 < hashes2.size() && h1.equals(hashes2.get(i1)) ? i1 : hashes2.indexOf(h1);
			System.out.println(i1 + " -> " + i2);
		}
	}

	private static List<Long> getSettlersHashes(AdvancedDatFileReader reader1) {
		SequenceList<Image> settlers = reader1.getSettlers();
		
		return IntStream.range(0, settlers.size())
				.mapToObj(settlers::get)
				.map(sequence -> hash(sequence.getImage(0)))
				.collect(Collectors.toList());
	}

	private static Long hash(Image image) {
		if (image instanceof SettlerImage) {
			ShortBuffer data = ((SettlerImage) image).getData();
			long hashCode = 1L;
		    long multiplier = 1L;
			while (data.hasRemaining()) {
		        multiplier *= 31L;
		        hashCode += (data.get() + 27L) * multiplier;
			}
			return hashCode;
		} else {
			return 0l;
		}
	}

	private static void ensureReadable(File f2) {
		if (!f2.canRead()) {
			System.err.println("Cannot read: " + f2.getAbsolutePath());
			System.exit(1);
		}
	}

	private static void usage() {
		System.err.println("Usage:");
		System.err.println("./gradlew compareDatFiles -PF1=path1 -PF2=path2");
		System.exit(1);
	}
}
