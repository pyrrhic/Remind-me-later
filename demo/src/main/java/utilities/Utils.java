package utilities;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Utils {
	public static String convertToTimezone(String fromTime, String fromTimezone, String toTimezone) {
		DateTimeZone fromTimeZone = DateTimeZone.forID(fromTimezone);
		DateTimeZone toTimeZone = DateTimeZone.forID(toTimezone);
		int space = fromTime.indexOf(" ");
		String formattedFromTime = fromTime.substring(0, space) + "T" + fromTime.substring(space + 1);
		//formattedFromTime = formattedFromTime.replace("/", "-");
		DateTime dateTime = new DateTime(formattedFromTime, fromTimeZone);

		DateTimeFormatter outputFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(toTimeZone);

		return outputFormatter.print(dateTime);
	}
}
