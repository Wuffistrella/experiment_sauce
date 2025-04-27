package net.wuffistrella.sauce_experiment.parser;

import net.wuffistrella.sauce_experiment.exceptions.SauceOutputException;
import net.wuffistrella.sauce_experiment.parse_tree.SauceCheeseElement;
import net.wuffistrella.sauce_experiment.parse_tree.SauceStatement;
import net.wuffistrella.sauce_experiment.parse_tree.SauceTree;
import net.wuffistrella.sauce_experiment.parse_tree.SauceTreeComponentFactory;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 *
 */
public class SauceTreeBuilder
	implements SauceDataConsumer {

	private final SauceTreeComponentFactory componentFactory;
	private final SauceTree sauceTree;

	private static class StackItem {

		SauceTree sauceTree;
		SauceCheeseElement cheeseElement;
		SauceStatement currentStatement;
		SauceStatement lastStatement;

		void ensureInStatement (
			SauceTreeComponentFactory componentFactory,
			int line,
			int column) {

			if (currentStatement != null) {
				return;
			}

			if (sauceTree != null) {
				currentStatement =
					componentFactory.newStatement (
						sauceTree, line, column);

			} else if (cheeseElement != null) {
				currentStatement =
					componentFactory.newCheeseStatement (
						cheeseElement, line, column);

			} else {
				throw new IllegalStateException (
					"Block is missing Sauce tree component."
				);
			}

			lastStatement = currentStatement;
		}

	}

	private final Stack<StackItem> stack = new Stack<> ();

	private StackItem currentStackItem;

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

			stackItem.sauceTree = null;
			stackItem.cheeseElement = null;
			stackItem.currentStatement = null;

			pool.push (stackItem);
		}
	}

	private final StackItemPool stackItemPool =
		new StackItemPool ();

	public SauceTreeBuilder (
		SauceTreeComponentFactory componentFactory) {

		this.componentFactory = componentFactory;

		sauceTree = componentFactory.newSauceTree ();
		StackItem topLevelStackItem = stackItemPool.obtainStackItem ();
		topLevelStackItem.sauceTree = sauceTree;
		currentStackItem = topLevelStackItem;
	}

	@Override
	public void singularElement (
		String value,
		String tag,
		int line,
		int column)
		throws SauceOutputException {

		if (currentStackItem == null) {
			throw createSauceEndedException ();
		}

		currentStackItem.ensureInStatement (
			componentFactory, line, column);

		componentFactory.newSingularElement (
			currentStackItem.currentStatement,
			value,
			null,
			line,
			column
		);
	}

	@Override
	public void cheeseElementStart (
		int line,
		int column)
		throws SauceOutputException {

		if (currentStackItem == null) {
			throw createSauceEndedException ();
		}

		currentStackItem.ensureInStatement (
			componentFactory, line, column);

		SauceCheeseElement cheeseElement =
			componentFactory.newCheeseElement (
				currentStackItem.currentStatement,
				line,
				column
			);

		stack.push (currentStackItem);
		StackItem cheeseStackItem = stackItemPool.obtainStackItem ();
		cheeseStackItem.cheeseElement = cheeseElement;
		currentStackItem = cheeseStackItem;
	}

	@Override
	public void cheeseElementEnd (
		int line,
		int column)
		throws SauceOutputException {

		if (currentStackItem == null) {
			throw createSauceEndedException ();
		}

		if (currentStackItem.cheeseElement == null) {
			throw new SauceOutputException (
				"End of cheese when not in cheese block.");
		}

		currentStackItem.cheeseElement.endLine = line;
		currentStackItem.cheeseElement.endColumn = column;

		stackItemPool.freeStackItem (currentStackItem);

		try {
			currentStackItem = stack.pop ();
		} catch (EmptyStackException e) {
			throw new SauceOutputException (
				"Attempt to end cheese at top level.");
		}
	}

	@Override
	public void statementEnd (
		int line,
		int column)
		throws SauceOutputException {

		if (currentStackItem == null) {
			throw createSauceEndedException ();
		}

		currentStackItem.ensureInStatement (
			componentFactory,
			line,
			column
		);

		currentStackItem.lastStatement.endLine = line;
		currentStackItem.lastStatement.endColumn = column;

		currentStackItem.currentStatement = null;
	}

	@Override
	public void bodyBlockStart (
		int line,
		int column)
		throws SauceOutputException {

		if (currentStackItem == null) {
			throw createSauceEndedException ();
		}

		currentStackItem.ensureInStatement (
			componentFactory, line, column);

		if (currentStackItem.currentStatement.bodyBlock != null) {
			throw new SauceOutputException (
				"Starting body block when one is already present");
		}

		SauceTree bodyBlock = componentFactory.newBodyBlock (
			currentStackItem.currentStatement,
			line,
			column
		);

		currentStackItem.currentStatement.bodyBlock = bodyBlock;

		stack.push (currentStackItem);
		StackItem bodyBlockStackItem = stackItemPool.obtainStackItem ();
		bodyBlockStackItem.sauceTree = bodyBlock;
		currentStackItem = bodyBlockStackItem;
	}

	@Override
	public void bodyBlockEnd (
		int line,
		int column)
		throws SauceOutputException {

		if (currentStackItem == null) {
			throw createSauceEndedException ();
		}

		if (currentStackItem.sauceTree == null) {
			throw new SauceOutputException (
				"End of body block when not in body block.");
		}

		currentStackItem.lastStatement.endLine = line;
		currentStackItem.lastStatement.endColumn = column;

		stackItemPool.freeStackItem (currentStackItem);

		try {
			currentStackItem = stack.pop ();
		} catch (EmptyStackException e) {
			throw new SauceOutputException (
				"Attempt to end block at top level.");
		}

		currentStackItem.currentStatement = null;
	}

	@Override
	public void end (
		int line,
		int column)
		throws SauceOutputException {

		if (currentStackItem == null) {
			throw createSauceEndedException ();
		}

		if (!stack.isEmpty ()) {
			throw new SauceOutputException (
				"Sauce did not end at top level.");
		}

		currentStackItem = null;
	}

	private static SauceOutputException createSauceEndedException () {
		return new SauceOutputException (
			"Attempt to continue after Sauce ended.");
	}

	public SauceTree getSauceTree () {
		return sauceTree;
	}

}
