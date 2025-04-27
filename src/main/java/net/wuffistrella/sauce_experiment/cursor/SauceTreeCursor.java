package net.wuffistrella.sauce_experiment.cursor;

import net.wuffistrella.sauce_experiment.parse_tree.SauceCheeseElement;
import net.wuffistrella.sauce_experiment.parse_tree.SauceElement;
import net.wuffistrella.sauce_experiment.parse_tree.SauceElementType;
import net.wuffistrella.sauce_experiment.parse_tree.SauceSingularElement;
import net.wuffistrella.sauce_experiment.parse_tree.SauceStatement;
import net.wuffistrella.sauce_experiment.parse_tree.SauceTree;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 *
 */
public class SauceTreeCursor {

	private final SauceTree sauceTree;

	private static class StackItem {

		SauceTree block;
		SauceCheeseElement cheeseBlock;
		ArrayList<SauceStatement> statements;
		SauceStatement currentStatement;
		int nextElementIndex;
		int nextStatementIndex;

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

			stackItem.block = null;
			stackItem.cheeseBlock = null;

			pool.push (stackItem);
		}
	}

	private final StackItemPool stackItemPool =
		new StackItemPool ();

	public SauceTreeCursor (
		SauceTree sauceTree) {

		if (sauceTree == null) {
			throw new IllegalArgumentException (
				"Attempt to create Sauce tree cursor over null."
			);
		}

		currentStackItem = stackItemPool.obtainStackItem ();
		currentStackItem.block = sauceTree;
		currentStackItem.statements = sauceTree.statements;
		currentStackItem.nextElementIndex = 0;
		currentStackItem.nextStatementIndex = 0;

		this.sauceTree = sauceTree;
	}

	public boolean moveToNextStatement () {
		if (currentStackItem.nextStatementIndex
			>= currentStackItem.statements.size ()) {

			return false;
		}

		currentStackItem.currentStatement =
			currentStackItem.statements.get (
				currentStackItem.nextStatementIndex);

		currentStackItem.nextStatementIndex++;
		currentStackItem.nextElementIndex = 0;

		return true;
	}

	public boolean hasNextElement() {
		ArrayList<SauceElement> currentElements =
			currentStackItem.currentStatement.elements;

		return currentStackItem.nextElementIndex
			< currentElements.size ();
	}

	public boolean enterCheeseBlock ()
		throws SauceTreeProcessException {

		ArrayList<SauceElement> currentElements =
			currentStackItem.currentStatement.elements;

		if (currentStackItem.nextElementIndex
			>= currentElements.size ()) {

			throw new SauceTreeProcessException (
				"End of statement element list while looking for cheese.",
				currentStackItem.currentStatement.endLine,
				currentStackItem.currentStatement.endColumn
			);
		}

		SauceElement element =
			currentElements.get (currentStackItem.nextElementIndex);

		boolean isNextElementCheese =
			element.type == SauceElementType.Cheese;

		if (isNextElementCheese) {
			currentStackItem.nextElementIndex++;
			stack.push (currentStackItem);

			currentStackItem = stackItemPool.obtainStackItem ();
			SauceCheeseElement cheeseBlock = (SauceCheeseElement) element;

			currentStackItem.cheeseBlock = cheeseBlock;
			currentStackItem.statements = cheeseBlock.statements;
			currentStackItem.nextElementIndex = 0;
			currentStackItem.nextStatementIndex = 0;
		}

		return isNextElementCheese;
	}

	public void exitCheeseBlock ()
		throws SauceTreeProcessException {

		if (currentStackItem.cheeseBlock == null) {
			throw new SauceTreeProcessException (
				"Attempt to exit cheese block when not inside one.",
				currentStackItem.currentStatement.endLine,
				currentStackItem.currentStatement.endColumn
			);
		}

		stackItemPool.freeStackItem (currentStackItem);
		try {
			currentStackItem = stack.pop ();
		} catch (EmptyStackException e) {
			// TODO report current element position
			throw new SauceTreeProcessException (
				"Attempt to exit cheese block at top level.",
				currentStackItem.currentStatement.line,
				currentStackItem.currentStatement.column
			);
		}
	}

	public boolean consumeElement (
		SauceSingularElementConsumer consumer)
		throws SauceTreeProcessException {

		ArrayList<SauceElement> currentElements =
			currentStackItem.currentStatement.elements;

		if (currentStackItem.nextElementIndex
			>= currentElements.size ()) {

			throw new SauceTreeProcessException (
				"End of statement element list while looking for singular element.",
				currentStackItem.currentStatement.endLine,
				currentStackItem.currentStatement.endColumn
			);
		}

		SauceElement element =
			currentElements.get (currentStackItem.nextElementIndex);

		boolean isNextElementSingular =
			element.type == SauceElementType.Singular;

		if (isNextElementSingular) {
			boolean consumed = consumer.consume ((SauceSingularElement) element);
			if (consumed) {
				currentStackItem.nextElementIndex++;
			}
			return consumed;

		} else {
			return false;
		}
	}

	public boolean enterBodyBlock () {
		SauceTree bodyBlock = currentStackItem.currentStatement.bodyBlock;

		boolean currentStatementHasBody = bodyBlock != null;

		if (currentStatementHasBody) {
			currentStackItem.nextElementIndex++;
			stack.push (currentStackItem);

			currentStackItem = stackItemPool.obtainStackItem ();

			currentStackItem.block = bodyBlock;
			currentStackItem.statements = bodyBlock.statements;
			currentStackItem.nextElementIndex = 0;
			currentStackItem.nextStatementIndex = 0;
		}

		return currentStatementHasBody;
	}

	public void exitBodyBlock ()
		throws SauceTreeProcessException {

		if (currentStackItem.block == null) {
			throw new SauceTreeProcessException (
				"Attempt to exit body block when not inside one.",
				currentStackItem.currentStatement.endLine,
				currentStackItem.currentStatement.endColumn
			);
		}

		stackItemPool.freeStackItem (currentStackItem);
		try {
			currentStackItem = stack.pop ();
		} catch (EmptyStackException e) {
			// TODO report current element position
			throw new SauceTreeProcessException (
				"Attempt to exit body block at top level.",
				currentStackItem.currentStatement.line,
				currentStackItem.currentStatement.column
			);
		}
	}

}
