package net.wuffistrella.sauce_experiment.strings;

/**
 *
 */
public class EasyStringBuilder {

	private final StringBuilder internalStringBuilder =
		new StringBuilder ();

	public EasyStringBuilder reset () {
		internalStringBuilder.delete (0, internalStringBuilder.length ());
		return this;
	}

	public EasyStringBuilder appendCodePoint (
		int codePoint) {

		internalStringBuilder.appendCodePoint (codePoint);
		return this;
	}

	public EasyStringBuilder append (
		String string) {

		internalStringBuilder.append (string);
		return this;
	}

	public EasyStringBuilder append (
		int intValue) {

		internalStringBuilder.append (intValue);
		return this;
	}

	public String getString () {
		String string = internalStringBuilder.toString ();
		reset ();
		return string;
	}

}
