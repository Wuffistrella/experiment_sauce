package net.wuffistrella.sauce_experiment.parse_tree;

import java.util.ArrayList;

/**
 *
 */
public class SauceStatement
	extends SauceTreeComponent {

	public int endLine;
	public int endColumn;

	/**
	 * Elements in statement.
	 */
	// TODO convert into linked list
	public final ArrayList<SauceElement> elements =
		new ArrayList<> ();

	public SauceTree bodyBlock;

}
