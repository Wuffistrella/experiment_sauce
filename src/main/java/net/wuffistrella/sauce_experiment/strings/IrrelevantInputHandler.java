package net.wuffistrella.sauce_experiment.strings;

/**
 *
 */
public interface IrrelevantInputHandler {

	void skipIrrelevantInput (
		StringCursor inputCursor);

	boolean isAtStartOfIrrelevantInput (
		StringCursor inputCursor);

}
