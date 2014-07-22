package sender;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SenderInit implements ServletContextListener {	
	private Thread thread;
	
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("kasjdfhkajsdhfkjashdflajkshdlfjkashdfklasjd");
		
		Sender sender = new Sender(sce.getServletContext());
		thread = new Thread(sender);
		thread.setName("Sender Thread");
		thread.start();
	}

	public void contextDestroyed(ServletContextEvent sce) {
		// wait for thread to finish sending messages
		while (thread.getState() != Thread.State.TIMED_WAITING) {}
		thread.interrupt();
	}
}
