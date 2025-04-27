package net.wuffistrella.sauce_experiment.exceptions;

import java.util.Arrays;

/**
 *
 */
public class ErrorMessageSanitizer {

	public static String sanitize (
		String string) {

		string = string.replaceAll (System.lineSeparator (), "\\n");
		string = string.replaceAll ("\\\\", "\\\\");
		return string;
	}

	public static String asSanitized (
		int codePoint) {

		char[] chars = Character.toChars (codePoint);
		String string = Arrays.toString (chars);
		return sanitize (string);
	}

}
