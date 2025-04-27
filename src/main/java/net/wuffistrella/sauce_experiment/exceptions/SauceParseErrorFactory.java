package net.wuffistrella.sauce_experiment.exceptions;

import net.wuffistrella.sauce_experiment.strings.EasyStringBuilder;
import net.wuffistrella.sauce_experiment.strings.StringPosition;

/**
 *
 */
public class SauceParseErrorFactory {

	private final EasyStringBuilder messageBuilder =
		new EasyStringBuilder ();

	public SauceParseError createExceptionObject (
		SauceParseErrorType errorType,
		int line,
		int column) {

		return new SauceParseError (
			createErrorMessage (errorType, line, column),
			line,
			column
		);
	}

	public SauceParseError createExceptionObject (
		SauceParseErrorType errorType,
		Throwable cause,
		int line,
		int column) {

		return new SauceParseError (
			createErrorMessage (errorType, line, column),
			cause,
			line,
			column
		);
	}

	public SauceParseError createExceptionObject (
		SauceParseErrorType errorType,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace,
		int line,
		int column) {

		return new SauceParseError (
			createErrorMessage (errorType, line, column),
			cause,
			enableSuppression,
			writableStackTrace,
			line,
			column
		);

	}

	private String createErrorMessage (
		SauceParseErrorType type,
		int line,
		int column) {

		String errorDescription;
		switch (type) {
		case UnknownError, default -> {
			errorDescription =
				"Unknown parse error.";
		}
		case InString_InvalidEscapeCode -> {
			errorDescription =
				"String contains invalid escape code.";
		}
		case InRawString_MalformedOpenDelimiter -> {
			errorDescription =
				"Raw string has malformed open delimiter.";
		}
		case InRawString_MalformedCloseDelimiter -> {
			errorDescription =
				"Raw string has malformed close delimiter.";
		}
		case AtCodeEnd_NoStringCloseQuote -> {
			errorDescription =
				"Missing string quote.";
		}
		case AtCodeEnd_NoCheeseCloseDelimiter -> {
			errorDescription =
				"Missing cheese close delimiter.";
		}
		case AtCodeEnd_NoBodyBlockCloseDelimiter -> {
			errorDescription =
				"Missing body block close delimiter.";
		}
		case AtTopLevel_UnexpectedBodyBlockCloseDelimiter -> {
			errorDescription =
				"Unexpected body block close delimiter.";
		}
		}

		messageBuilder
			.reset ()
			.append (errorDescription)
		;

		if (line != StringPosition.POSITION_NOT_TRACKED) {
			messageBuilder
				.append (" Line: ")
				.append (line)
				.append (".")
			;
		}

		if (column != StringPosition.POSITION_NOT_TRACKED) {
			messageBuilder
				.append (" Column: ")
				.append (column)
				.append (".")
			;
		}

		return messageBuilder.getString ();
	}

}
