package net.wuffistrella.sauce_experiment.exceptions;

/**
 *
 */
public class SauceOutputException extends Exception {

	SauceOutputException () {
	}

	public SauceOutputException (String message) {
		super (message);
	}

	public SauceOutputException (
		String message,
		Throwable cause) {
		super (message, cause);
	}

	public SauceOutputException (Throwable cause) {
		super (cause);
	}

	public SauceOutputException (
		String message,
		Throwable cause,
		boolean enableSuppression,
		boolean writableStackTrace) {
		super (message, cause, enableSuppression, writableStackTrace);
	}

}
