package jsettlers.graphics.debug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

import jsettlers.graphics.reader.bytereader.ByteReader;
import jsettlers.graphics.sound.SoundManager;
import jsettlers.graphics.swing.SwingResourceLoader;

public class ExtractSounds extends SoundManager {

	private ExtractSounds() {
		super(null);
	}

	public static void main(String[] args) throws IOException {
		SwingResourceLoader.setupSwingPaths();

		ByteReader file = openSoundFile();
		int[][] starts = getSoundStarts(file);

		final JFileChooser fc = new JFileChooser();

		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File base = fc.getSelectedFile();

			for (int i = 0; i < starts.length; i++) {
				File groupDir = new File(base, "" + i);
				groupDir.mkdir();

				for (int j = 0; j < starts[i].length; j++) {
					File seqFile = new File(groupDir, j + ".wav");
					short[] data = getSoundData(file, starts[i][j]);
					exportTo(data, seqFile);
					System.out.println("Exported file " + i + "." + j);
				}
			}
		}
	}

	/**
	 * Export data to a given wav file path.
	 * 
	 * @param data
	 * @param seqFile
	 * @throws IOException
	 */
	private static void exportTo(short[] data, File seqFile) throws IOException {
		FileOutputStream os = new FileOutputStream(seqFile);
		os.write(new byte[] { 'R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'A', 'V',
				'E', 'f', 'm', 't', ' ', 0x10, 0, 0, 0, 1, 0, 2, 0, 0x22,
				(byte) 0x56, 0, 0, 0x10, (byte) 0xb1, 2, 0, 4, 0, 0x10, 0, 'd',
				'a', 't', 'a' });
		for (short d : data) {
			os.write(d & 0xff);
			os.write((d / 256) & 0xff);
			os.write(d & 0xff);
			os.write((d / 256) & 0xff);
		}
		
		os.close();
	}
}
