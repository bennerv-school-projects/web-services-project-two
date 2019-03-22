package com.bennerv.participant.initiator;


import com.bennerv.participant.api.NewElectionRequest;
import com.bennerv.participant.api.VoteForParticipantBody;
import com.bennerv.participant.register.ParticipantEntity;
import com.bennerv.participant.register.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final RegistrationService registrationService;
    private Environment environment;

    @Autowired
    public InitiatorController(Environment environment, RegistrationService registrationService) {
        this.environment = environment;
        this.registrationService = registrationService;
    }

    @CrossOrigin
    @RequestMapping(path = "/initiate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VoteForParticipantBody> becomeInitiate(@RequestBody NewElectionRequest newElection) {

        // Get a random participant to vote for them
        ParticipantEntity randomParticipant = registrationService.getRandomParticipant();
        VoteForParticipantBody vote = VoteForParticipantBody.builder()
                .electionNumber(newElection.getElectionNumber())
                .vote(randomParticipant.getPort())
                .voter(registrationService.getPort())
                .build();

        return ResponseEntity.ok(vote);
    }
}
