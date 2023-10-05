package com.cbf.base;

import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public interface MessageListener {

    void onMessage(String channel, int streamId, DirectBuffer buffer, int offset, int length, Header header);
}
