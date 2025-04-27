package net.wuffistrella.sauce_experiment.exceptions;

/**
 *
 */
public class StringCursorNotReusableException
	extends RuntimeException {

	public StringCursorNotReusableException () {
	}

	public StringCursorNotReusableException (String message) {
		super (message);
	}

	public StringCursorNotReusableException (
		String message,
		Throwable cause) {
		super (message, cause);
	}

	public StringCursorNotReusableException (Throwable cause) {
		super (cause);
	}

	public StringCursorNotReusableException (
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace) {
		super (message, cause, enableSuppression, writableStackTrace);
	}

}
