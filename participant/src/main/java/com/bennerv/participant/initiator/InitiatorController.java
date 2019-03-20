package com.bennerv.participant.initiator;


import com.bennerv.participant.api.NewElectionRequest;
import com.bennerv.participant.api.VoteForParticipantBody;
import com.bennerv.participant.eureka.EurekaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class InitiatorController {

    private final EurekaService eurekaService;
    private Environment environment;

    @Autowired
    public InitiatorController(Environment environment, EurekaService eurekaService) {
        this.environment = environment;
        this.eurekaService = eurekaService;
    }

    @CrossOrigin
    @RequestMapping(path = "/initiate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VoteForParticipantBody> becomeInitiate(@RequestBody NewElectionRequest newElection) {
        Integer voter = Integer.parseInt(environment.getProperty("local.server.port"));

        // Get a random participant to vote for them
        ServiceInstance randomParticipant = eurekaService.getRandomParticipant();
        VoteForParticipantBody vote = VoteForParticipantBody.builder()
                .electionNumber(newElection.getElectionNumber())
                .vote(randomParticipant.getPort())
                .voter(voter)
                .build();

        return ResponseEntity.ok(vote);
    }
}
