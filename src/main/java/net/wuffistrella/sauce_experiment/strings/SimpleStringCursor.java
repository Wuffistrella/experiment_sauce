package net.wuffistrella.sauce_experiment.strings;

/**
 *
 */
public class SimpleStringCursor
	extends BaseStringCursor {

	/**
	 * Constructor.
	 */
	public SimpleStringCursor () {
	}

	@Override
	public void setInputString (
		String inputString) {

		if (inputString == null) {
			inputString = "";
		}

		this.string = inputString;
		stringLength = string.length ();
		stringIndex = 0;

		currentCodePoint =
			!string.isEmpty ()
				? string.codePointAt (0)
				: StringPosition.STRING_END;
	}

	@Override
	public void moveToNextCodePoint () {
		if (currentCodePoint == StringPosition.STRING_END) {
			return;
		}

		if (stringIndex >= stringLength) {
			currentCodePoint = StringPosition.STRING_END;
			return;
		}

		stringIndex += Character.charCount (currentCodePoint);

		if (stringIndex < stringLength) {
			currentCodePoint = string.codePointAt (stringIndex);
		} else {
			currentCodePoint = StringPosition.STRING_END;
		}
	}

	@Override
	public void getCurrentPosition (
		StringPosition out) {

		out.stringIndex = stringIndex;
		out.line = StringPosition.POSITION_NOT_TRACKED;
		out.column = StringPosition.POSITION_NOT_TRACKED;
	}

	@Override
	public int getCurrentLine () {
		return StringPosition.POSITION_NOT_TRACKED;
	}

	@Override
	public int getCurrentColumn () {
		return StringPosition.POSITION_NOT_TRACKED;
	}

	@Override
	public void moveToPosition (
		StringPosition position) {

		stringIndex = position.stringIndex;
		currentCodePoint = string.codePointAt (stringIndex);
	}

}
