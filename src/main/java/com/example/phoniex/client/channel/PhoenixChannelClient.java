package com.example.phoniex.client.channel;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.phoenixframework.channels.*;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.phoenixframework.channels.ChannelEvent;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IMessageCallback;

import java.io.IOException;

@Slf4j
public class PhoenixChannelClient {

    private String userName;
    private Socket socket;
    private Channel channel;
    private long delay = 0l;

    public PhoenixChannelClient(String socketUrl, String userName, String channelName) throws IOException {
        this.userName = userName;
        socket = new Socket(socketUrl);
        socket.connect();
        channel = socket.chan(channelName, null);

        channel.join()
                .receive("ignore", new IMessageCallback() {
                    @Override
                    public void onMessage(Envelope envelope) {
                        logInfoWithUserName("IGNORE");
                    }
                })
                .receive("ok", new IMessageCallback() {
                    @Override
                    public void onMessage(Envelope envelope) {
                        logInfoWithUserName("JOINED :" + envelope.getPayloadAsString());
                    }
                });

        channel.on("shout", new IMessageCallback() {
            @Override
            public void onMessage(Envelope envelope) {
                logInfoWithUserName("INBOX: " + envelope.getPayloadAsString());
            }
        });

        channel.on(ChannelEvent.CLOSE.getPhxEvent(), new IMessageCallback() {
            @Override
            public void onMessage(Envelope envelope) {
                logInfoWithUserName("CLOSED: " + envelope.getPayloadAsString());
            }
        });

        channel.on(ChannelEvent.ERROR.getPhxEvent(), new IMessageCallback() {
            @Override
            public void onMessage(Envelope envelope) {
                logErrorWithUserName("ERROR: " + envelope.getPayloadAsString());
            }
        });
    }

    private final void logInfoWithUserName(String msg) {
        log.info("{} - {} - {}", userName, channel.getTopic(), msg);
    }

    private final void logErrorWithUserName(String msg) {
        log.info("{} - {} - {}", userName, channel.getTopic(), msg);
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @SneakyThrows
    public void sendMessage(String message) {
        Thread.sleep(this.delay);
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance)
                .put("name", this.userName)
                .put("body", message);
    }
}
