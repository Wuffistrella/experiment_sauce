package net.wuffistrella.sauce_experiment.parse_tree;

/**
 *
 */
public class SauceSingularElement
	extends SauceElement {

	/**
	 * Constructor.
	 */
	public SauceSingularElement () {
		super (SauceElementType.Singular);
	}

	/**
	 * Element content.
	 * <p>
	 * If string, contains parsed, unescaped value.
	 */
	public String value;

	public String tag;

}
