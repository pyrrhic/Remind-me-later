package utilities;

public class MonthTable
{
	public static String getMonthTableName(int month) {
		
		String monthTable = "";
		switch (month)
		{
			case 1: monthTable = "january_reminders";
					break;
			case 2: monthTable = "february_reminders";	
					break;
			case 3: monthTable = "march_reminders";
					break;
			case 4: monthTable = "april_reminders";
					break;
			case 5: monthTable = "may_reminders";
					break;
			case 6: monthTable = "june_reminders";
					break;
			case 7: monthTable = "july_reminders";
					break;
			case 8: monthTable = "august_reminders";
					break;
			case 9: monthTable = "september_reminders";
					break;
			case 10:monthTable = "october_reminders";
					break;
			case 11:monthTable = "november_reminders";
					break;
			case 12:monthTable = "december_reminders";
					break;
		}
		
		return monthTable;
	}
}
