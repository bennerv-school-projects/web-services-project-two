package com.bennerv.participant.register;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantEntity {


    private Long id;

    private Integer port;

    private String hostname;

}
