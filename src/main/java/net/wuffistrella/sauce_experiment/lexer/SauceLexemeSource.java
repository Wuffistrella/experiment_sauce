package net.wuffistrella.sauce_experiment.lexer;

import net.wuffistrella.sauce_experiment.exceptions.SauceParseError;

/**
 *
 */
public interface SauceLexemeSource {

	void nextLexeme (
		SauceLexeme out)
		throws SauceParseError;

}
