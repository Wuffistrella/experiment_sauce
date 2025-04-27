package net.wuffistrella.sauce_experiment.cursor;

/**
 *
 */
public class SauceTreeProcessException extends Exception {

	final int line;
	final int column;

	SauceTreeProcessException (
		int line,
		int column) {
		this.line = line;
		this.column = column;
	}

	SauceTreeProcessException (
		String message,
		int line,
		int column) {
		super (message);
		this.line = line;
		this.column = column;
	}

	SauceTreeProcessException (
		String message,
		Throwable cause,
		int line,
		int column) {
		super (message, cause);
		this.line = line;
		this.column = column;
	}

	SauceTreeProcessException (
		Throwable cause,
		int line,
		int column) {
		super (cause);
		this.line = line;
		this.column = column;
	}

	SauceTreeProcessException (
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
