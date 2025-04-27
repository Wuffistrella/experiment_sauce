package net.wuffistrella.sauce_experiment.strings;

/**
 * Object that represents a position in a string.
 */
public class StringPosition {

	public static final int STRING_END = -1;

	/**
	 * Line or column is set to this value if it isn't tracked.
	 */
	public static final int POSITION_NOT_TRACKED = -1;

	/**
	 * Index of the character in string.
	 */
	public int stringIndex;

	/**
	 * Line on which the character is located.
	 */
	public int line;

	/**
	 * Column on which the character is located.
	 */
	public int column;

	/**
	 * Clone object.
	 */
	public StringPosition createCopy () {
		StringPosition copy = new StringPosition ();

		copyTo (copy);

		return copy;
	}

	/**
	 * Copy data to another object.
	 */
	public void copyTo (
		StringPosition out) {

		out.stringIndex = this.stringIndex;
		out.line = this.line;
		out.column = this.column;
	}

}
