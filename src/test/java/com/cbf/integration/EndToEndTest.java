package com.cbf.integration;

import com.cbf.gateway.Gateway;
import com.cbf.message.EventBuilder;
import com.cbf.oms.OrderManagerAlgo;
import com.cbf.proxy.Proxy;
import com.cbf.sequencer.Sequencer;
import com.cbf.stream.oms.Side;
import com.cbf.testutil.TestAgent;
import com.cbf.testutil.TestEventAgent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EndToEndTest {
    private EventBuilder eventBuilder = new EventBuilder();
    private TestEventAgent eventStreamAgent;
    private Sequencer sequencer;
    private OrderManagerAlgo orderManagerAlgo;
    private Gateway gateway;
    private Proxy proxy;
    private TestAgent fixClient;

    @BeforeEach
    public void setup() {
        eventStreamAgent = new TestEventAgent("eventStreamAgent");
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
    public void should_accept_fix_order() {
        // when
        fixClient.injectMessage("FIX:MsgType=NewOrderSingle|Symbol=VOD.L|Side=Buy|OrderQty=100|Price=75.96");
        // then
        eventStreamAgent.assertEvent(eventBuilder.orderPending().id(1).ticker("VOD.L").side(Side.Buy).quantity(100).price(7596));
        eventStreamAgent.assertEvent(eventBuilder.orderAccepted().id(1));
        fixClient.assertReceivedMessage("FIX:MsgType=ExecutionReport|OrdStatus=New|OrderID=1");
        // when
        fixClient.injectMessage("FIX:MsgType=OrderCancelRequest|OrderID=1");
    }

    @Test
    public void should_cancel_accept_fix_order() {
        // when
        fixClient.injectMessage("FIX:MsgType=NewOrderSingle|Symbol=VOD.L|Side=Buy|OrderQty=100|Price=75.96");
        // then
        eventStreamAgent.assertEvent(eventBuilder.orderPending().id(1).ticker("VOD.L").side(Side.Buy).quantity(100).price(7596));
        eventStreamAgent.assertEvent(eventBuilder.orderAccepted().id(1));
        fixClient.assertReceivedMessage("FIX:MsgType=ExecutionReport|OrdStatus=New|OrderID=1");

        // when
        fixClient.injectMessage("FIX:MsgType=OrderCancelRequest|OrderID=1");
        // then
        eventStreamAgent.assertEvent(eventBuilder.orderCancelRequested().id(1));
        fixClient.assertReceivedMessage("FIX:MsgType=ExecutionReport|ExecType=PendingCancel|OrderID=1");
        eventStreamAgent.assertEvent(eventBuilder.orderCancelAccepted().id(1));
        fixClient.assertReceivedMessage("FIX:MsgType=ExecutionReport|ExecType=Canceled|OrderID=1");
    }

    @Test
    public void should_cancel_reject_unknown_order() {
        // when
        fixClient.injectMessage("FIX:MsgType=OrderCancelRequest|OrderID=2");
        // then
        eventStreamAgent.assertEvent(eventBuilder.orderCancelRequested().id(2));
        fixClient.assertReceivedMessage("FIX:MsgType=ExecutionReport|ExecType=PendingCancel|OrderID=2");
        eventStreamAgent.assertEvent(eventBuilder.orderCancelRejected().id(2));
        fixClient.assertReceivedMessage("FIX:MsgType=OrderCancelReject|OrderID=2");
    }
}
