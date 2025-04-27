package net.wuffistrella.sauce_experiment.test;

import net.wuffistrella.sauce_experiment.cursor.SauceSingularElementConsumer;
import net.wuffistrella.sauce_experiment.cursor.SauceTreeCursor;
import net.wuffistrella.sauce_experiment.cursor.SauceTreeProcessException;
import net.wuffistrella.sauce_experiment.exceptions.SauceOutputException;
import net.wuffistrella.sauce_experiment.exceptions.SauceParseError;
import net.wuffistrella.sauce_experiment.exceptions.SauceParseErrorFactory;
import net.wuffistrella.sauce_experiment.lexer.SauceLexer;
import net.wuffistrella.sauce_experiment.parse_tree.SauceSingularElement;
import net.wuffistrella.sauce_experiment.parse_tree.SauceTree;
import net.wuffistrella.sauce_experiment.parse_tree.SimpleSauceTreeComponentFactory;
import net.wuffistrella.sauce_experiment.parser.SauceParser;
import net.wuffistrella.sauce_experiment.parser.SauceTreeBuilder;
import net.wuffistrella.sauce_experiment.strings.PositionTrackingStringCursor;
import net.wuffistrella.sauce_experiment.strings.PositionTrackingStringCursor.Configuration;
import net.wuffistrella.sauce_experiment.strings.WhitespaceAndCCommentsHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 */
public class Main {
	public static void main (String[] args) {
		runTest_1 ();
	}

	private static void runTest_1 () {
		File inputFile = new File ("data/test-1.s.txt");

		String inputString = null;

		try {
			inputString = new Scanner (inputFile).useDelimiter ("\\Z").next ();

		} catch (FileNotFoundException e) {
			throw new RuntimeException (
				"Input file not found.",
				e
			);
		}

		Configuration configuration =
			new Configuration (2);

		PositionTrackingStringCursor inputCursor =
			new PositionTrackingStringCursor (configuration);

		inputCursor.setInputString (inputString);

		SauceParseErrorFactory parseErrorFactory =
			new SauceParseErrorFactory ();

		SauceLexer lexer =
			new SauceLexer (
				inputCursor,
				new WhitespaceAndCCommentsHandler (),
				parseErrorFactory
			);

		SauceParser parser =
			new SauceParser (parseErrorFactory);

		SauceTreeBuilder sauceTreeBuilder =
			new SauceTreeBuilder (
				new SimpleSauceTreeComponentFactory ()
			);

		try {
			parser.processSauce (
				lexer,
				sauceTreeBuilder
			);
		} catch (SauceParseError | SauceOutputException e) {
			throw new RuntimeException ("Failed to parse Sauce.", e);
		}

		SauceTree sauceTree = sauceTreeBuilder.getSauceTree ();

		SauceTreeCursor cursor = new SauceTreeCursor (sauceTree);

		final ElementConsumerOutput consumerOutput =
			new ElementConsumerOutput ();

		SauceSingularElementConsumer stringConsumer =
			new SauceSingularElementConsumer () {

				@Override
				public boolean consume (
					SauceSingularElement element) throws SauceTreeProcessException {

					consumerOutput.stringValue = element.value;
					return true;
				}

			};

		SauceSingularElementConsumer numberConsumer =
			new SauceSingularElementConsumer () {

				@Override
				public boolean consume (
					SauceSingularElement element) throws SauceTreeProcessException {

					try {
						consumerOutput.numberValue =
							Float.parseFloat (element.value);
						return true;
					} catch (NumberFormatException e) {
						consumerOutput.numberValue = Float.NaN;
//						return false;
						// skip
						return true;
					}
				}

			};

		Data data = new Data ();

		try {
			while (cursor.moveToNextStatement ()) {
				if (cursor.hasNextElement ()) {
					cursor.consumeElement (stringConsumer);
					switch (consumerOutput.stringValue) {
					case "point" -> {
						Point point = readPoint (
							cursor, numberConsumer, consumerOutput);

						data.points.add (point);
					}

					case "quad" -> {
						Quad quad = new Quad ();

						cursor.consumeElement (stringConsumer);
						quad.name = consumerOutput.stringValue;

						cursor.enterBodyBlock ();
						while (cursor.moveToNextStatement ()) {
							cursor.consumeElement (stringConsumer);
							switch (consumerOutput.stringValue) {
							case "p1" -> {
								quad.p1 = readPoint (
									cursor, numberConsumer, consumerOutput);
							}

							case "p2" -> {
								quad.p2 = readPoint (
									cursor, numberConsumer, consumerOutput);
							}
							}
						}
						cursor.exitBodyBlock ();

						data.quads.add (quad);
					}

					case "triangle" -> {
						Triangle triangle = new Triangle ();

						cursor.enterCheeseBlock ();
						cursor.moveToNextStatement ();
						triangle.a = readPoint (
							cursor, numberConsumer, consumerOutput);
						cursor.exitCheeseBlock ();

						cursor.enterCheeseBlock ();
						cursor.moveToNextStatement ();
						triangle.b = readPoint (
							cursor, numberConsumer, consumerOutput);
						cursor.exitCheeseBlock ();

						cursor.enterCheeseBlock ();
						cursor.moveToNextStatement ();
						triangle.c = readPoint (
							cursor, numberConsumer, consumerOutput);
						cursor.exitCheeseBlock ();

						triangle.color = new Color ();

						cursor.enterCheeseBlock ();
						while (cursor.moveToNextStatement ()) {
							cursor.consumeElement (stringConsumer);
							switch (consumerOutput.stringValue) {
							case "rgb" -> {
								cursor.consumeElement (numberConsumer);
								triangle.color.r = consumerOutput.numberValue;

								cursor.consumeElement (numberConsumer);
								triangle.color.g = consumerOutput.numberValue;

								cursor.consumeElement (numberConsumer);
								triangle.color.b = consumerOutput.numberValue;
							}

							case "trans" -> {
								cursor.consumeElement (stringConsumer);
								String trans = consumerOutput.stringValue;
								if (trans.endsWith ("%")) {
									String transStringValue =
										trans.substring (0, trans.length () - 1);

									triangle.color.a =
										Float.parseFloat (transStringValue) / 100;

								} else {
									triangle.color.a =
										Float.parseFloat (trans);
								}
							}
							}
						}
						cursor.exitCheeseBlock ();

						data.triangles.add (triangle);
					}
					}

				} else {
					cursor.enterBodyBlock ();
					while (cursor.moveToNextStatement ()) {
						if (!cursor.hasNextElement ()) {
							continue;
						}

						cursor.consumeElement (stringConsumer);
						switch (consumerOutput.stringValue) {
						case "item" -> {
							Item item = new Item ();

							cursor.consumeElement (stringConsumer);
							item.name = consumerOutput.stringValue;

							data.items.add (item);
						}
						}
					}
					cursor.exitBodyBlock ();
				}
			}

		} catch (SauceTreeProcessException e) {
			throw new RuntimeException ("Invalid Sauce.", e);
		}

		System.out.println ("=== Data ===");

		System.out.println ("=== Points ===");
		for (Point point : data.points) {
			System.out.println (point);
		}

		System.out.println ("=== Quads ===");
		for (Quad quad : data.quads) {
			System.out.println (quad);
		}

		System.out.println ("=== Triangles ===");
		for (Triangle triangle : data.triangles) {
			System.out.println (triangle);
		}

		System.out.println ("=== Items ===");
		for (Item item : data.items) {
			System.out.println (item);
		}

		System.out.println ("=== The end ===");
	}

