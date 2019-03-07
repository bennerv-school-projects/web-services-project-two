package com.bennerv.participant.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping(value = "/")
    public String home() {
        for (String service : discoveryClient.getServices()) {
            for (ServiceInstance instance : discoveryClient.getInstances(service)) {
                System.out.println(instance.getHost() + " " + instance.getPort());
            }
        }

        return "Eureka Client application";
    }
}
