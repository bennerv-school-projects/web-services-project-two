package com.bennerv.coordinator.participant;

import com.bennerv.coordinator.api.RegisterParticipantRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        if (participants.size() == 0) {
            return null;
        }

        int randomNumber = ThreadLocalRandom.current().nextInt(0, participants.size());
        return participants.get(randomNumber);

    }

    @Scheduled(fixedRate = 15000)
    public void checkParticipantHealth() {
        List<ParticipantEntity> participants = getParticipants();

        RestTemplate restTemplate = new RestTemplate();

        // Check if the participants are still up
        for (ParticipantEntity participant : participants) {

            // Call actuator health endpoint on the participant.  If exception, then delete from database
            try {
                restTemplate.getForEntity("http://" + participant.getHostname() + ":" + participant.getPort() + "/actuator/health", String.class);
            } catch (Exception e) {
                log.info("Observer dropped participant " + participant.getPort());
                participantRepository.delete(participant);
            }
        }
    }
}
