package com.angulartest.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.angulartest.model.Reminder;
import com.angulartest.utility.MyConstants;
import com.angulartest.utility.Utils;

@Repository
public class ReminderDAO {

    private JdbcTemplate jdbcTemplate;		
	private final int SEND_LIMIT = 10;
	
	@Autowired
	public ReminderDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public List<Reminder> getRemindersToSend(String monthTable, String scheduledDate) {
		String getMessagesSql = "SELECT * FROM " + MyConstants.SCHEMA_NAME + "." + monthTable + " WHERE ScheduledDate <= ? AND \"wasSent\" = false LIMIT " + SEND_LIMIT;

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
				reminder.setFromUser(rs.getString("username"));
				reminder.setMessage(rs.getString("message"));				
				reminder.setProvider(rs.getString("provider"));
				reminder.setCellNumber(rs.getString("contacts"));
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
		String monthTable = Utils.getMonthTableName(month);
		
		String sql = "UPDATE " +  MyConstants.SCHEMA_NAME + "." + monthTable + " SET \"wasSent\"=? WHERE " + " reminderid=?";
		
		int id = r.getId();
		
		jdbcTemplate.update(sql, new Object[] {
			true,
			id
		});
	}
	
	public List<Reminder> getRemindersToView(String monthTable, int year, String username, String offset, String limit, boolean getUnsentOnly) {
		String getMessagesSql = "SELECT * FROM " +  MyConstants.SCHEMA_NAME + "." + monthTable
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

		time = Utils.convertToTimezone(date + " " + time, MyConstants.SERVER_TIMEZONE, timezone);

		int timeDelim = time.indexOf(" ");
		time = time.substring(timeDelim);
		reminder.setTime(time);

		return reminder;
	}
	
	public boolean doesReminderExist(Reminder reminder) {
		String message = reminder.getMessage();
		
		String date = getReminderDate(reminder);
		String timezone = reminder.getTimezone();
		date = convertTZtoServerTZ(date, timezone);
		Date insertDate = formatReminderDateToDBdate(date);

		String contact = reminder.getCellNumber();

		int month = Integer.parseInt(reminder.getMonth());
		String monthTable = Utils.getMonthTableName(month);
		
		String sql = "SELECT COUNT(contacts) FROM " + MyConstants.SCHEMA_NAME + "." + monthTable + " WHERE contacts = ? AND message = ? AND scheduleddate = ?";
		
		List<Integer> numReminders = jdbcTemplate.query(sql, new Object[] {contact, message, insertDate}, new RowMapper<Integer>() {
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt(1);
			}
		});
		
		return (numReminders.get(0) > 0) ? true : false;
	}
	
	public void addReminder(Reminder reminder, String username) {
		String date = getReminderDate(reminder);

		String name = reminder.getName();
		String message = reminder.getMessage();
		String timezone = reminder.getTimezone();

		date = convertTZtoServerTZ(date, timezone);

		Date insertDate = formatReminderDateToDBdate(date);

		String contact = reminder.getCellNumber();
		String provider = reminder.getProvider();

		int month = Integer.parseInt(reminder.getMonth());
		String monthTable = Utils.getMonthTableName(month);

		String sql = "INSERT INTO " +  MyConstants.SCHEMA_NAME + "." + monthTable
				+ " (USERNAME, SCHEDULEDDATE, NAME, MESSAGE, CONTACTS, PROVIDER, TIMEZONE, \"wasSent\") VALUES (?,?,?,?,?,?,?,?)";

		jdbcTemplate.update(sql, new Object[] { username, insertDate, name, message, contact, provider, timezone, false });
	}
	
	private String getReminderDate(Reminder reminder) {
		String date = reminder.getYear() + "-" + reminder.getMonth() + "-" + reminder.getDay();

		if (date != null) {
			String formattedTime = formatTimePostgreSQL24hour(reminder.getTime());
			date = date + " " + formattedTime;
		}
		
		return date;
	}
	
	private String convertTZtoServerTZ(String date, String timezone) {
		if (!timezone.equals(MyConstants.SERVER_TIMEZONE)) {
			date = Utils.convertToTimezone(date, timezone, MyConstants.SERVER_TIMEZONE);
		}
		
		return date;
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
	
	private Date formatReminderDateToDBdate(String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		Date insertDate = null;
		try {
			insertDate = df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return insertDate;
	}
	
	private String addLeadingZeroToTime(String time) {
		if (time.length() < 10) time = "0" + time;
		
		return time;
	}

	/*
	 * limit number of reminders for a day
	 */
	public boolean isSendLimitReached(String username) {		
		Calendar cal = Calendar.getInstance();
		
	    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    Date insertDate = null;
	    try {
	      insertDate = df.parse(cal.getTime().toString());
	    } catch (ParseException e) {
	      e.printStackTrace();
	    }
		
	    int month = cal.get(Calendar.MONTH) + 1;
		
		String monthTable = Utils.getMonthTableName(month);
		String sql = "SELECT COUNT(reminderid) FROM " + MyConstants.SCHEMA_NAME + "." + monthTable + " WHERE username = ? and scheduleddate::date = ?";
	
		List<Integer> numReminders = jdbcTemplate.query(sql, new Object[] {username, insertDate}, new RowMapper<Integer>() {
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getInt(1);
			}
		});
		
		return (numReminders.size() > SEND_LIMIT) ? true : false;
	}
}
