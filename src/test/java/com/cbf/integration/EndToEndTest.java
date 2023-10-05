package com.cbf.integration;

import static org.assertj.core.api.Assertions.*;
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
    private TestAgent eventStreamAgent;
    private Sequencer sequencer;
    private OrderManagerAlgo orderManagerAlgo;
    private Gateway gateway;
    private Proxy proxy;
    private TestAgent fixClient;

    @BeforeEach
    public void setup() {
        eventStreamAgent = new TestAgent("eventStreamAgent");
        sequencer = new Sequencer().start();
        final int fixClientPort = 1001;
        proxy = new Proxy(fixClientPort).start();
        fixClient = new TestAgent("fixClient", fixClientPort);
        gateway = new Gateway().start();
        orderManagerAlgo = new OrderManagerAlgo().start();
    }

    @AfterEach
    public void tearDown() {
        gateway.stop();
        proxy.stop();
        fixClient.stop();
        orderManagerAlgo.stop();
        sequencer.stop();
    }

    @Test
    public void should_send_order_to_exchange() {
        // when
        fixClient.injectCmd("FIX:NewOrderSingle");
        // then
        fixClient.assertReceivedMessage("FIX:MsgType=ExecutionReport|OrdStatus=New|OrderID=1");
    }
}
