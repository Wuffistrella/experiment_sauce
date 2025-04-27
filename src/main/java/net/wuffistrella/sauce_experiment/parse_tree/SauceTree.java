package net.wuffistrella.sauce_experiment.parse_tree;

import java.util.ArrayList;

/**
 *
 */
public class SauceTree
	extends SauceTreeComponent {

	// TODO convert into linked list
	public final ArrayList<SauceStatement> statements =
		new ArrayList<> ();

}
