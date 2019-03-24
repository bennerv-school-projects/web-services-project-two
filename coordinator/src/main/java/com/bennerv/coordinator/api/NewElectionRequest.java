package com.bennerv.coordinator.api;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewElectionRequest {

    @NotNull
    Integer electionNumber;

    @NotNull
    String algorithm;

}
