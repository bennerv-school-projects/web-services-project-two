package com.bennerv.coordinator.election.Api;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewElectionRequest {

    @NotNull
    Integer electionNumber;

}
