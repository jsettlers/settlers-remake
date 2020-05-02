package jsettlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.stream.IntStream;

public class CharacterExtractor {
	public static void main(String[] args) {
		if(args.length == 1) {
			extract(args[0]);
		} else {
			System.err.println("filename needed");
		}
	}

	private static void extract(String filename) {
		File f = new File(filename);
		StringBuilder content = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String line;
			while((line = reader.readLine()) != null) {
				content.append(line);
			}
		} catch(Throwable thrown) {
			thrown.printStackTrace();
		}


		Object[] specialCharacters = IntStream.range(0, content.length()).mapToObj(content::charAt).filter(character -> character > 127).distinct().sorted().toArray();
		for(Object specialCharacter : specialCharacters) System.out.print(specialCharacter);
	}
}
