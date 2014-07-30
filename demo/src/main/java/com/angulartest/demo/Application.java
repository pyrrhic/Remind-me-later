package com.angulartest.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.angulartest.controller.Home;
import com.angulartest.dao.ReminderDAO;
import com.angulartest.sender.SenderInit;

@Configuration
@ComponentScan("com.angulartest")
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(new Class[] {Application.class, SenderInit.class}, args);
    }
}
