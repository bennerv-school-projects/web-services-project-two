package com.bennerv.participant.initiator;

import com.bennerv.participant.register.ParticipantEntity;
import com.bennerv.participant.register.RegistrationService;
import com.bennerv.participant.voter.VoteService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
public class InitiatorService {

    private final VoteService voteService;
    private final RegistrationService registrationService;

    @Autowired
    public InitiatorService(VoteService voteService, RegistrationService registrationService) {
        this.voteService = voteService;
        this.registrationService = registrationService;
    }


    public boolean initiateElection(Integer electionNumber, String algorithm) {
        boolean success = voteService.voteForParticipant(electionNumber, algorithm);

        // Send /vote request to every participant
        RestTemplate restTemplate = new RestTemplate();

        for (ParticipantEntity participant : registrationService.getParticipants()) {
            // Make sure initiator doesn't vote twice
            if (!participant.getPort().equals(registrationService.getPort())) {
                restTemplate.getForEntity("http://" + participant.getHostname() + ":" + participant.getPort() + "/vote?electionNumber=" + electionNumber + "&algorithm=" + algorithm, Object.class);
            }
        }

        return success;
    }
}