	private static Point readPoint (
		SauceTreeCursor cursor,
		SauceSingularElementConsumer numberConsumer,
		ElementConsumerOutput consumerOutput)
		throws SauceTreeProcessException {

		Point point = new Point ();

		cursor.consumeElement (numberConsumer);
		point.x = consumerOutput.numberValue;

		cursor.consumeElement (numberConsumer);
		point.y = consumerOutput.numberValue;

		return point;
	}

	private static class ElementConsumerOutput {
		String stringValue;
		float numberValue;
	}

	private static class Data {
		final ArrayList<Point> points = new ArrayList<> ();
		final ArrayList<Quad> quads = new ArrayList<> ();
		final ArrayList<Triangle> triangles = new ArrayList<> ();
		final ArrayList<Item> items = new ArrayList<> ();
	}

	private static class Point {
		float x;
		float y;

		@Override
		public String toString () {
			return "Point{" +
				"x=" + x +
				", y=" + y +
				'}';
		}
	}

	private static class Quad {
		String name;
		Point p1;
		Point p2;

		@Override
		public String toString () {
			return "Quad{" +
				"name='" + name + '\'' +
				", p1=" + p1 +
				", p2=" + p2 +
				'}';
		}
	}

	private static class Triangle {
		Point a;
		Point b;
		Point c;
		Color color;

		@Override
		public String toString () {
			return "Triangle{" +
				"a=" + a +
				", b=" + b +
				", c=" + c +
				", color=" + color +
				'}';
		}
	}

	private static class Color {
		float r;
		float g;
		float b;
		float a;

		@Override
		public String toString () {
			return "Color{" +
				"r=" + r +
				", g=" + g +
				", b=" + b +
				", a=" + a +
				'}';
		}
	}

	private static class Item {
		String name;

		@Override
		public String toString () {
			return "Item{" +
				"name='" + name + '\'' +
				'}';
		}
	}
}
