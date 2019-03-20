package com.bennerv.coordinator.election;

import com.bennerv.coordinator.api.NewElectionRequest;
import com.bennerv.coordinator.api.VoteForParticipantBody;
import com.bennerv.coordinator.api.WinnerRequest;
import com.bennerv.coordinator.eureka.EurekaService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NewElectionRequest> request = new HttpEntity<>(electionRequest, headers);
        ResponseEntity<VoteForParticipantBody> voteResponseResponseEntity = restTemplate.exchange("http://" + initiator.getHost() + ":" + initiator.getPort() + INITIATE_ENDPOINT, HttpMethod.POST, request, VoteForParticipantBody.class);

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

        log.info("Election " + voteRequest.getElectionNumber() + ": Participant " + voteRequest.getVoter() + " voted for " + voteRequest.getVote());
        checkElectionFinished(voteRequest.getElectionNumber());


    }

    private void checkElectionFinished(int electionNumber) {
        int totalVotes = voteRepository.countByElectionNumber(electionNumber);
        int totalVoters = eurekaService.getParticipants().size();
        int winner = getWinner(electionNumber);

        if (totalVotes == totalVoters) {
            List<ServiceInstance> participants = eurekaService.getParticipants();

            WinnerRequest winnerRequest = WinnerRequest.builder()
                    .electionNumber(electionNumber)
                    .winner(winner)
                    .build();

            log.info("Election " + electionNumber + ": Winner is " + winner);

            // Build the request
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<WinnerRequest> request = new HttpEntity<>(winnerRequest, headers);

            // Notify every participant of the winner
            for (ServiceInstance participant : participants) {
                restTemplate.exchange("http://" + participant.getHost() + ":" + participant.getPort() + WINNER_ENDPOINT, HttpMethod.POST, request, Boolean.class);
            }
        }
    }

    private int getWinner(int electionNumber) {
        List<VoteEntity> votes = voteRepository.findByElectionNumber(electionNumber);
        Map<Integer, Integer> votesForParticipant = new HashMap<>();
        int winner = -1;

        // Tally votes into a map (voterId, numberOfVotesForThem)
        for (VoteEntity vote : votes) {
            if (votesForParticipant.containsKey(vote.getVoterId())) {
                votesForParticipant.put(vote.getVoterId(), votesForParticipant.get(vote.getVoterId()) + 1);
            } else {
                votesForParticipant.put(vote.getVoterId(), 1);
                winner = vote.getVoterId();
            }
        }

        // Get the person with the most votes
        for (Integer key : votesForParticipant.keySet()) {
            if (votesForParticipant.get(key) > votesForParticipant.get(winner)) {
                winner = key;
            }
        }

        return winner;
    }
}