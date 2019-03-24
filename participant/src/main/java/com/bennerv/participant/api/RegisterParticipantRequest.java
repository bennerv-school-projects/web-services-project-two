package com.bennerv.participant.api;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterParticipantRequest {

    @NotNull
    String host;

    @NotNull
    Integer port;

    @NotNull
    Long ping;
}
