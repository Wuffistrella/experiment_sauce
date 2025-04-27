package net.wuffistrella.sauce_experiment.lexer;

import net.wuffistrella.sauce_experiment.strings.LogStringSanitizer;
import net.wuffistrella.sauce_experiment.exceptions.SauceParseError;
import net.wuffistrella.sauce_experiment.exceptions.SauceParseErrorFactory;
import net.wuffistrella.sauce_experiment.exceptions.SauceParseErrorType;
import net.wuffistrella.sauce_experiment.exceptions.StringCursorNotReusableException;
import net.wuffistrella.sauce_experiment.strings.EasyStringBuilder;
import net.wuffistrella.sauce_experiment.strings.IrrelevantInputHandler;
import net.wuffistrella.sauce_experiment.strings.ReusableStringCursor;
import net.wuffistrella.sauce_experiment.strings.SimpleStringCursor;
import net.wuffistrella.sauce_experiment.strings.StringCursor;
import net.wuffistrella.sauce_experiment.strings.StringPosition;

/**
 *
 */
public class SauceLexer
	implements SauceLexemeSource {

	private final StringCursor inputCursor;
	private final IrrelevantInputHandler irrelevantInputHandler;
	private final SauceParseErrorFactory errorFactory;

	private final StringPosition lexemeStartPosition =
		new StringPosition ();

	private final StringPosition possibleTerminatorPosition =
		new StringPosition ();

	private final StringPosition delimiterTagBlockStartPosition =
		new StringPosition ();

	private final StringPosition delimiterTagStartPosition =
		new StringPosition ();

	private final StringPosition rawStringContentStartPosition =
		new StringPosition ();

	private final EasyStringBuilder lexemeRawContentBuilder =
		new EasyStringBuilder ();

	private final EasyStringBuilder lexemeValueContentBuilder =
		new EasyStringBuilder ();

	private final SimpleStringCursor delimiterTagCursor =
		new SimpleStringCursor ();

	/**
	 * Constructor.
	 */
	public SauceLexer (
		StringCursor inputCursor,
		IrrelevantInputHandler irrelevantInputHandler,
		SauceParseErrorFactory errorFactory) {

		this.inputCursor = inputCursor;
		this.irrelevantInputHandler = irrelevantInputHandler;
		this.errorFactory = errorFactory;
	}

	public void setInputString (
		String inputString) {

		try {
			((ReusableStringCursor) inputCursor).setInputString (inputString);

		} catch (ClassCastException e) {
			throw new StringCursorNotReusableException (
				"The string cursor of the lexer is not reusable."
			);
		}
	}

	@Override
	public boolean nextLexeme (
		SauceLexeme out)
		throws SauceParseError {

		try {
			lexemeRawContentBuilder.reset ();
			lexemeValueContentBuilder.reset ();

			irrelevantInputHandler.skipIrrelevantInput (inputCursor);

			int codePoint = inputCursor.getCurrentCodePoint ();
			inputCursor.getCurrentPosition (lexemeStartPosition);

			switch (codePoint) {
			case StringPosition.STRING_END -> {
				out.type = SauceLexemeType.CodeEnd;
				out.setContentToNone ();
				out.setPosition (
					inputCursor.getCurrentLine (),
					inputCursor.getCurrentColumn ()
				);
				return false;
			}

			case '.' -> {
				out.type = SauceLexemeType.StatementTerminator;
				out.setContentToNone ();

				inputCursor.moveToNextCodePoint ();
				codePoint = inputCursor.getCurrentCodePoint ();

				switch (codePoint) {
				case StringPosition.STRING_END,
					'(',
					')',
					'{',
					'}' -> {
					// noop
				}

				default -> {
					if (!irrelevantInputHandler
						.isAtStartOfIrrelevantInput (inputCursor)) {

						lexemeRawContentBuilder.append (".");
						readRestOfWord (out);
					}
				}
				}
			}

			case '(' -> {
				inputCursor.moveToNextCodePoint ();

				out.type = SauceLexemeType.CheeseOpenDelimiter;
				out.setContentToNone ();
			}

			case ')' -> {
				inputCursor.moveToNextCodePoint ();

				out.type = SauceLexemeType.CheeseCloseDelimiter;
				out.setContentToNone ();
			}

			case '{' -> {
				inputCursor.moveToNextCodePoint ();

				out.type = SauceLexemeType.BodyBlockOpenDelimiter;
				out.setContentToNone ();
			}

			case '}' -> {
				inputCursor.moveToNextCodePoint ();

				out.type = SauceLexemeType.BodyBlockCloseDelimiter;
				out.setContentToNone ();
			}

			case '\'', '\"', '`' -> {
				lexemeRawContentBuilder.appendCodePoint (codePoint);
				inputCursor.moveToNextCodePoint ();
				readRestOfString (out, codePoint, null);
			}

			case '<' -> {
				inputCursor.moveToNextCodePoint ();
				readRestOfRawString (out);
			}

			default -> {
				lexemeRawContentBuilder.appendCodePoint (codePoint);
				inputCursor.moveToNextCodePoint ();
				readRestOfWord (out);
			}
			}

			out.setPosition (lexemeStartPosition);
			return true;

		} catch (SauceParseError parseError) {
			throw parseError;

		} catch (Exception otherException) {
			throw errorFactory.createExceptionObject (
				SauceParseErrorType.UnknownError,
				otherException,
				inputCursor.getCurrentLine (),
				inputCursor.getCurrentColumn ()
			);
		}
	}

	private void readRestOfWord (
		SauceLexeme out)
		throws SauceParseError {

		int codePoint = inputCursor.getCurrentCodePoint ();
		boolean isStringTag = false;

		ScanWord:
		while (true) {
			if (irrelevantInputHandler
				.isAtStartOfIrrelevantInput (inputCursor)) {

				break ScanWord;
			}

			switch (codePoint) {
			case StringPosition.STRING_END,
				'(', ')', '{', '}', '<' -> {
				break ScanWord;
			}

			case '\'', '\"', '`' -> {
				isStringTag = true;
				break ScanWord;
			}

			case '.' -> {
				inputCursor.getCurrentPosition (possibleTerminatorPosition);

				inputCursor.moveToNextCodePoint ();
				codePoint = inputCursor.getCurrentCodePoint ();

				switch (codePoint) {
				case StringPosition.STRING_END,
					'(', ')', '{', '}', '<' -> {
					inputCursor.moveToPosition (possibleTerminatorPosition);
					break ScanWord;
				}

				case '\'', '\"', '`' -> {
					lexemeRawContentBuilder.appendCodePoint ('.');
					isStringTag = true;
					break ScanWord;
				}

				default -> {
					if (irrelevantInputHandler
						.isAtStartOfIrrelevantInput (inputCursor)) {

						inputCursor.moveToPosition (possibleTerminatorPosition);
						break ScanWord;
					}

					lexemeRawContentBuilder.appendCodePoint ('.');
				}
				}
			}

			default -> {
				lexemeRawContentBuilder.appendCodePoint (codePoint);
				inputCursor.moveToNextCodePoint ();
			}
			}

			codePoint = inputCursor.getCurrentCodePoint ();
		}

		if (isStringTag) {
			String stringTag = lexemeRawContentBuilder.getString ();
			readRestOfString (out, codePoint, stringTag);

		} else {
			out.type = SauceLexemeType.Word;
			out.setContent (lexemeRawContentBuilder.getString ());
		}
	}

	private void readRestOfString (
		SauceLexeme out,
		int openDelimiterCodePoint,
		String tag)
		throws SauceParseError {

		int codePoint = inputCursor.getCurrentCodePoint ();

		ScanString:
		while (true) {
			switch (codePoint) {
			case StringPosition.STRING_END -> {
				throw errorFactory.createExceptionObject (
					SauceParseErrorType.AtCodeEnd_NoStringCloseQuote,
					lexemeStartPosition.line,
					lexemeStartPosition.column
				);
			}

			case '\'', '\"', '`' -> {
				lexemeRawContentBuilder.appendCodePoint (codePoint);

				if (codePoint == openDelimiterCodePoint) {
					inputCursor.moveToNextCodePoint ();
					break ScanString;

				} else {
					lexemeValueContentBuilder.appendCodePoint (codePoint);
					inputCursor.moveToNextCodePoint ();
					codePoint = inputCursor.getCurrentCodePoint ();
				}
			}

			default -> {
				lexemeRawContentBuilder.appendCodePoint (codePoint);
				lexemeValueContentBuilder.appendCodePoint (codePoint);
				inputCursor.moveToNextCodePoint ();
				codePoint = inputCursor.getCurrentCodePoint ();
			}

			case '\\' -> {
				lexemeRawContentBuilder.appendCodePoint (codePoint);
				inputCursor.moveToNextCodePoint ();
				codePoint = inputCursor.getCurrentCodePoint ();

				if (openDelimiterCodePoint == '`') {
					if (codePoint == '`') {
						lexemeValueContentBuilder.appendCodePoint ('`');

					} else {
						lexemeValueContentBuilder.appendCodePoint ('\\');
						lexemeValueContentBuilder.appendCodePoint (codePoint);
						inputCursor.moveToNextCodePoint ();
						codePoint = inputCursor.getCurrentCodePoint ();
					}

				} else {
					switch (codePoint) {
					case '\\', '\'', '\"' -> {
						lexemeRawContentBuilder.appendCodePoint ('\\');
						lexemeValueContentBuilder.appendCodePoint (codePoint);
					}

					default -> {
						throw errorFactory.createExceptionObject (
							SauceParseErrorType.InString_InvalidEscapeCode,
							inputCursor.getCurrentLine (),
							inputCursor.getCurrentColumn ()
						);
					}
					}
				}
			}
			}
		}

		switch (openDelimiterCodePoint) {
		case '\'' -> {
			out.type = tag == null
				? SauceLexemeType.SingleQuoteString
				: SauceLexemeType.TaggedSingleQuoteString;
		}

		case '\"' -> {
			out.type = tag == null
				? SauceLexemeType.DoubleQuoteString
				: SauceLexemeType.TaggedDoubleQuoteString;
		}

		case '`' -> {
			out.type = tag == null
				? SauceLexemeType.BackQuoteString
				: SauceLexemeType.TaggedBackQuoteString;
		}

		default -> {
			throw new IllegalArgumentException (
				"Invalid open delimiter: ["
					+ LogStringSanitizer.asSanitized (openDelimiterCodePoint)
					+ "]."
			);
		}
		}

		out.setContent (
			lexemeRawContentBuilder.getString (),
			lexemeValueContentBuilder.getString (),
			tag
		);
	}

	private void readRestOfRawString (
		SauceLexeme out)
		throws SauceParseError {

		// read raw string open delimiter

		inputCursor.getCurrentPosition (delimiterTagBlockStartPosition);
		irrelevantInputHandler.skipIrrelevantInput (inputCursor);
		inputCursor.getCurrentPosition (delimiterTagStartPosition);
		int codePoint = inputCursor.getCurrentCodePoint ();

		int delimiterTagLength = 0;

		ScanOpenDelimiterTag:
		while (true) {
			switch (codePoint) {
			case StringPosition.STRING_END -> {
				throw errorFactory.createExceptionObject (
					SauceParseErrorType.InRawString_MalformedOpenDelimiter,
					delimiterTagBlockStartPosition.line,
					delimiterTagBlockStartPosition.column
				);
			}

			default -> {
				if (irrelevantInputHandler
					.isAtStartOfIrrelevantInput (inputCursor)) {

					break ScanOpenDelimiterTag;
				}

				lexemeRawContentBuilder.appendCodePoint (codePoint);
				delimiterTagLength++;
				inputCursor.moveToNextCodePoint ();
				codePoint = inputCursor.getCurrentCodePoint ();
			}

			case '>' -> {
				break ScanOpenDelimiterTag;
			}
			}
		}

		String delimiterTag =
			inputCursor.getSubstringRelativeToCurrentPosition (
				delimiterTagStartPosition
			);

		irrelevantInputHandler.skipIrrelevantInput (inputCursor);

		codePoint = inputCursor.getCurrentCodePoint ();
		if (codePoint != '>') {
			throw errorFactory.createExceptionObject (
				SauceParseErrorType.InRawString_MalformedOpenDelimiter,
				delimiterTagBlockStartPosition.line,
				delimiterTagBlockStartPosition.column
			);
		}

		lexemeRawContentBuilder.appendCodePoint (codePoint);
		inputCursor.moveToNextCodePoint ();
		codePoint = inputCursor.getCurrentCodePoint ();

		// read raw string content

		inputCursor.getCurrentPosition (rawStringContentStartPosition);

		while (!isAtRawStringCloseDelimiter (
			delimiterTag,
			delimiterTagLength
		)) {
			lexemeRawContentBuilder.appendCodePoint (codePoint);
			inputCursor.moveToNextCodePoint ();
			codePoint = inputCursor.getCurrentCodePoint ();
		}

		String content =
			inputCursor.getSubstringRelativeToCurrentPosition (
				rawStringContentStartPosition
			);

		// read raw string close delimiter

		// read '</'

		lexemeRawContentBuilder.appendCodePoint (codePoint);
		inputCursor.moveToNextCodePoint ();
		codePoint = inputCursor.getCurrentCodePoint ();

		lexemeRawContentBuilder.appendCodePoint (codePoint);
		inputCursor.moveToNextCodePoint ();

		irrelevantInputHandler.skipIrrelevantInput (inputCursor);

		// read delimiter tag
		for (int i = 0; i < delimiterTagLength; i++) {
			codePoint = inputCursor.getCurrentCodePoint ();
			lexemeRawContentBuilder.appendCodePoint (codePoint);
			inputCursor.moveToNextCodePoint ();
		}

		irrelevantInputHandler.skipIrrelevantInput (inputCursor);

		// read '>'

		codePoint = inputCursor.getCurrentCodePoint ();
		lexemeRawContentBuilder.appendCodePoint (codePoint);
		inputCursor.moveToNextCodePoint ();

		// write output

		out.type = SauceLexemeType.RawString;

		out.setContent (
			lexemeRawContentBuilder.getString (),
			content,
			delimiterTag
		);
	}

	private boolean isAtRawStringCloseDelimiter (
		String delimiterTag,
		int delimiterTagLength)
		throws SauceParseError {

		int codePoint = inputCursor.getCurrentCodePoint ();

		if (codePoint == StringPosition.STRING_END) {
			throw errorFactory.createExceptionObject (
				SauceParseErrorType.InRawString_MalformedCloseDelimiter,
				lexemeStartPosition.line,
				lexemeStartPosition.column
			);
		}

		if (codePoint != '<') {
			return false;
		}

		inputCursor.getCurrentPosition (delimiterTagBlockStartPosition);

		inputCursor.moveToNextCodePoint ();
		codePoint = inputCursor.getCurrentCodePoint ();

		if (codePoint == StringPosition.STRING_END) {
			throw errorFactory.createExceptionObject (
				SauceParseErrorType.InRawString_MalformedCloseDelimiter,
				delimiterTagBlockStartPosition.line,
				delimiterTagBlockStartPosition.column
			);
		}

		if (codePoint != '/') {
			inputCursor.moveToPosition (delimiterTagBlockStartPosition);
			return false;
		}

		inputCursor.moveToNextCodePoint ();

		irrelevantInputHandler.skipIrrelevantInput (inputCursor);

		delimiterTagCursor.setInputString (delimiterTag);
		for (int i = 0; i < delimiterTagLength; i++) {
			if (inputCursor.getCurrentCodePoint ()
				!= delimiterTagCursor.getCurrentCodePoint ()) {

				inputCursor.moveToPosition (delimiterTagBlockStartPosition);
				return false;
			}

			inputCursor.moveToNextCodePoint ();
			delimiterTagCursor.moveToNextCodePoint ();
		}

		irrelevantInputHandler.skipIrrelevantInput (inputCursor);

		if (inputCursor.getCurrentCodePoint () != '>') {
			throw errorFactory.createExceptionObject (
				SauceParseErrorType.InRawString_MalformedCloseDelimiter,
				delimiterTagBlockStartPosition.line,
				delimiterTagBlockStartPosition.column
			);
		}

		inputCursor.moveToPosition (delimiterTagBlockStartPosition);
		return true;
	}

}
