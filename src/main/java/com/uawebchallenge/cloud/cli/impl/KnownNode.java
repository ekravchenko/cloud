package com.uawebchallenge.cloud.cli.impl;

import lombok.Value;

@Value
public class KnownNode {

    private String hostIP;
    private Integer port;
}
