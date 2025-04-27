package net.wuffistrella.sauce_experiment.parse_tree;

/**
 *
 */
public interface SauceTreeComponentFactory {

	SauceTree newSauceTree ();

	SauceStatement newStatement (
		SauceTree sauceTree,
		int line,
		int column);

	SauceStatement newCheeseStatement (
		SauceCheeseElement cheeseElement,
		int line,
		int column);

	SauceSingularElement newSingularElement (
		SauceStatement statement,
		String value,
		String tag,
		int line,
		int column);

	SauceCheeseElement newCheeseElement (
		SauceStatement statement,
		int line,
		int column);

	SauceTree newBodyBlock (
		SauceStatement statement,
		int line,
		int column);

}
