package com.cbf.integration;

import com.cbf.gateway.Gateway;
import com.cbf.proxy.Proxy;
import com.cbf.testutil.TestAgent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EndToEndTest {
    private TestAgent testAgent;
    private Gateway gateway;
    private Proxy proxy;

    @BeforeEach
    void setup() {
        testAgent = new TestAgent();
        gateway = new Gateway().start();
        proxy = new Proxy().start();
    }

    @AfterEach
    void tearDown() {
        gateway.stop();
        proxy.stop();
    }

    @Test
    public void should_send_order_to_exchange() {
        testAgent.send("Client:NewOrderSingle");
    }
}
