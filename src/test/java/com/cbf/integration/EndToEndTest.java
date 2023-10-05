package com.cbf.integration;

import com.cbf.base.TransportAddress;
import com.cbf.gateway.Gateway;
import com.cbf.oms.OrderManagerAlgo;
import com.cbf.proxy.Proxy;
import com.cbf.sequencer.Sequencer;
import com.cbf.testutil.TestAgent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class EndToEndTest {
    private TestAgent testAgent;
    private Sequencer sequencer;
    private OrderManagerAlgo orderManagerAlgo;
    private Gateway gateway;
    private Proxy proxy;
    private TestAgent fixClient;

    @BeforeEach
    public void setup() {
        testAgent = new TestAgent();
        sequencer = new Sequencer().start();
        final int fixClientPort = 1001;
        proxy = new Proxy(fixClientPort).start();
        fixClient = new TestAgent(fixClientPort);
        gateway = new Gateway().start();
        orderManagerAlgo = new OrderManagerAlgo().start();
    }

    @AfterEach
    public void tearDown() throws Exception {
        gateway.stop();
        proxy.stop();
        orderManagerAlgo.stop();
        sequencer.stop();
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void should_send_order_to_exchange() {
        fixClient.send("FIX:NewOrderSingle");
//        testAgent.got()
    }
}
