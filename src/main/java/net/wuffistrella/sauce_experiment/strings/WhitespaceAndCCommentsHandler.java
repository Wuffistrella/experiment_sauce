package net.wuffistrella.sauce_experiment.strings;

/**
 *
 */
public class WhitespaceAndCCommentsHandler
	implements IrrelevantInputHandler {

	private final StringPosition commentStart =
		new StringPosition ();

	/**
	 * Constructor.
	 */
	public WhitespaceAndCCommentsHandler () {
	}

	@Override
	public void skipIrrelevantInput (
		StringCursor inputCursor) {

		SkipInput:
		while (true) {
			boolean foundWhitespaceOrComment = false;

			while (
				Character.isWhitespace (
					inputCursor.getCurrentCodePoint ()
				)) {

				inputCursor.moveToNextCodePoint ();
				foundWhitespaceOrComment = true;
			}

			if (inputCursor.getCurrentCodePoint () == '/') {
				inputCursor.getCurrentPosition (commentStart);
				inputCursor.moveToNextCodePoint ();

				switch (inputCursor.getCurrentCodePoint ()) {
				case '/' -> {
					foundWhitespaceOrComment = true;
					inputCursor.moveToNextCodePoint ();

					SkipRestOfLine:
					while (true) {
						switch (inputCursor.getCurrentCodePoint ()) {
						case '\n' -> {
							// TODO handle other line end characters
							inputCursor.moveToNextCodePoint ();
							break SkipRestOfLine;
						}

						case StringPosition.STRING_END -> {
							break SkipInput;
						}
						}
					}
				}

				case '*' -> {
					foundWhitespaceOrComment = true;
					inputCursor.moveToNextCodePoint ();

					boolean previousCharacterWasAsterisk = false;
					SkipRestOfComment:
					while (true) {
						int codePoint = inputCursor.getCurrentCodePoint ();
						inputCursor.moveToNextCodePoint ();

						switch (codePoint) {
						case '*' -> {
							previousCharacterWasAsterisk = true;
						}

						case '/' -> {
							if (previousCharacterWasAsterisk) {
								break SkipRestOfComment;
							}
						}

						default -> {
							previousCharacterWasAsterisk = false;
						}

						case StringPosition.STRING_END -> {
							break SkipInput;
						}
						}
					}
				}

				default -> {
					inputCursor.moveToPosition (commentStart);
					break SkipInput;
				}
				}
			}

			if (!foundWhitespaceOrComment) {
				break SkipInput;
			}
		}
	}

	@Override
	public boolean isAtStartOfIrrelevantInput (
		StringCursor inputCursor) {

		int codePoint = inputCursor.getCurrentCodePoint ();

		if (Character.isWhitespace (codePoint)) {
			return true;

		} else if (inputCursor.getCurrentCodePoint () != '/') {
			return false;

		} else {
			boolean isAtStartOfIrrelevantInput;

			inputCursor.getCurrentPosition (commentStart);
			inputCursor.moveToNextCodePoint ();

			codePoint = inputCursor.getCurrentCodePoint ();

			isAtStartOfIrrelevantInput =
				(codePoint == '/' || codePoint == '*');

			inputCursor.moveToPosition (commentStart);
			return isAtStartOfIrrelevantInput;
		}
	}

}
