package net.wuffistrella.sauce_experiment.parser;

import net.wuffistrella.sauce_experiment.exceptions.SauceOutputException;
import net.wuffistrella.sauce_experiment.exceptions.SauceParseError;
import net.wuffistrella.sauce_experiment.exceptions.SauceParseErrorFactory;
import net.wuffistrella.sauce_experiment.exceptions.SauceParseErrorType;
import net.wuffistrella.sauce_experiment.lexer.SauceLexeme;
import net.wuffistrella.sauce_experiment.lexer.SauceLexemeSource;

import java.util.Stack;

/**
 *
 */
public class SauceParser {

	private final SauceParseErrorFactory errorFactory;
	private final SauceLexeme currentLexeme = new SauceLexeme ();

	private static class StackItem {

		boolean isCheese;
		int startLine;
		int startColumn;

	}

	private final Stack<StackItem> stack = new Stack<> ();

	private static class StackItemPool {
		private final Stack<StackItem> pool = new Stack<> ();

		StackItem obtainStackItem () {
			if (pool.isEmpty ()) {
				return new StackItem ();
			} else {
				return pool.pop ();
			}
		}

		void freeStackItem (
			StackItem stackItem) {

			pool.push (stackItem);
		}
	}

	private final StackItemPool stackItemPool =
		new StackItemPool ();

	public SauceParser (
		SauceParseErrorFactory errorFactory) {

		this.errorFactory = errorFactory;
	}

	public void processSauce (
		SauceLexemeSource lexemeSource,
		SauceDataConsumer out)
		throws SauceParseError, SauceOutputException {

		try {
			boolean needStatementEnd = false;
			boolean hasMoreLexemes;

			ParseSauce:
			do {
				hasMoreLexemes = lexemeSource.nextLexeme (currentLexeme);

				switch (currentLexeme.type) {
				case Word -> {
					if (currentLexeme.tag != null) {
						throw errorFactory.createExceptionObject (
							SauceParseErrorType.InWord_Tag,
							currentLexeme.line,
							currentLexeme.column
						);
					}

					out.singularElement (
						currentLexeme.valueContent,
						null,
						currentLexeme.line,
						currentLexeme.column
					);

					needStatementEnd = true;
				}

				case SingleQuoteString, DoubleQuoteString, BackQuoteString -> {
					if (currentLexeme.tag != null) {
						throw errorFactory.createExceptionObject (
							SauceParseErrorType.InString_UnexpectedTag,
							currentLexeme.line,
							currentLexeme.column
						);
					}

					out.singularElement (
						currentLexeme.valueContent,
						null,
						currentLexeme.line,
						currentLexeme.column
					);

					needStatementEnd = true;
				}

				case TaggedSingleQuoteString,
					TaggedDoubleQuoteString,
					TaggedBackQuoteString -> {

					if (currentLexeme.tag == null) {
						throw errorFactory.createExceptionObject (
							SauceParseErrorType.InString_NoTag,
							currentLexeme.line,
							currentLexeme.column
						);
					}

					out.singularElement (
						currentLexeme.valueContent,
						currentLexeme.tag,
						currentLexeme.line,
						currentLexeme.column
					);

					needStatementEnd = true;
				}

				case RawString -> {
					if (currentLexeme.tag == null) {
						throw errorFactory.createExceptionObject (
							SauceParseErrorType.InRawString_NoTag,
							currentLexeme.line,
							currentLexeme.column
						);
					}

					out.singularElement (
						currentLexeme.valueContent,
						currentLexeme.tag,
						currentLexeme.line,
						currentLexeme.column
					);

					needStatementEnd = true;
				}

				case CheeseOpenDelimiter -> {
					out.cheeseElementStart (
						currentLexeme.line,
						currentLexeme.column
					);

					StackItem stackItem = stackItemPool.obtainStackItem ();
					stackItem.isCheese = true;
					stackItem.startLine = currentLexeme.line;
					stackItem.startColumn = currentLexeme.column;
					stack.push (stackItem);
					needStatementEnd = false;
				}

				case CheeseCloseDelimiter -> {
					if (stack.isEmpty ()) {
						throw errorFactory.createExceptionObject (
							SauceParseErrorType
								.AtTopLevel_UnexpectedCheeseCloseDelimiter,
							currentLexeme.line,
							currentLexeme.column
						);
					}

					StackItem stackItem = stack.pop ();
					if (!stackItem.isCheese) {
						throw errorFactory.createExceptionObject (
							SauceParseErrorType
								.InNestedBlock_UnexpectedCheeseCloseDelimiter,
							currentLexeme.line,
							currentLexeme.column
						);
					}
					stackItemPool.freeStackItem (stackItem);

					if (needStatementEnd) {
						out.statementEnd (
							currentLexeme.line,
							currentLexeme.column
						);
					}

					out.cheeseElementEnd (
						currentLexeme.line,
						currentLexeme.column
					);

					needStatementEnd = true;
				}

				case BodyBlockOpenDelimiter -> {
					out.bodyBlockStart (
						currentLexeme.line,
						currentLexeme.column
					);

					StackItem stackItem = stackItemPool.obtainStackItem ();
					stackItem.isCheese = false;
					stackItem.startLine = currentLexeme.line;
					stackItem.startColumn = currentLexeme.column;
					stack.push (stackItem);

					needStatementEnd = false;
				}

				case BodyBlockCloseDelimiter -> {
					if (stack.isEmpty ()) {
						throw errorFactory.createExceptionObject (
							SauceParseErrorType.
								AtTopLevel_UnexpectedBodyBlockCloseDelimiter,
							currentLexeme.line,
							currentLexeme.column
						);
					}

					StackItem stackItem = stack.pop ();
					if (stackItem.isCheese) {
						throw errorFactory.createExceptionObject (
							SauceParseErrorType
								.InNestedBlock_UnexpectedBodyBlockCloseDelimiter,
							currentLexeme.line,
							currentLexeme.column
						);
					}
					stackItemPool.freeStackItem (stackItem);

					if (needStatementEnd) {
						out.statementEnd (
							currentLexeme.line,
							currentLexeme.column
						);
					}

					out.bodyBlockEnd (
						currentLexeme.line,
						currentLexeme.column
					);

					needStatementEnd = false;
				}

				case StatementTerminator -> {
					out.statementEnd (
						currentLexeme.line,
						currentLexeme.column
					);

					needStatementEnd = false;
				}

				case CodeEnd -> {
					if (needStatementEnd) {
						out.statementEnd (
							currentLexeme.line,
							currentLexeme.column
						);
					}

					out.end (
						currentLexeme.line,
						currentLexeme.column
					);

					needStatementEnd = false;
				}
				}

			} while (hasMoreLexemes);

			if (!stack.isEmpty ()) {
				StackItem currentStackItem = stack.peek ();

				SauceParseErrorType errorType =
					currentStackItem.isCheese
						? SauceParseErrorType.AtCodeEnd_NoCheeseCloseDelimiter
						: SauceParseErrorType.AtCodeEnd_NoBodyBlockCloseDelimiter;

				throw errorFactory.createExceptionObject (
					errorType,
					currentStackItem.startLine,
					currentStackItem.startColumn
				);
			}

		} catch (SauceParseError | SauceOutputException e) {
			throw e;

		} catch (Exception e) {
			throw errorFactory.createExceptionObject (
				SauceParseErrorType.UnknownError,
				e,
				currentLexeme.line,
				currentLexeme.line
			);

		} finally {
			while (!stack.isEmpty ()) {
				stackItemPool.freeStackItem (stack.pop ());
			}
		}
	}

}
