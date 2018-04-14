package jsettlers.graphics.debug;

import java.io.File;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jsettlers.common.utils.Tuple;
import jsettlers.graphics.image.GuiImage;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.reader.AdvancedDatFileReader;
import jsettlers.graphics.reader.DatFileType;
import jsettlers.graphics.reader.SequenceList;
import jsettlers.graphics.image.sequence.Sequence;

/**
 * Small application that finds the mapping differences between dat files.
 * <p>
 * Outputs the image index map of the first to the second dat file.
 * <p>
 *
 * @author Michael Zangl
 */
public class DatFileMappingComparator {
	public static void main(String[] args) {
		if (args.length != 2) {
			usage();
		}
		File file1 = new File(args[0]);
		File file2 = new File(args[1]);

		ensureReadable(file1);
		ensureReadable(file2);

		if (file1.isDirectory() || file2.isDirectory()) {
			processGfxDictionaries(file1, file2);

		} else {
			compareDatFiles(file1, file2);
		}
	}

	private static void processGfxDictionaries(File gfxFolder1, File gfxFolder2) {
		List<File> datFiles1 = distinctFileNames(gfxFolder1.listFiles());
		List<File> datFiles2 = distinctFileNames(gfxFolder2.listFiles());

		if (datFiles1 == null || datFiles2 == null) {
			throw new IllegalArgumentException("At least one of the given paths is no directory.");
		}

		Map<String, File> datFiles2ByName = datFiles1.stream().map(file -> new Tuple<>(getDatFileName(file), file)).collect(Collectors.toMap(Tuple::getE1, Tuple::getE2));

		for (File file1 : datFiles1) {
			File file2 = datFiles2ByName.get(getDatFileName(file1));
			try {
				compareDatFiles(file1, file2);
			} catch (Exception e) {
				System.err.println("Error comparing files " + file1 + " and " + file2 + " : " + e.getMessage());
			}
		}
	}

	private static List<File> distinctFileNames(File[] files) {
		Arrays.sort(files);
		LinkedList<File> distinct = new LinkedList<>();
		for (File file : files) {
			if (distinct.isEmpty() || !getDatFileName(distinct.getLast()).equals(getDatFileName(file))) {
				distinct.add(file);
			}
		}
		return distinct;
	}

	private static String getDatFileName(File file) {
		return file.getName().split("\\.")[0];
	}

	private static void compareDatFiles(File file1, File file2) {
		AdvancedDatFileReader reader1 = new AdvancedDatFileReader(file1, DatFileType.getForPath(file1));
		AdvancedDatFileReader reader2 = new AdvancedDatFileReader(file2, DatFileType.getForPath(file2));

		System.out.println("Comparing settlers hashes for files " + file1 + " and " + file2);
		int[] settlersMapping = compareHashes(getSettlersHashes(reader1), getSettlersHashes(reader2));
		System.out.println("Comparing gui hashes for files " + file1 + " and " + file2);
		int[] guiMapping = compareHashes(getGuiHashes(reader1), getGuiHashes(reader2));


	}

	private static int[] compareHashes(List<Long> hashes1, List<Long> hashes2) {
		int[] mapping = new int[hashes1.size()];

		for (int i1 = 0; i1 < hashes1.size(); i1++) {
			Long h1 = hashes1.get(i1);
			int i2 = i1 < hashes2.size() && h1.equals(hashes2.get(i1)) ? i1 : hashes2.indexOf(h1);
			mapping[i1] = i2;
			System.out.println(i1 + " -> " + i2);
		}

		return mapping;
	}

	private static List<Long> getSettlersHashes(AdvancedDatFileReader reader) {
		SequenceList<Image> settlers = reader.getSettlers();

		return IntStream.range(0, settlers.size())
				.mapToObj(settlers::get)
				.map(sequence -> sequence.getImage(0))
				.map(DatFileMappingComparator::hash)
				.collect(Collectors.toList());
	}

	private static List<Long> getGuiHashes(AdvancedDatFileReader reader) {
		Sequence<GuiImage> sequence = reader.getGuis();

		return IntStream.range(0, sequence.length())
				.mapToObj(sequence::getImage)
				.map(DatFileMappingComparator::hash)
				.collect(Collectors.toList());
	}

	private static Long hash(Image image) {
		if (image instanceof SingleImage) {
			ShortBuffer data = ((SingleImage) image).getData();
			long hashCode = 1L;
			long multiplier = 1L;
			while (data.hasRemaining()) {
				multiplier *= 31L;
				hashCode += (data.get() + 27L) * multiplier;
			}
			return hashCode;
		} else {
			return 0L;
		}
	}

	private static void ensureReadable(File f2) {
		if (!f2.canRead()) {
			throw new IllegalArgumentException("Cannot read file " + f2);
		}
	}

	private static void usage() {
		System.err.println("Compares two GFX folders or two dat files:");
		System.err.println("./gradlew compareDatFiles -PF1=path1 -PF2=path2");
		System.exit(1);
	}
}
