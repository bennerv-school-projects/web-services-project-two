package com.bennerv.participant.eureka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EurekaService {

    private final DiscoveryClient discoveryClient;

    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    public EurekaService(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public List<ServiceInstance> getParticipants() {
        for (String service : discoveryClient.getServices()) {
            if (service.equals(appName)) {
                return discoveryClient.getInstances(service);
            }
        }
        return null;
    }

    public ServiceInstance getRandomParticipant() {
        List<ServiceInstance> participants = this.getParticipants();
        if (participants == null) {
            return null;
        }

        // Get random number
        int random = ThreadLocalRandom.current().nextInt(0, participants.size());
        return participants.get(random);
    }

    public ServiceInstance getCoordinator() {
        for (String service : discoveryClient.getServices()) {
            if (!service.equals(appName)) {
                return discoveryClient.getInstances(service).get(0);
            }
        }
        return null;
    }

}

