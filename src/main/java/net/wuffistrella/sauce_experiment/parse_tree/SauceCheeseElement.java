package net.wuffistrella.sauce_experiment.parse_tree;

import java.util.ArrayList;

/**
 *
 */
public class SauceCheeseElement
	extends SauceElement {

	public int endLine;
	public int endColumn;

	// TODO convert into linked list
	public final ArrayList<SauceStatement> statements =
		new ArrayList<> ();

	/**
	 * Constructor.
	 */
	protected SauceCheeseElement () {
		super (SauceElementType.Cheese);
	}

}
