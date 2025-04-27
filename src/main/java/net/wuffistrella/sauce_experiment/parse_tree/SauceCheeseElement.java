package net.wuffistrella.sauce_experiment.parse_tree;

import java.util.ArrayList;

/**
 *
 */
public class SauceCheeseElement
	extends SauceElement {

	// TODO convert into linked list
	private final ArrayList<SauceStatement> statements =
		new ArrayList<> ();

	/**
	 * Constructor.
	 */
	protected SauceCheeseElement () {
		super (SauceElementType.Cheese);
	}

}
