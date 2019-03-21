package com.bennerv.coordinator.election;

import com.bennerv.coordinator.api.VoteForParticipantBody;
import com.bennerv.coordinator.participant.ParticipantEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
public class ElectionController {

    private final ElectionService electionService;

    @Autowired
    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @CrossOrigin
    @RequestMapping(path = "/election", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String beginElection() {
        int electionNumber = ElectionNumber.getUniqueElectionNumber();
        log.info("Election " + electionNumber + ": Request to begin election");
        ParticipantEntity participant = electionService.beginElection(electionNumber);

        if (participant == null) {
            log.info("Election " + electionNumber + ": Failed to select an initiator.  There are no participants");
            return "Election " + electionNumber + ": No participants connected!";
        }
        log.info("Election " + electionNumber + ": Initiator selected: " + participant.getPort());
        return "Election " + electionNumber + ": Started!";
    }

    @CrossOrigin
    @RequestMapping(path = "/castvote", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> vote(@RequestBody VoteForParticipantBody voteRequest) {
        if (voteRequest == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        electionService.castVote(voteRequest);
        return ResponseEntity.ok(true);
    }
}
