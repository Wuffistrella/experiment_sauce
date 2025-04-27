package net.wuffistrella.sauce_experiment.parse_tree;

import java.util.ArrayList;

/**
 *
 */
public class SauceStatement
	extends SauceTreeComponent {

	/**
	 * Elements in statement.
	 */
	// TODO convert into linked list
	public final ArrayList<SauceElement> elements =
		new ArrayList<> ();

	/**
	 * Block of statement.
	 * <p>
	 * Is null if statement has no block.
	 */
	public SauceTree block;

}
