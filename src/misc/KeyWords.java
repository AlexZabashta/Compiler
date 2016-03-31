package misc;

import java.util.HashSet;
import java.util.Set;

public class KeyWords {

	private static final Set<String> keyWords = new HashSet<String>();
	static {
		// keyWords.add("do");
		keyWords.add("if");
		keyWords.add("for");
		keyWords.add("else");
		keyWords.add("while");
		keyWords.add("return");
		keyWords.add("break");
		keyWords.add("continue");
	}

	public static boolean contains(String word) {
		return keyWords.contains(word);
	}
}
