package com.bennerv.participant.voter;


import com.bennerv.participant.api.AnnounceWinnerBody;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Log4j2
@Controller
public class VoteController {

    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @CrossOrigin
    @RequestMapping(path = "/vote", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> vote(@RequestParam @NotNull Integer electionNumber, @RequestParam @NotNull String algorithm) {

        boolean success = voteService.voteForParticipant(electionNumber, algorithm);

        if (!success) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(path = "/winner", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getWinner(@RequestBody AnnounceWinnerBody request) {
        if (request == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        log.info("Election " + request.getElectionNumber() + ": Informed of winner " + request.getWinner());
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
