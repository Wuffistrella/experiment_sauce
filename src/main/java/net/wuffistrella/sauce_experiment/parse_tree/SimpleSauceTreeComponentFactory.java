package net.wuffistrella.sauce_experiment.parse_tree;

/**
 * TODO later I would like to create an object pool
 */
public class SimpleSauceTreeComponentFactory
	implements SauceTreeComponentFactory {

	@Override
	public SauceTree newSauceTree () {
		SauceTree sauceTree = new SauceTree ();
		sauceTree.setPosition (0, 0);

		return sauceTree;
	}

	@Override
	public SauceStatement newStatement (
		SauceTree sauceTree,
		int line,
		int column) {

		SauceStatement statement = new SauceStatement ();
		statement.setPosition (line, column);

		sauceTree.statements.add (statement);

		return statement;
	}

	@Override
	public SauceStatement newCheeseStatement (
		SauceCheeseElement cheeseElement,
		int line,
		int column) {

		SauceStatement statement = new SauceStatement ();
		statement.setPosition (line, column);

		cheeseElement.statements.add (statement);

		return statement;
	}

	@Override
	public SauceSingularElement newSingularElement (
		SauceStatement statement,
		String value,
		String tag,
		int line,
		int column) {

		SauceSingularElement element = new SauceSingularElement ();
		element.value = value;
		element.tag = tag;
		element.setPosition (line, column);

		statement.elements.add (element);

		return element;
	}

	@Override
	public SauceCheeseElement newCheeseElement (
		SauceStatement statement,
		int line,
		int column) {

		SauceCheeseElement element = new SauceCheeseElement ();
		element.setPosition (line, column);

		statement.elements.add (element);

		return element;
	}

	@Override
	public SauceTree newBodyBlock (
		SauceStatement statement,
		int line,
		int column) {

		SauceTree block = new SauceTree ();
		block.setPosition (line, column);

		statement.bodyBlock = block;

		return block;
	}

}
