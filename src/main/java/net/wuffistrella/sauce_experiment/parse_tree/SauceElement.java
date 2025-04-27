package net.wuffistrella.sauce_experiment.parse_tree;

/**
 *
 */
public abstract class SauceElement
	extends SauceTreeComponent {

	public final SauceElementType type;

	/**
	 * Constructor.
	 */
	protected SauceElement (
		SauceElementType type) {

		this.type = type;
	}

}
