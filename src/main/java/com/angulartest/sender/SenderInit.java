package com.angulartest.sender;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.angulartest.service.ProvidersService;

@Configuration
public class SenderInit implements ServletContextListener {	
	@Autowired
	private ProvidersService provider;
	
	private Thread thread;
	
	public void contextInitialized(ServletContextEvent sce) {		
//		Sender sender = new Sender(sce.getServletContext(), provider);
//		thread = new Thread(sender);
//		thread.setName("Sender Thread");
//		thread.start();
	}

	public void contextDestroyed(ServletContextEvent sce) {
		// wait for thread to finish sending messages
//		while (thread.getState() != Thread.State.TIMED_WAITING) {}
//		thread.interrupt();
	}
}
