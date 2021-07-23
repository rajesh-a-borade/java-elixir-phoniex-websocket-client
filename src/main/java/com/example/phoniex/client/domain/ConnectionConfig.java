package com.example.phoniex.client.domain;

import lombok.Data;

@Data
public class ConnectionConfig {
    private String server;
    private String channel;
    private int count;
}
