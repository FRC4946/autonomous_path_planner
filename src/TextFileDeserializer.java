
import java.util.StringTokenizer;

import ca._4946.mreynolds.pathplanner.src.data.Segment;
import ca._4946.mreynolds.pathplanner.src.data.actions.DriveAction;

/**
 *
 * @author Jared341
 */
public class TextFileDeserializer {

	public static DriveAction deserialize(String serialized) {
		DriveAction path = new DriveAction();

		StringTokenizer tokenizer = new StringTokenizer(serialized, "\n");
		System.out.println("Parsing path string...");
		System.out.println("String has " + serialized.length() + " chars");
		System.out.println("Found " + tokenizer.countTokens() + " tokens");

		String name = tokenizer.nextToken();
		System.out.println(name);

		int num_elements = Integer.parseInt(tokenizer.nextToken());

		for (int i = 0; i < num_elements; ++i) {
			Segment segment = new Segment();
			StringTokenizer line_tokenizer = new StringTokenizer(tokenizer.nextToken(), " ");

			segment.pos = FastParser.parseFormattedDouble(line_tokenizer.nextToken());
			segment.vel = FastParser.parseFormattedDouble(line_tokenizer.nextToken());
			segment.accel = FastParser.parseFormattedDouble(line_tokenizer.nextToken());
			segment.jerk = FastParser.parseFormattedDouble(line_tokenizer.nextToken());
			segment.heading = FastParser.parseFormattedDouble(line_tokenizer.nextToken());
			segment.dt = FastParser.parseFormattedDouble(line_tokenizer.nextToken());
			segment.x = FastParser.parseFormattedDouble(line_tokenizer.nextToken());
			segment.y = FastParser.parseFormattedDouble(line_tokenizer.nextToken());

			path.addSegment(true, segment);
		}
		for (int i = 0; i < num_elements; ++i) {
			Segment segment = new Segment();
			StringTokenizer line_tokenizer = new StringTokenizer(tokenizer.nextToken(), " ");

			segment.pos = FastParser.parseFormattedDouble(line_tokenizer.nextToken());
			segment.vel = FastParser.parseFormattedDouble(line_tokenizer.nextToken());
			segment.accel = FastParser.parseFormattedDouble(line_tokenizer.nextToken());
			segment.jerk = FastParser.parseFormattedDouble(line_tokenizer.nextToken());
			segment.heading = FastParser.parseFormattedDouble(line_tokenizer.nextToken());
			segment.dt = FastParser.parseFormattedDouble(line_tokenizer.nextToken());
			segment.x = FastParser.parseFormattedDouble(line_tokenizer.nextToken());
			segment.y = FastParser.parseFormattedDouble(line_tokenizer.nextToken());

			path.addSegment(false, segment);
		}

		System.out.println("...finished parsing path from string.");
		return path;
	}

}
