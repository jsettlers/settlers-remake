package jsettlers.graphics.debug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import java8.util.stream.Collectors;
import jsettlers.common.utils.Tuple;
import jsettlers.graphics.image.reader.AdvancedDatFileReader;
import jsettlers.graphics.image.reader.DatFileType;
import jsettlers.graphics.image.reader.DatFileUtils;
import jsettlers.graphics.image.reader.versions.IndexingDatFileMapping;
import jsettlers.graphics.image.reader.versions.IndexingGfxFolderMapping;
import jsettlers.graphics.image.reader.versions.SettlersVersionMapping;

import static java8.util.stream.StreamSupport.stream;
import static jsettlers.graphics.image.reader.DatFileUtils.distinctFileNames;
import static jsettlers.graphics.image.reader.DatFileUtils.getDatFileIndex;
import static jsettlers.graphics.image.reader.DatFileUtils.getDatFileName;

/**
 * Small application that finds the mapping differences between dat files.
 * <p>
 * Outputs the image index map of the first to the second dat file.
 * <p>
 *
 * @author Michael Zangl
 */
public class DatFileMappingComparator {

	private static final String GRAPHICS_RESOURCE_DIRECTORY = "jsettlers.graphics/src/main/resources/jsettlers/graphics/image/reader/versions/";

	public static void main(String[] args) {
		if (args.length < 2) {
			printUsage();
		}
		File file1 = new File(args[0]);
		File file2 = new File(args[1]);

		ensureReadable(file1);
		ensureReadable(file2);

		if (file1.isDirectory() || file2.isDirectory()) {
			if (args.length < 3) {
				printUsage();
			}

			processGfxDictionaries(file1, file2, args[2]);

		} else {
			compareDatFiles(file1, file2);
		}
	}

	private static void processGfxDictionaries(File gfxFolder1, File gfxFolder2, String settlersVersionName) {
		List<File> datFiles1 = distinctFileNames(gfxFolder1.listFiles());
		List<File> datFiles2 = distinctFileNames(gfxFolder2.listFiles());

		if (datFiles1 == null || datFiles2 == null) {
			throw new IllegalArgumentException("At least one of the given paths is no directory.");
		}

		String settlersVersionHash = DatFileUtils.generateOriginalVersionId(gfxFolder2);

		Map<String, File> datFiles2ByName = stream(datFiles2).map(file -> new Tuple<>(getDatFileName(file), file)).collect(Collectors.toMap(Tuple::getE1, Tuple::getE2));

		int highestIndex = datFiles1.stream().mapToInt(DatFileUtils::getDatFileIndex).max().orElse(0);
		IndexingDatFileMapping[] datFileMappings = new IndexingDatFileMapping[highestIndex + 1];

		for (File file1 : datFiles1) {
			File file2 = datFiles2ByName.get(getDatFileName(file1));

			if (file2 == null) {
				System.out.println("Could not find partner for file " + file1);
				continue;
			}

			try {
				int index = getDatFileIndex(file1);
				datFileMappings[index] = compareDatFiles(file1, file2);
			} catch (Exception e) {
				System.err.println("Error comparing files " + file1 + " and " + file2 + " :");
				e.printStackTrace();
			}
		}

		try {
			String fileName = settlersVersionName + ".json";
			String filePath = GRAPHICS_RESOURCE_DIRECTORY + fileName;
			new IndexingGfxFolderMapping(settlersVersionHash, datFileMappings).serializeToStream(new FileOutputStream(filePath));

			SettlersVersionMapping settlersVersionMapping = SettlersVersionMapping.readFromDirectory(GRAPHICS_RESOURCE_DIRECTORY);
			settlersVersionMapping.putMapping(settlersVersionHash, fileName);
			settlersVersionMapping.serializeToDirectory(GRAPHICS_RESOURCE_DIRECTORY);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static IndexingDatFileMapping compareDatFiles(File file1, File file2) {
		AdvancedDatFileReader reader1 = new AdvancedDatFileReader(file1, DatFileType.getForPath(file1));
		AdvancedDatFileReader reader2 = new AdvancedDatFileReader(file2, DatFileType.getForPath(file2));

		System.out.println("Comparing settlers hashes for files " + file1 + " and " + file2);
		int[] settlersMapping = reader1.getSettlersHashes().compareAndCreateMapping(reader2.getSettlersHashes());
		System.out.println("Comparing gui hashes for files " + file1 + " and " + file2);
		int[] guiMapping = reader1.getGuiHashes().compareAndCreateMapping(reader2.getGuiHashes());

		return new IndexingDatFileMapping(settlersMapping, guiMapping);
	}

	private static void ensureReadable(File f2) {
		if (!f2.canRead()) {
			throw new IllegalArgumentException("Cannot read file " + f2);
		}
	}

	private static void printUsage() {
		System.err.println("Compares two GFX folders or two dat files:");
		System.err.println("To compare two dat files, start the program with the paths of the two files as arguments");
		System.err.println("To compare two dat gfx folders, start the program with the paths of the two gfx folders and the name of the settlers version. A mapping file will be created"
			+ " in the resources of jsettlers.graphics");
		System.exit(1);
	}
}
