package com.bennerv.coordinator.election;

import com.bennerv.coordinator.api.AnnounceWinnerBody;
import com.bennerv.coordinator.api.NewElectionRequest;
import com.bennerv.coordinator.api.VoteForParticipantBody;
import com.bennerv.coordinator.participant.ParticipantEntity;
import com.bennerv.coordinator.participant.ParticipantService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final ParticipantService participantService;
    private final VoteRepository voteRepository;


    @Autowired
    public ElectionService(VoteRepository voteRepository, ParticipantService participantService) {
        this.voteRepository = voteRepository;
        this.participantService = participantService;
    }

    ParticipantEntity beginElection(int electionNumber, String algorithm) {
        ParticipantEntity initiator = participantService.getRandomParticipant();
        log.info("Election " + electionNumber + ": Initiator selected: " + initiator.getPort());

        // Initiator request
        NewElectionRequest electionRequest = NewElectionRequest.builder()
                .electionNumber(electionNumber)
                .algorithm(algorithm)
                .build();

        // Tell a service that they are the initiator
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NewElectionRequest> request = new HttpEntity<>(electionRequest, headers);
        restTemplate.exchange("http://" + initiator.getHostname() + ":" + initiator.getPort() + INITIATE_ENDPOINT, HttpMethod.POST, request, VoteForParticipantBody.class);

        // Initiator casts vote
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
        int totalVoters = participantService.getParticipants().size();

        if (totalVotes == totalVoters) {
            int winner = getWinner(electionNumber);
            List<ParticipantEntity> participants = participantService.getParticipants();

            AnnounceWinnerBody winnerRequest = AnnounceWinnerBody.builder()
                    .electionNumber(electionNumber)
                    .winner(winner)
                    .build();

            log.info("Election " + electionNumber + ": Winner is " + winner);

            // Build the request
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AnnounceWinnerBody> request = new HttpEntity<>(winnerRequest, headers);

            // Notify every participant of the winner
            for (ParticipantEntity participant : participants) {
                restTemplate.exchange("http://" + participant.getHostname() + ":" + participant.getPort() + WINNER_ENDPOINT, HttpMethod.POST, request, Boolean.class);
            }
        }
    }

    private int getWinner(int electionNumber) {
        List<VoteEntity> votes = voteRepository.findByElectionNumber(electionNumber);
        Map<Integer, Integer> votesForParticipant = new HashMap<>();
        int winner = -1;

        // Tally votes into a map (voterId, numberOfVotesForThem)
        for (VoteEntity vote : votes) {
            if (votesForParticipant.containsKey(vote.getVote())) {
                votesForParticipant.put(vote.getVote(), votesForParticipant.get(vote.getVote()) + 1);
            } else {
                votesForParticipant.put(vote.getVote(), 1);
                winner = vote.getVote();
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
