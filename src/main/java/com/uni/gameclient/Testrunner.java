package com.uni.gameclient;

import com.uni.gameclient.rest.service.Gameserverservice;
import org.springframework.stereotype.Component;

@Component
public class Testrunner {



    public Testrunner( ) {
        System.out.println("Test runner executed");
    }

    public void run(String... args) throws Exception {
        // You can add test code here to run at application startup
        System.out.println("Test runner executed");
    }


}