package net.wuffistrella.sauce_experiment.strings;

/**
 *
 */
public class PositionTrackingStringCursor
	extends BaseStringCursor {

	@SuppressWarnings ("ClassCanBeRecord") // TODO deal with this
	public static class Configuration {

		public final int tabWidth;

		public Configuration (
			int tabWidth) {

			if (tabWidth <= 0) {
				throw new IllegalArgumentException ("Tab width must be > 0.");
			}

			this.tabWidth = tabWidth;
		}

	}

	private final Configuration configuration;

	private String string;
	private int stringLength;
	private int stringIndex;
	private int currentLine;
	private int currentColumn;
	private int currentCodePoint;

	/**
	 * Constructor.
	 */
	public PositionTrackingStringCursor (
		Configuration configuration) {

		this.configuration = configuration;
	}

	@Override
	public void setInputString (
		String inputString) {

		if (inputString == null) {
			inputString = "";
			currentLine = StringPosition.POSITION_NOT_TRACKED;
			currentColumn = StringPosition.POSITION_NOT_TRACKED;

		} else {
			currentLine = 1;
			currentColumn = 1;
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
	public int getCurrentCodePoint () {
		return currentCodePoint;
	}

	@Override
	public void moveToNextCodePoint () {
		if (currentCodePoint == StringPosition.STRING_END) {
			return;
		}

		// TODO add support for other line end characters
		if (currentCodePoint == '\n') {
			++currentLine;
			currentColumn = 1;

		} else if (currentCodePoint == '\t') {
			int tabWidth = configuration.tabWidth;

			currentColumn =
				((currentColumn - 1) / tabWidth + 1)
					* tabWidth;

		} else {
			++currentColumn;
		}

		stringIndex += Character.charCount (currentCodePoint);

		if (stringIndex >= stringLength) {
			currentCodePoint = StringPosition.STRING_END;

		} else {
			currentCodePoint = string.codePointAt (stringIndex);
		}
	}

	@Override
	public void getCurrentPosition (
		StringPosition out) {

		out.stringIndex = stringIndex;
		out.line = currentLine;
		out.column = currentColumn;
	}

	@Override
	public int getCurrentLine () {
		return currentLine;
	}

	@Override
	public int getCurrentColumn () {
		return currentColumn;
	}

	@Override
	public void moveToPosition (
		StringPosition position) {

		stringIndex = position.stringIndex;
		currentLine = position.line;
		currentColumn = position.column;
		currentCodePoint = string.codePointAt (stringIndex);
	}

}
