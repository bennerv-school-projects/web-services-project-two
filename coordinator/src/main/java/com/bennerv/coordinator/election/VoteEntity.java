package com.bennerv.coordinator.election;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vote")
class VoteEntity {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(name = "ELECTION_NUMBER")
    private Integer electionNumber;

    @NotNull
    @Column(name = "VOTER_ID")
    private Integer voterId;

    @NotNull
    @Column(name = "VOTE")
    private Integer vote;

}
