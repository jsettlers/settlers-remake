package jsettlers;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Collection;

import jsettlers.common.CommonConstants;
import jsettlers.common.resources.ResourceManager;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.save.MapFileHeader;
import jsettlers.logic.map.save.MapList;
import jsettlers.main.replay.ReplayTool;
import jsettlers.tests.utils.CountingInputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AutoReplayIT {
	static {
		CommonConstants.ENABLE_CONSOLE_LOGGING = true;
		CommonConstants.CONTROL_ALL = true;

		TestUtils.setupResourcesManager();
	}

	private static final String remainingReplay = "out/remainingReplay.log";
	private static final Object lock = new Object();

	@Parameters(name = "{index}: {0} : {1}")
	public static Collection<Object[]> replaySets() {
		return Arrays.asList(new Object[][] {
				{ "basicProduction-mountainlake", 15 },
				{ "fullProduction-mountainlake", 10 },
				{ "fullProduction-mountainlake", 20 },
				{ "fullProduction-mountainlake", 30 },
				{ "fullProduction-mountainlake", 40 },
				{ "fullProduction-mountainlake", 50 },
				{ "fullProduction-mountainlake", 69 }
		});
	}

	private final String folderName;
	private final int targetTimeMinutes;

	public AutoReplayIT(String folderName, int targetTimeMinutes) {
		this.folderName = folderName;
		this.targetTimeMinutes = targetTimeMinutes;
	}

	@Test
	public void testReplay() throws IOException {
		synchronized (lock) {
			Path savegameFile = replayAndGetSavegame(getReplayPath(), targetTimeMinutes);
			Path expectedFile = getSavegamePath();

			compareMapFiles(expectedFile, savegameFile);
		}
	}

	private Path getSavegamePath() {
		return Paths.get("resources/autoreplay/" + folderName + "/savegame-" + targetTimeMinutes + "m.map");
	}

	private Path getReplayPath() {
		return Paths.get("resources/autoreplay/" + folderName + "/replay.log");
	}

	private static void compareMapFiles(Path expectedFile, Path actualFile) throws IOException {
		System.out.println("Comparing expected '" + expectedFile + "' with actual '" + actualFile + "'");

		BufferedInputStream expectedStream = new BufferedInputStream(Files.newInputStream(expectedFile));
		MapFileHeader expectedHeader = MapFileHeader.readFromStream(expectedStream);
		CountingInputStream actualStream = new CountingInputStream(new BufferedInputStream(Files.newInputStream(actualFile)));
		MapFileHeader actualHeader = MapFileHeader.readFromStream(actualStream);

		assertEquals(expectedHeader.getBaseMapId(), actualHeader.getBaseMapId());

		int e, a;
		while ((e = expectedStream.read()) != -1 & (a = actualStream.read()) != -1) {
			assertEquals("difference at byte " + (actualStream.getByteCounter() - 1), a, e);
		}
		assertEquals("files have different lengths", e, a);

		expectedStream.close();
		actualStream.close();
	}

	private static Path replayAndGetSavegame(Path replayPath, int targetTimeMinutes) throws IOException {
		Constants.FOG_OF_WAR_DEFAULT_ENABLED = false;
		ReplayTool.replayAndCreateSavegame(replayPath.toFile(), targetTimeMinutes * 60 * 1000, remainingReplay);

		Path savegameFile = findSavegameFile();
		System.out.println("Replayed: " + replayPath + " and created savegame: " + savegameFile);
		return savegameFile;
	}

	private static Path findSavegameFile() throws IOException { // TODO implement better way to find the correct savegame
		Path saveDirPath = new File(ResourceManager.getSaveDirectory(), "save").toPath();

		final Path[] newestFile = new Path[1];
		Files.walkFileTree(saveDirPath, new SimpleFileVisitor<Path>() {
			private FileTime newestCreationTime = null;

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (file.toString().endsWith(MapList.MAP_EXTENSION)
						&& (newestCreationTime == null || newestCreationTime.compareTo(attrs.creationTime()) < 0)) {
					newestCreationTime = attrs.creationTime();
					newestFile[0] = file;
				}
				return super.visitFile(file, attrs);
			}
		});

		return newestFile[0];
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Creating reference files for replays...");

		for (Object[] replaySet : replaySets()) {
			String folderName = (String) replaySet[0];
			int targetTimeMinutes = (Integer) replaySet[1];

			AutoReplayIT replayIT = new AutoReplayIT(folderName, targetTimeMinutes);
			Path savegame = AutoReplayIT.replayAndGetSavegame(replayIT.getReplayPath(), targetTimeMinutes);
			Path expectedSavegamePath = replayIT.getSavegamePath();

			Files.move(savegame, expectedSavegamePath, StandardCopyOption.REPLACE_EXISTING);
			System.out.println("Moved savegame '" + savegame + "' to expected location '" + expectedSavegamePath + "'");
		}
	}
}
