package sender;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletContext;

import model.Reminder;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import utilities.MonthTable;
import dao.ReminderDAO;

public class Sender implements Runnable
{		
	private HashMap<String,String> emails;
	private boolean isRunning;
	private ServletContext context;
	
	public Sender(ServletContext context) {
		initEmailMap();
		this.context = context;
	}   
	
	public void run()
	{
		System.out.println("SENDER STARTED");
		//some process to fire off emails that were somehow missed?
		
		// figure out what day and time it is.
		// query database for next upcoming time increment
		// wait until it's time
		// fire off the emails
		// mark emails that were pulled with some sort of flag as 'sent'
		
		// only pull 1000 emails at a time? so i dont run out of memory.
		isRunning = true;
		while(isRunning) {
			System.out.println("Sender awake");
			Calendar cal = new GregorianCalendar();
			TimeZone tz = TimeZone.getTimeZone("America/New_York");
			cal.setTimeZone(tz);
			
			System.out.println(cal.getTime());
			
			String date = getDate(cal);
			String time = getTime(cal);
			time = time.substring(0, 6) + "00";
			
			Mail mail = new Mail(context);			
			//mail.updateNoSendList();
			
			//send
			int month = Integer.parseInt(date.substring(5, 7));
			String monthTable = MonthTable.getMonthTableName(month);
			String dateTime = date + " " + time;
			System.out.println("getting messages for sending - " + dateTime);
			send(monthTable, dateTime, mail);

			//update what time it is before sleeping. not sure how long sending messages will take.
			//sleep until next minute time increment
			sleepUntilNextTimeIncrement(cal);
		}
		
		System.out.println(Thread.currentThread().getName() + " is shutting down.");
	}

	private void send(String monthTable, String dateTime, Mail mail) {
		WebApplicationContext servletContext =  WebApplicationContextUtils.getWebApplicationContext(context);	
		ReminderDAO reminderDao = (ReminderDAO) servletContext.getBean("reminderDAO");
		
		List<Reminder> messages = null;
		do {
			messages = reminderDao.getRemindersToSend(monthTable, dateTime);
			System.out.println("num messages:" + messages.size());

			for(Reminder msgObj: messages) {					
				String provider = msgObj.getProvider();
				String email = getEmailForProvider(provider);
				String sendAddr = msgObj.getCellNumber() + email;
				String msg = msgObj.getMessage();
				
				mail.send(msg, sendAddr);
				reminderDao.setRemindersAsSent(msgObj);
			}	
		} while(messages.size() == reminderDao.getSendLimit());		
	}
	
	private void sleepUntilNextTimeIncrement(Calendar cal) {
		String time = getTime(cal);
		int seconds = Integer.parseInt(time.substring(6));
		
		int timeIncrementInSeconds = 60;
		long wait = 0;
		long milliTime = seconds * 1000;
		if(seconds <= timeIncrementInSeconds){
			wait = timeIncrementInSeconds * 1000 - milliTime;
		}
		
		System.out.println("SLEEPING. currTime = " + time + " sleepTime = " + (wait/1000) + " seconds");
		
		try
			{Thread.sleep(wait);} 
		catch (InterruptedException e) {
			e.printStackTrace();
			isRunning = false;
		}
	}
	
	private String getDate(Calendar cal) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setCalendar(cal);
		return dateFormat.format(cal.getTime());
	}
	
	private String getTime(Calendar cal) {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		timeFormat.setCalendar(cal);
		String time = timeFormat.format(cal.getTime());
		return time;
	}
	
	private void initEmailMap() {		
		emails = new HashMap<String, String>();
		emails.put("Verizon", "@vtext.com");
		emails.put("AT&T", "@txt.att.net");
		emails.put("Sprint", "@messaging.sprintpcs.com");
		emails.put("T-Mobile", "@tmomail.net");
		emails.put("Boost Mobile", "@myboostmobile.com");
	}
	
	private String getEmailForProvider(String provider) {
		return emails.get(provider);
	}
}
