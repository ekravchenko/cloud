package com.uawebchallenge.cloud;

import com.uawebchallenge.cloud.cli.CloudCli;
import com.uawebchallenge.cloud.cli.CloudCliService;
import com.uawebchallenge.cloud.cli.impl.DefaultCloudCli;
import com.uawebchallenge.cloud.cli.impl.DefaultCloudCliService;

public class Application {

    public static void main(String[] args) {
        CloudCliService cloudCliService = new DefaultCloudCliService();
        CloudCli cloud = new DefaultCloudCli(cloudCliService);
        cloud.execute(args);
    }
}
