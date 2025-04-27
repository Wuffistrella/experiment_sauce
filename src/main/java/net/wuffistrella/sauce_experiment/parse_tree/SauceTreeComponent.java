package net.wuffistrella.sauce_experiment.parse_tree;

/**
 *
 */
public abstract class SauceTreeComponent {

	public int line;
	public int column;

	public void setPosition (
		int line,
		int column) {

		this.line = line;
		this.column = column;
	}

}
