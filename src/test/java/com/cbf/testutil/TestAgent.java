package com.cbf.testutil;

import com.cbf.base.Transport;
import com.cbf.base.TransportAddress;
import com.cbf.base.TransportReceiver;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class TestAgent {
    private Transport commandStream;
    private Transport eventStream;
    private final TransportReceiver transportReceiver;

    public TestAgent() {
        this(TransportAddress.commandStreamAddress(), TransportAddress.eventStreamAddress());
    }

    public TestAgent(int connectionPort) {
        this(new TransportAddress(connectionPort), new TransportAddress(connectionPort));
    }

    public TestAgent(TransportAddress writeStreamAddress,
                     TransportAddress readStreamAddress) {
        commandStream = new Transport(TestAgent.class.getSimpleName(), writeStreamAddress);
        eventStream = new Transport(TestAgent.class.getSimpleName(), readStreamAddress);
        transportReceiver = new TransportReceiver(eventStream, this::onMessage);
    }

    public void injectCmd(String message) {
        commandStream.send(message);
    }

    private void onMessage(String channel, int streamId, DirectBuffer directBuffer, int offset, int length, Header header) {

    }
}
