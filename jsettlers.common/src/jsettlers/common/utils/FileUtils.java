package jsettlers.common.utils;

import java.io.File;
import java.io.IOException;

public class FileUtils {

	public interface IFileVisitor {
		void visitFile(File file) throws IOException;
	}

	public static void walkFileTree(File directory, IFileVisitor fileVisitor) throws IOException {
		for (File file : directory.listFiles()) {
			fileVisitor.visitFile(file);
			if (file.isDirectory()) {
				walkFileTree(file, fileVisitor);
			}
		}
	}
}
