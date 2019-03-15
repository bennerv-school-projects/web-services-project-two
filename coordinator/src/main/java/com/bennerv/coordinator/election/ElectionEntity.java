package com.bennerv.coordinator.election;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "election")
public class ElectionEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "ELECTION_NUMBER")
    private Integer electionNumber;

    @Column(name = "VOTER_ID")
    private Integer voterId;

}
