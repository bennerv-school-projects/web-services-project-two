package com.bennerv.coordinator.participant;

import com.bennerv.coordinator.api.RegisterParticipantRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.ConnectException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Log4j2
@Service
public class ParticipantService {


    final
    ParticipantRepository participantRepository;

    @Autowired
    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public boolean registerParticipant(RegisterParticipantRequest request) {
        ParticipantEntity participantEntity = ParticipantEntity.builder()
                .hostname(request.getHost())
                .port(request.getPort())
                .build();

        if (participantRepository.existsParticipantEntityByPort(request.getPort())) {
            return false;
        }

        log.info("Registered participant " + participantEntity.getPort() + " with the observer");
        participantRepository.save(participantEntity);
        return true;
    }

    public List<ParticipantEntity> getParticipants() {
        return participantRepository.findAll();
    }

    public ParticipantEntity getRandomParticipant() {
        List<ParticipantEntity> participants = getParticipants();
        if(participants.size() == 0) {
            return null;
        }

        int randomNumber = ThreadLocalRandom.current().nextInt(0, participants.size());
        return participants.get(randomNumber);

    }

    @Scheduled(fixedRate = 15000)
    public void checkParticipantHealth() {
        log.info("I'm here in fixed rate!");
        List<ParticipantEntity> participants = getParticipants();

        RestTemplate restTemplate = new RestTemplate();

        // Check if the participants are still up
        for (ParticipantEntity participant : participants) {

            // Call actuator health endpoint to check status code == 200
            // If not 200, then delete from database
            try {
                ResponseEntity<String> response = restTemplate.getForEntity("http://" + participant.getHostname() + ":" + participant.getPort() + "/actuator/health", String.class);
            } catch(Exception e) {
                log.info("Observer dropped participant " + participant.getPort());
                participantRepository.delete(participant);
            }
        }
    }
}
