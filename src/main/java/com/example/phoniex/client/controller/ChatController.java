package com.example.phoniex.client.controller;

import com.example.phoniex.client.channel.PhoenixChannelClient;
import com.example.phoniex.client.domain.ConnectionConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class ChatController {

    private static AtomicInteger counter = new AtomicInteger(0);
    private static Map<String, PhoenixChannelClient> ALL_CONNECTIONS = new ConcurrentHashMap<>();

    @PostMapping("/connections/new")
    public Map<String, Integer> create(@RequestBody ConnectionConfig connectionConfig) throws IOException {
        for(int i = counter.get(); i < (counter.get() + connectionConfig.getCount()); i++) {
            String userName = "user_" + i;
            PhoenixChannelClient phoenixChannelClient = new PhoenixChannelClient(
                    connectionConfig.getServer(), userName, connectionConfig.getChannel());
            ALL_CONNECTIONS.put(userName, phoenixChannelClient);
        }
        counter.addAndGet(connectionConfig.getCount());
        return Map.of("total_users", ALL_CONNECTIONS.keySet().size());
    }

    @PostMapping("/connections/message/hello/all")
    public Map<String, String> send(@RequestBody String empty) {
        if(ALL_CONNECTIONS.keySet().size() < 1) {
            return Map.of("status", "failed");
        }
        ALL_CONNECTIONS.get(ALL_CONNECTIONS.keySet().iterator().next()).sendMessage("Hello All ...");
        return Map.of("status", "success");
    }

    @GetMapping("/connections/list")
    public Set<String> getAll() {
        return ALL_CONNECTIONS.keySet();
    }

    @GetMapping("/connections/count")
    public Map<String, Integer> getCount() {
        return Map.of("total_users", ALL_CONNECTIONS.keySet().size());
    }

}
