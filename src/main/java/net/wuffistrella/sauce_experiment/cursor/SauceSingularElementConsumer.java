package net.wuffistrella.sauce_experiment.cursor;

import net.wuffistrella.sauce_experiment.parse_tree.SauceSingularElement;

/**
 *
 */
public interface SauceSingularElementConsumer {

	boolean consume (
		SauceSingularElement element)
		throws SauceTreeProcessException;

}
