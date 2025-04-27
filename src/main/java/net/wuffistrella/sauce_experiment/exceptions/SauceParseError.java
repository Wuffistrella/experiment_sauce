package net.wuffistrella.sauce_experiment.exceptions;

/**
 *
 */
public class SauceParseError
	extends Exception {

	final int line;
	final int column;

	SauceParseError (
		int line,
		int column) {
		this.line = line;
		this.column = column;
	}

	SauceParseError (
		String message,
		int line,
		int column) {
		super (message);
		this.line = line;
		this.column = column;
	}

	SauceParseError (
		String message,
		Throwable cause,
		int line,
		int column) {
		super (message, cause);
		this.line = line;
		this.column = column;
	}

	SauceParseError (
		Throwable cause,
		int line,
		int column) {
		super (cause);
		this.line = line;
		this.column = column;
	}

	SauceParseError (
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace,
		int line,
		int column) {
		super (message, cause, enableSuppression, writableStackTrace);
		this.line = line;
		this.column = column;
	}

}
