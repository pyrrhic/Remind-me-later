package com.angulartest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.angulartest.dao.RegistrationDAO;
import com.angulartest.model.RegistrationFO;

@Service
public class RegistrationService {
	private RegistrationDAO registrationDAO;
	
	@Autowired
	public RegistrationService(RegistrationDAO registrationDAO) {
		this.registrationDAO = registrationDAO;
	}
	
	public void register(RegistrationFO registrationFO) {
		String email = registrationFO.getEmail();
		if (!registrationDAO.isEmailRegistered(email)) {
			registrationDAO.register(registrationFO);
		}
	}
	
	public boolean isEmailRegistered(String email) {
		return registrationDAO.isEmailRegistered(email);
	}
}
