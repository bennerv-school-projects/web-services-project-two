package com.bennerv.coordinator.participant;

import com.bennerv.coordinator.api.RegisterParticipantRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ParticipantController {

    private final ParticipantService participantService;

    @Autowired
    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @CrossOrigin
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registerParticipant(@RequestBody RegisterParticipantRequest request) {
        boolean success = participantService.registerParticipant(request);

        if (success) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
    }

    @CrossOrigin
    @RequestMapping(value = "/participants", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ParticipantEntity[]> getParticipants() {
        List<ParticipantEntity> participants = participantService.getParticipants();

        if (participants == null || participants.size() == 0) {
            return new ResponseEntity<>(HttpStatus.FAILED_DEPENDENCY);
        }

        ParticipantEntity[] participantEntities = participants.toArray(new ParticipantEntity[0]);

        return ResponseEntity.ok(participantEntities);
    }
}
