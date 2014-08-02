package com.angulartest.dao;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class Providers {
	private static Providers instance;
	
	private Map<String, String> providerEmails;
	
	public static Providers getInstance() {
		if (instance == null) {
			instance = new Providers();
		}
		return instance;
	}
	
	private Providers() {
		providerEmails = new HashMap<String, String>();
		providerEmails.put("Verizon", "@vtext.com");
		providerEmails.put("AT&T", "@txt.att.net");
		providerEmails.put("Sprint", "@messaging.sprintpcs.com");
		providerEmails.put("T-Mobile", "@tmomail.net");
	}
	
	public String[] getProviders() {
		String[] providers = providerEmails.keySet().toArray(new String[providerEmails.size()]);		
		return providers;
	}
	
	public String getEmailForProvider(String provider) {
		return providerEmails.get(provider);
	}
}
