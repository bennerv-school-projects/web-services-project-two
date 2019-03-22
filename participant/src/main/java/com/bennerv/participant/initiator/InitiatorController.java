package com.bennerv.participant.initiator;


import com.bennerv.participant.api.NewElectionRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Log4j2
@Controller
public class InitiatorController {

    private final InitiatorService initiatorService;

    @Autowired
    public InitiatorController(InitiatorService initiatorService) {
        this.initiatorService = initiatorService;
    }

    @CrossOrigin
    @RequestMapping(path = "/initiate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> becomeInitiate(@RequestBody NewElectionRequest newElection) {
        boolean success = initiatorService.initiateElection(newElection.getElectionNumber());

        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
