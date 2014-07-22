package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import sender.SenderInit;
import controller.Home;
import dao.ReminderDAO;

@Configuration
@ComponentScan(basePackageClasses={Home.class, ReminderDAO.class})
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(new Class[] {Application.class, SenderInit.class}, args);
    }
}
