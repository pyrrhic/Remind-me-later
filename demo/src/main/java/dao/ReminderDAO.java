package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.Reminder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import utilities.MonthTable;
import utilities.Utils;

@Repository
public class ReminderDAO {

	@Autowired
    private JdbcTemplate jdbcTemplate;	
	
private int sendLimit = 1000;
	
	public int getSendLimit() {
		return sendLimit;
	}
	
	public List<Reminder> getRemindersToSend(String monthTable, String scheduledDate) {
		String getMessagesSql = "SELECT * FROM " + HelperDAO.getSchemaName() + "." + monthTable + " WHERE ScheduledDate <= ? AND \"wasSent\" = false LIMIT " + sendLimit;

	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
	    Date insertDate = null;
	    try {
	      insertDate = df.parse(scheduledDate);
	    } catch (ParseException e) {
	      e.printStackTrace();
	    }
		
		List<Reminder> reminders = jdbcTemplate.query(getMessagesSql, new Object[] { insertDate }, new RowMapper<Reminder>() {
			public Reminder mapRow(ResultSet rs, int rowNum) throws SQLException {
				Reminder reminder = new Reminder();
				reminder.setMessage(rs.getString("Message"));
				
				String contacts = rs.getString("contacts");
				String provider = rs.getString("provider");
				
				reminder.setProvider(provider);
				reminder.setCellNumber(contacts);
				reminder.setId(rs.getInt("reminderid"));
				reminder.setDate(rs.getString("scheduleddate"));
				
				return reminder;
			}
		});
		return reminders;
	}

	public void setRemindersAsSent(Reminder r) {
		String date = r.getDate();
		String monthString = date.substring(5, 7);
		int month = Integer.parseInt(monthString);
		String monthTable = MonthTable.getMonthTableName(month);
		
		String sql = "UPDATE " + HelperDAO.getSchemaName() + "." + monthTable + " SET \"wasSent\"=? WHERE " + " reminderid=?";
		
		int id = r.getId();
		
		jdbcTemplate.update(sql, new Object[] {
			true,
			id
		});
	}
	
	public List<Reminder> getRemindersToView(String monthTable, int year, String username, String offset, String limit, boolean getUnsentOnly) {
		String getMessagesSql = "SELECT * FROM " + HelperDAO.getSchemaName() + "." + monthTable
				+ " WHERE username = ? AND \"wasSent\" = ? AND date_part('year', scheduleddate) = ? ORDER BY scheduleddate LIMIT " + limit + " OFFSET " + offset;
		
		List<Reminder> reminders = jdbcTemplate.query(getMessagesSql, new Object[] { username, false, year }, new RowMapper<Reminder>() {
			public Reminder mapRow(ResultSet rs, int rowNum) throws SQLException {
				Reminder reminder = new Reminder();
				reminder.setId(rs.getInt("reminderid"));
				reminder.setName(rs.getString("name"));
				reminder.setCellNumber(rs.getString("contacts"));
				reminder.setTimezone(rs.getString("timeZone"));
				reminder.setProvider(rs.getString("provider"));
				
				String dateTime = rs.getString("scheduledDate");
				String date = "";
				String time = "";
				if (dateTime != null) {
					int delim = dateTime.indexOf(" ");
					date = dateTime.substring(0, delim);
					time = dateTime.substring(delim + 1);
				}

				reminder.setDate(date);
				reminder.setTime(time);

				reminder = formatData(reminder);

				return reminder;
			}
		});
		return reminders;
	}

	private Reminder formatData(Reminder reminder) {
		String time = reminder.getTime();
		String date = reminder.getDate();
		String timezone = reminder.getTimezone();

		time = Utils.convertToTimezone(date + " " + time, "US/Eastern", timezone);

		int timeDelim = time.indexOf(" ");
		time = time.substring(timeDelim);
		reminder.setTime(time);

		return reminder;
	}
	
	public void addReminder(Reminder reminder, String username) {
		String date = reminder.getYear() + "-" + reminder.getMonth() + "-" + reminder.getDay();

		if (date != null) {
			String formattedTime = formatTimePostgreSQL24hour(reminder.getTime());
			date = date + " " + formattedTime;
		}

		String name = reminder.getName();
		String message = reminder.getMessage();
		String timezone = reminder.getTimezone();

		if (!timezone.equals("US/Eastern")) {
			date = Utils.convertToTimezone(date, timezone, "US/Eastern");
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		Date insertDate = null;
		try {
			insertDate = df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String contact = reminder.getCellNumber();
		String provider = reminder.getProvider();

		int month = Integer.parseInt(reminder.getMonth());
		String monthTable = MonthTable.getMonthTableName(month);

		String sql = "INSERT INTO " + HelperDAO.getSchemaName() + "." + monthTable
				+ " (USERNAME, SCHEDULEDDATE, NAME, MESSAGE, CONTACTS, PROVIDER, TIMEZONE, \"wasSent\") VALUES (?,?,?,?,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { username, insertDate, name, message, contact, provider, timezone, false });
	}
	
	private String formatTimePostgreSQL24hour(String time) {
		time = addLeadingZeroToTime(time);
		
		String AMPM = time.substring(time.length() - 2, time.length());

		String hours = time.substring(0, 2);
		if (AMPM.equals("PM")) {
			hours = Integer.toString(Integer.parseInt(hours) + 12);
		} else if (AMPM.equals("AM") && hours.equals("12")) {
			hours = "00";
		}

		String minutes = time.substring(3, 5);
		String seconds = "00";
		time = hours + ":" + minutes + ":" + seconds;

		return time;
	}
	
	private String addLeadingZeroToTime(String time) {
		if (time.length() < 10) time = "0" + time;
		
		return time;
	}

	/*
	 * limit number of reminders for a contact per day
	 */
	public boolean isSendLimitReached(Reminder reminder, String username, int limit) {
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    Date insertDate = null;
	    try {
	      insertDate = df.parse(reminder.getDate());
	    } catch (ParseException e) {
	      e.printStackTrace();
	    }
		
		Calendar cal = Calendar.getInstance();
	    cal.setTime(insertDate);
	    int month = cal.get(Calendar.MONTH) + 1;
		
		String monthTable = MonthTable.getMonthTableName(month);
		String sql = "SELECT COUNT(reminderid) FROM " + monthTable + " WHERE contacts like ? and username = ? and scheduleddate::date = ?";
		
	    String contact = reminder.getCellNumber();
	
		List<Integer> numReminders = jdbcTemplate.query(sql, new Object[] {"%" + contact + "%", username, insertDate}, new RowMapper<Integer>() {
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt(1);
			}
		});
		
		return (numReminders.size() > limit) ? true : false;
	}
}
