package com.kubernetes.hello.world.kubehelloworld.controller;

import com.kubernetes.hello.world.kubehelloworld.serverinfo.ServerInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @Autowired
    private ServerInformationService serverInfo;

    @GetMapping(path = "/hello")
    public String helloWorld() {
        return "Hello World " + " v1 " + serverInfo.getServerInfo();
    }

}
