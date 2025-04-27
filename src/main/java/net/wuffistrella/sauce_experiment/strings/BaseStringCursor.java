package net.wuffistrella.sauce_experiment.strings;

/**
 *
 */
public abstract class BaseStringCursor
	implements ReusableStringCursor {

	protected String string;
	protected int stringLength;
	protected int stringIndex;
	protected int currentCodePoint;

	/**
	 * Constructor.
	 */
	protected BaseStringCursor () {
	}

	@Override
	public int getCurrentCodePoint () {
		return currentCodePoint;
	}

	@Override
	public String getSubstring (
		int stringIndex_1,
		int stringIndex_2) {

		return stringIndex_1 < stringIndex_2
			? string.substring (stringIndex_1, stringIndex_2)
			: string.substring (stringIndex_2, stringIndex_1);
	}

	@Override
	public String getSubstring (
		StringPosition position_1,
		StringPosition position_2) {

		return getSubstring (
			position_1.stringIndex,
			position_2.stringIndex
		);
	}

	@Override
	public String getSubstringRelativeToCurrentPosition (
		int stringIndex) {

		return getSubstring (
			stringIndex,
			this.stringIndex
		);
	}

	@Override
	public String getSubstringRelativeToCurrentPosition (
		StringPosition position) {

		return getSubstring (
			position.stringIndex,
			this.stringIndex
		);
	}

}
