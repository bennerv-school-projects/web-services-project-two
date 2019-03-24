package com.bennerv.coordinator.participant;


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
@Table(name = "participants")
public class ParticipantEntity {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(name = "PORT")
    private Integer port;

    @NotNull
    @Column(name = "HOST_NAME")
    private String hostname;


    @NotNull
    @Column(name = "PING_SPEED")
    private long ping;
}
