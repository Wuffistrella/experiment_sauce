package net.wuffistrella.sauce_experiment.lexer;

/**
 *
 */
public enum SauceLexemeType {

	Word,

	SingleQuoteString,
	DoubleQuoteString,
	BackQuoteString,

	TaggedSingleQuoteString,
	TaggedDoubleQuoteString,
	TaggedBackQuoteString,

	RawString,

	CheeseOpenDelimiter,
	CheeseCloseDelimiter,

	BlockOpenDelimiter,
	BlockCloseDelimiter,

	StatementTerminator,

	CodeEnd,

}
