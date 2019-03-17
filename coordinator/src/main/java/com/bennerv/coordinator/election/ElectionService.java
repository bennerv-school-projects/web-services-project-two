package com.bennerv.coordinator.election;

import com.bennerv.coordinator.election.Api.NewElectionRequest;
import com.bennerv.coordinator.election.Api.VoteForParticipantBody;
import com.bennerv.coordinator.eureka.EurekaService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
public class ElectionService {

    private static final String INITIATE_ENDPOINT = "/initiate";
    private static final String WINNER_ENDPOINT = "/winner";
    private final EurekaService eurekaService;
    private final VoteRepository voteRepository;


    @Autowired
    public ElectionService(VoteRepository voteRepository, EurekaService eurekaService) {
        this.voteRepository = voteRepository;
        this.eurekaService = eurekaService;
    }

    ServiceInstance beginElection(int electionNumber) {
        ServiceInstance initiator = eurekaService.getRandomParticipant();
        if (initiator == null) {
            return null;
        }

        // Initiator request
        NewElectionRequest electionRequest = NewElectionRequest.builder()
                .electionNumber(electionNumber)
                .build();

        // Tell a service that they are the initiator
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<VoteForParticipantBody> voteResponseResponseEntity = restTemplate.postForEntity(initiator.getScheme() + initiator.getHost() + ":" + initiator.getPort() + INITIATE_ENDPOINT, electionRequest, VoteForParticipantBody.class);

        // Initiator casts vote
        this.castVote(voteResponseResponseEntity.getBody());
        return initiator;
    }

    void castVote(VoteForParticipantBody voteRequest) {
        // Get the vote response of the entity
        VoteEntity electionVote = VoteEntity.builder()
                .electionNumber(voteRequest.getElectionNumber())
                .voterId(voteRequest.getVoter())
                .vote(voteRequest.getVote())
                .build();

        // Save the initiators vote
        voteRepository.save(electionVote);
        checkElectionFinished(voteRequest.getElectionNumber());

        log.info("Election " + voteRequest.getElectionNumber() + ": Participant " + voteRequest.getVoter() + " voted for " + voteRequest.getVote());
    }

    private void checkElectionFinished(int electionNumber) {
        int totalVotes = voteRepository.countByElectionNumber(electionNumber);
        int totalVoters = eurekaService.getParticipants().size();

        if(totalVotes == totalVoters) {
            // todo Send who is the winner to all

        }
    }
}
