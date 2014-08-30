package com.angulartest.sender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.angulartest.dao.NoSendListDAO;

public class Mail {	
	private static String EMAIL = System.getenv("EMAIL");
	private static String EMAIL_PASSWORD = System.getenv("EMAIL_PASSWORD");
	
	public List<String> updateNoSendList() {
		List<String> addToNoSendList = null;
		Store store = null;
		try {
			store = connect();
			
			Folder inbox = getInbox(store);
			
			FetchProfile fetchProfile = createFetchProfile();
			
			Message messages[] = getMessages(inbox, fetchProfile);
			
			addToNoSendList = new ArrayList<String>(messages.length);

			for(Message message:messages) {
				String fromAddr = message.getFrom()[0].toString();	
				int delim = fromAddr.indexOf("@");
				String contact = fromAddr.substring(0, delim-1);
				String content = null;
				try {
					content = getText(message);
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
				
				content = content.toLowerCase();
				if(content.contains("stop")) {					
					addToNoSendList.add(contact);			
				}
				
				message.setFlag(Flags.Flag.DELETED, true);
			}
			
			inbox.expunge();
		} 
		catch (NoSuchProviderException e) {
			e.printStackTrace();
		} 
		catch (MessagingException e) {
			e.printStackTrace();
		}
		finally {
			if (store != null) {
				disconnect(store);
			}
		}
		
		return (addToNoSendList == null) ? new ArrayList<String>(0) : addToNoSendList;
	}
	
	private Store connect() {
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		
		Session session = Session.getDefaultInstance(props);
		Store store = null;
		try {
			store = session.getStore("imaps");
			store.connect("imap.gmail.com", EMAIL, EMAIL_PASSWORD);
			//System.out.println(store);
		} 
		catch (MessagingException e) {
			e.printStackTrace();
		}
		
		return store;
	}
	
	private Folder getInbox(Store store) {
		Folder inbox = null;
		try {
			inbox = store.getFolder("Inbox");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		return inbox;
	}
	
	private Message[] getMessages(Folder inbox, FetchProfile fetchProfile) {
		Message messages[] = {};
		
		try {
			inbox.open(Folder.READ_WRITE);
			messages = inbox.getMessages();
			inbox.fetch(messages, fetchProfile);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		return messages;
	}
	
	private FetchProfile createFetchProfile() {
		FetchProfile fetchProfile = new FetchProfile();
		fetchProfile.add(FetchProfile.Item.CONTENT_INFO);
		fetchProfile.add(FetchProfile.Item.FLAGS);
		fetchProfile.add(FetchProfile.Item.ENVELOPE);
		
		return fetchProfile;
	}
	
	private void disconnect(Store store) {
		try {
			store.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public void send(String txtMsg, String sendAddr) {
		int delim = sendAddr.indexOf("@");
		String cellNumber = sendAddr.substring(0, delim);		
		
	    final String SMTP_HOST_NAME = "smtp.gmail.com";
	    final int SMTP_HOST_PORT = 465;
	    
	    Properties props = new Properties();

        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", SMTP_HOST_NAME);
        props.put("mail.smtps.auth", "true");
        
        Session mailSession = Session.getInstance(props);
        mailSession.setDebug(true);
        Transport transport = null;
		try {
			transport = mailSession.getTransport();
		} 
		catch (NoSuchProviderException e) {
			e.printStackTrace();
		}

        MimeMessage message = new MimeMessage(mailSession);
        try {
			message.setSubject("");
	        message.setContent(txtMsg, "text/plain");

	        message.addRecipient(Message.RecipientType.TO, new InternetAddress(sendAddr));
	        
	        transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, EMAIL, EMAIL_PASSWORD);

	        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
	        transport.close();
		} 
        catch (Exception e) {
        	System.out.println(e.getClass().getName());
			e.printStackTrace();
		}
	}
	
	private String getText(Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            return s;
        }
		
		if (p.isMimeType("multipart/ALTERNATIVE")) {
			// prefer html text over plain text
			Multipart mp = (Multipart) p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
				Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					if (text == null)
						text = getText(bp);
					continue;
				} else if (bp.isMimeType("text/html")) {
					String s = getText(bp);
					if (s != null)
						return s;
				} else {
					return getText(bp);
				}
			}
			return text;
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = getText(mp.getBodyPart(i));
				if (s != null)
					return s;
			}
		}

		return null;
	}

}
