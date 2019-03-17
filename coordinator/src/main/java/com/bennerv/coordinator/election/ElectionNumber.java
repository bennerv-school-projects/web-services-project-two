package com.bennerv.coordinator.election;

import lombok.Synchronized;

public class ElectionNumber {

    private static int electionNumber = 0;

    @Synchronized
    public static int getUniqueElectionNumber() {
        return ++electionNumber;
    }

    private ElectionNumber() { }
}
