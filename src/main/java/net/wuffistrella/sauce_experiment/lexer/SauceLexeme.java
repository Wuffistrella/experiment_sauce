package net.wuffistrella.sauce_experiment.lexer;

import net.wuffistrella.sauce_experiment.strings.StringPosition;

/**
 *
 */
public class SauceLexeme {

	public SauceLexemeType type;
	public String rawContent;
	public String valueContent;
	public String tag;
	public int line;
	public int column;

	public void setContentToNone () {
		rawContent = valueContent = null;
		tag = null;
	}

	public void setContent (
		String content) {

		rawContent = valueContent = content;
		tag = null;
	}

	public void setContent (
		String rawContent,
		String valueContent) {

		this.rawContent = rawContent;
		this.valueContent = valueContent;
		tag = null;
	}

	public void setContent (
		String rawContent,
		String valueContent,
		String tag) {

		this.rawContent = rawContent;
		this.valueContent = valueContent;
		this.tag = tag;
	}

	public void setPosition (
		int line,
		int column) {

		this.line = line;
		this.column = column;
	}

	public void setPosition (
		StringPosition position) {

		line = position.line;
		column = position.column;
	}

}
