package com.uawebchallenge.cloud.cli.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CloudCliOption {

    WORKER("w", "worker"),
    INPUT("i", "input"),
    TASK("t", "task"),
    SCHEDULE("s", "schedule"),
    FILE("f", "file"),
    OUTPUT("o", "output");


    private String code;
    private String longCode;
}
