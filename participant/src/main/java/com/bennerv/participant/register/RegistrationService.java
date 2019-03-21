package com.bennerv.participant.register;

import com.bennerv.participant.api.RegisterParticipantRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Log4j2
@Service
public class RegistrationService {

    final Environment environment;

    @Value("${observer.url}")
    String observerUrl;

    @Autowired
    public RegistrationService(Environment environment) {
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerOnInit() {
        RestTemplate restTemplate = new RestTemplate();

        RegisterParticipantRequest registerRequest = RegisterParticipantRequest.builder()
                .host(getHostname())
                .port(getPort())
                .build();

        ResponseEntity<Boolean> registerResponse = restTemplate.postForEntity(observerUrl + "/register", registerRequest, Boolean.class);
        if(!registerResponse.getStatusCode().equals(HttpStatus.CREATED)) {
            log.error("Failed to create participant.  This will not work :(");
        } else {
            log.info("Registered participant " + getPort() + " with the observer");
        }
    }

    public int getPort() {
        return Integer.parseInt(environment.getProperty("local.server.port"));
    }

    public String getHostname() {
        String hostname = null;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch(UnknownHostException ex) {
            log.error("Failed to get the hostname of the participant.  This won't work :(");
        }
        return hostname;
    }
}

