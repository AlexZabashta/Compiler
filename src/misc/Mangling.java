package misc;

public class Mangling {
	public static String convert(int id) {
		if (id == 0) {
			return "a";
		} else {
			String name = "";

			while (id > 0) {
				name += (char) ((id % 26) + 'a');
				id /= 26;
			}
			return name;
		}
	}
}
