package net.wuffistrella.sauce_experiment.strings;

/**
 *
 */
public interface StringCursor {

	int getCurrentCodePoint ();

	void moveToNextCodePoint ();

	void getCurrentPosition (
		StringPosition out);

	int getCurrentLine ();

	int getCurrentColumn ();

	void moveToPosition (
		StringPosition position);

	String getSubstring (
		int stringIndex_1,
		int stringIndex_2);

	String getSubstring (
		StringPosition position_1,
		StringPosition position_2);

	String getSubstringRelativeToCurrentPosition (
		int stringIndex);

	String getSubstringRelativeToCurrentPosition (
		StringPosition position);

}
