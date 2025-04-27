package net.wuffistrella.sauce_experiment.exceptions;

/**
 *
 */
public enum SauceParseErrorType {

	InString_InvalidEscapeCode,

	InRawString_MalformedOpenDelimiter,
	InRawString_MalformedCloseDelimiter,

	AtCodeEnd_NoStringCloseQuote,
	AtCodeEnd_NoCheeseCloseDelimiter,
	AtCodeEnd_NoBodyBlockCloseDelimiter,

	AtTopLevel_UnexpectedBodyBlockCloseDelimiter,

	UnknownError

}
