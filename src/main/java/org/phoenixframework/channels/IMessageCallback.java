package org.phoenixframework.channels;

/**
 * Code copied from https://github.com/eoinsha/JavaPhoenixChannels
 */
public interface IMessageCallback {

    /**
     * @param envelope The envelope containing the message payload and properties
     */
    void onMessage(final Envelope envelope);
}
