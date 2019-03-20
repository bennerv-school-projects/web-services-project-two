package com.bennerv.participant.eureka;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    @RequestMapping(value = "/")
    public String home() {
        return "Eureka Client application";
    }
}
