package com.bennerv.participant.register;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantEntity {


    private Long id;

    private Integer port;

    private String hostname;

    private long ping;

}
