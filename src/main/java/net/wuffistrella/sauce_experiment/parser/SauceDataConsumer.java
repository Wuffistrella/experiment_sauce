package net.wuffistrella.sauce_experiment.parser;

import net.wuffistrella.sauce_experiment.exceptions.SauceOutputException;

/**
 *
 */
public interface SauceDataConsumer {

	void singularElement (
		String value,
		String tag,
		int line,
		int column)
		throws SauceOutputException;

	void cheeseElementStart (
		int line,
		int column)
		throws SauceOutputException;

	void cheeseElementEnd (
		int line,
		int column)
		throws SauceOutputException;

	void statementEnd (
		int line,
		int column)
		throws SauceOutputException;

	void bodyBlockStart (
		int line,
		int column)
		throws SauceOutputException;

	void bodyBlockEnd (
		int line,
		int column)
		throws SauceOutputException;

	void end (
		int line,
		int column)
		throws SauceOutputException;

}
