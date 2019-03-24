package com.bennerv.participant.voter;

import com.bennerv.participant.api.VoteForParticipantBody;
import com.bennerv.participant.register.ParticipantEntity;
import com.bennerv.participant.register.RegistrationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
public class VoteService {

    private final RegistrationService registrationService;

    @Value("${observer.url}")
    private String observerUrl;

    @Autowired
    public VoteService(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    public boolean voteForParticipant(Integer electionNumber, String algorithm) {
        ParticipantEntity selectedParticipant;

        if (algorithm.equalsIgnoreCase("FASTEST") || algorithm.equalsIgnoreCase("FAST")) {
            selectedParticipant = registrationService.getFastestParticipant();
        } else {
            selectedParticipant = registrationService.getRandomParticipant();
        }

        log.info("Election " + electionNumber + ": Participant " + registrationService.getPort() + " voting for " + selectedParticipant.getPort());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        VoteForParticipantBody voteRequestBody = VoteForParticipantBody.builder()
                .electionNumber(electionNumber)
                .voter(registrationService.getPort())
                .vote(selectedParticipant.getPort())
                .build();

        HttpEntity<VoteForParticipantBody> voteRequest = new HttpEntity<>(voteRequestBody, headers);
        ResponseEntity<Object> responseEntity = restTemplate.exchange(observerUrl + "/castvote", HttpMethod.POST, voteRequest, Object.class);

        return responseEntity.getStatusCode().equals(HttpStatus.OK);
    }
}
