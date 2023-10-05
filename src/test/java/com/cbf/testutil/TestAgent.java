package com.cbf.testutil;

import com.cbf.base.Transport;
import com.cbf.base.TransportAddress;

public class TestAgent {
    private Transport commandStream;
    private Transport eventStream;

    public TestAgent() {
        commandStream = new Transport(TestAgent.class.getSimpleName(), TransportAddress.commandStreamAddress());
        eventStream = new Transport(TestAgent.class.getSimpleName(), TransportAddress.eventStreamAddress());
    }

    public TestAgent(int connectionPort) {
        commandStream = new Transport(TestAgent.class.getSimpleName(), new TransportAddress(connectionPort));
        eventStream = new Transport(TestAgent.class.getSimpleName(), new TransportAddress(connectionPort));
    }

    public void send(String message) {
        commandStream.send(message);
    }
}
