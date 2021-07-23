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
    private static Map<String, PhoenixChannelClient> CACHE = new ConcurrentHashMap<>();

    @PostMapping("/connections/new")
    public Map<String, Integer> create(@RequestBody ConnectionConfig connectionConfig) throws IOException {
        for(int i = counter.get(); i <= (counter.get() + connectionConfig.getCount()); i++) {
            String userName = "user_" + i;
            PhoenixChannelClient phoenixChannelClient = new PhoenixChannelClient(
                    connectionConfig.getServer(), userName, connectionConfig.getChannel());
            CACHE.put(userName, phoenixChannelClient);
        }
        counter.addAndGet(connectionConfig.getCount());
        return Map.of("total_users", CACHE.keySet().size());
    }

    @GetMapping("/connections/list")
    public Set<String> getAll() {
        return CACHE.keySet();
    }

    @GetMapping("/connections/count")
    public Map<String, Integer> getCount() {
        return Map.of("total_users", CACHE.keySet().size());
    }

}
