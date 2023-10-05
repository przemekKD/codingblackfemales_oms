package com.cbf.base;

public class TransportAddress {
    private static final int COMMAND_STREAM_ID = 1;
    private static final int EVENT_STREAM_ID = 2;
    private static final String CHANNEL_ID = "aeron:ipc";

    private final String channelId;
    private final int streamId;

    public static TransportAddress commandStreamAddress() {
        return new TransportAddress(COMMAND_STREAM_ID);
    }

    public static TransportAddress eventStreamAddress() {
        return new TransportAddress(EVENT_STREAM_ID);
    }

    public TransportAddress(int streamId) {
        this(CHANNEL_ID, streamId);
    }

    public TransportAddress(String channelId, int streamId) {
        this.channelId = channelId;
        this.streamId = streamId;
    }

    public String getChannelId() {
        return channelId;
    }

    public int getStreamId() {
        return streamId;
    }

    @Override
    public String toString() {
        return "TransportAddress{" +
                "channelId='" + channelId + '\'' +
                ", streamId=" + streamId +
                '}';
    }
}
