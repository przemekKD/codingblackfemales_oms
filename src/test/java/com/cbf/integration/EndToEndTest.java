package com.cbf.integration;

import com.cbf.gateway.Gateway;
import com.cbf.oms.OrderManagerAlgo;
import com.cbf.proxy.Proxy;
import com.cbf.sequencer.Sequencer;
import com.cbf.testutil.TestAgent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EndToEndTest {
    private TestAgent testAgent;
    private Sequencer sequencer;
    private OrderManagerAlgo orderManagerAlgo;
    private Gateway gateway;
    private Proxy proxy;

    @BeforeEach
    public void setup() {
        testAgent = new TestAgent();
        sequencer = new Sequencer().start();
        proxy = new Proxy().start();
        gateway = new Gateway().start();
        orderManagerAlgo = new OrderManagerAlgo().start();
    }

    @AfterEach
    public void tearDown() {
        gateway.stop();
        proxy.stop();
        orderManagerAlgo.stop();
        sequencer.stop();
    }

    @Test
    public void should_send_order_to_exchange() {
        testAgent.send("FIX:NewOrderSingle");
    }
}
