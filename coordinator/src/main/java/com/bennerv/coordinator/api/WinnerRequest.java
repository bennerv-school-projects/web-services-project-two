package com.bennerv.coordinator.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WinnerRequest {

    @NotNull
    public int electionNumber;

    @NotNull
    public int winner;
}
