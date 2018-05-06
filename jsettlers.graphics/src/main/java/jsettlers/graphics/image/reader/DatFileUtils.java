/*
 * Copyright (c) 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package jsettlers.graphics.image.reader;

import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import jsettlers.graphics.image.reader.AdvancedDatFileReader;
import jsettlers.graphics.image.reader.DatFileType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static java8.util.stream.StreamSupport.stream;

public class DatFileUtils {

	public static String generateOriginalVersionId(File gfxDirectory) {
		File[] gfxDatFiles = gfxDirectory.listFiles();
		List<File> distinctGfxDatFiles = distinctFileNames(gfxDatFiles);

		List<Long> allHashes = stream(distinctGfxDatFiles)
				.filter(file -> file.getName().toLowerCase().endsWith(".dat"))
				.map(datFile -> new AdvancedDatFileReader(datFile, DatFileType.getForPath(datFile))).map(reader -> {
					List<Long> hashes = new ArrayList<>();
					hashes.addAll(reader.getSettlersHashes());
					hashes.addAll(reader.getGuiHashes());
					return hashes;
				}).flatMap(StreamSupport::stream).collect(Collectors.toList());

		long hashCode = 1L;
		long multiplier = 1L;
		for (Long hash : allHashes) {
			multiplier *= 31L;
			hashCode += (hash + 27L) * multiplier;
		}
		return Long.toString(hashCode);
	}

	public static List<File> distinctFileNames(File[] files) {
		Arrays.sort(files);
		LinkedList<File> distinct = new LinkedList<>();
		for (File file : files) {
			if (distinct.isEmpty() || !getDatFileName(distinct.getLast()).equalsIgnoreCase(getDatFileName(file))) {
				distinct.add(file);
			}
		}
		return distinct;
	}

	public static String getDatFileName(File file) {
		return file.getName().split("\\.")[0].toLowerCase();
	}

	public static int getDatFileIndex(File datFile) {
		return Integer.valueOf(getDatFileName(datFile).split("_")[1]);
	}
}
