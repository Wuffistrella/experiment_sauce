package net.wuffistrella.sauce_experiment.exceptions;

/**
 *
 */
public enum SauceParseErrorType {

	InWord_Tag,

	InString_InvalidEscapeCode,
	InString_UnexpectedTag,
	InString_NoTag,

	InRawString_MalformedOpenDelimiter,
	InRawString_MalformedCloseDelimiter,
	InRawString_NoTag,

	AtCodeEnd_NoStringCloseQuote,
	AtCodeEnd_NoCheeseCloseDelimiter,
	AtCodeEnd_NoBodyBlockCloseDelimiter,

	AtTopLevel_UnexpectedCheeseCloseDelimiter,
	AtTopLevel_UnexpectedBodyBlockCloseDelimiter,

	InNestedBlock_UnexpectedCheeseCloseDelimiter,
	InNestedBlock_UnexpectedBodyBlockCloseDelimiter,

	UnknownError

}
