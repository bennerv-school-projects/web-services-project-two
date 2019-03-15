package com.bennerv.coordinator.election;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<ElectionEntity, Long> {
    List<ElectionEntity> findByElectionNumber(Integer electionNumber);

    Integer countByElectionNumber(Integer electionNumber);
}
