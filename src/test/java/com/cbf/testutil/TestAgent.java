package com.cbf.testutil;

import com.cbf.base.Transport;

public class TestAgent {
    private Transport transport;

    public TestAgent() {
        transport = new Transport(TestAgent.class.getSimpleName());
    }

    public void send(String message) {
        transport.send(message);
    }
}
