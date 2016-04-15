package misc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Types {

	public static int getType(String type) {
		if (type == null) {
			throw new RuntimeException("Type can't be null.");
		}

		if (type.equals("void")) {
			return 0;
		}

		type = type.toLowerCase();
		String[] levelOfType = { "int", "string", "text" };

		for (int i = 0; i < levelOfType.length; i++) {
			if (type.equals(levelOfType[i])) {
				return i + 1;
			}

			if (type.startsWith(levelOfType[i] + '_')) {
				try {
					int level = Integer.parseInt(type.substring(levelOfType.length + 1));
					if (level < 0) {
						throw new NumberFormatException("Negative level of " + type);
					}
					return level + i + 1;
				} catch (NumberFormatException error) {
					throw new RuntimeException(error.getMessage());
				}
			}
		}

		throw new RuntimeException("Unknown type " + type);
	}

	public static int getType(String type, List<String> errors) {
		if (type == null) {
			errors.add("Type can't be null.");
			return -1;
		}

		if (type.equals("void")) {
			return 0;
		}

		type = type.toLowerCase();
		String[] levelOfType = { "int", "string", "text" };

		for (int i = 0; i < levelOfType.length; i++) {
			if (type.equals(levelOfType[i])) {
				return i + 1;
			}

			if (type.startsWith(levelOfType[i] + '_')) {
				try {
					int level = Integer.parseInt(type.substring(levelOfType.length + 1));
					if (level < 0) {
						throw new NumberFormatException("Negative level of " + type);
					}
					return level + i + 1;
				} catch (NumberFormatException error) {
					errors.add(error.getMessage());
					return i + 1;
				}
			}
		}

		errors.add("Unknown type " + type);
		return -1;
	}
}
