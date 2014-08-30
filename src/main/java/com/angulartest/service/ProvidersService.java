package com.angulartest.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class ProvidersService {
	private static ProvidersService instance;
	
	private Map<String, String> providerEmails;
	
	public static ProvidersService getInstance() {
		if (instance == null) {
			instance = new ProvidersService();
		}
		return instance;
	}
	
	private ProvidersService() {
		providerEmails = new HashMap<String, String>();
		providerEmails.put("Verizon", "@vtext.com");
		providerEmails.put("AT&T", "@txt.att.net");
		providerEmails.put("Sprint", "@messaging.sprintpcs.com");
		providerEmails.put("T-Mobile", "@tmomail.net");
		providerEmails.put("test", "@gmail.com");
	}
	
	public String[] getProviders() {
		String[] providers = providerEmails.keySet().toArray(new String[providerEmails.size()]);		
		return providers;
	}
	
	public String getEmailForProvider(String provider) {
		return providerEmails.get(provider);
	}
}
