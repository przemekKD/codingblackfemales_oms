package com.cbf.sequencer;

import com.cbf.base.Transport;
import com.cbf.base.TransportAddress;
import com.cbf.base.TransportReceiver;
import com.cbf.message.EventBuilder;
import com.cbf.stream.oms.AcceptOrderDecoder;
import com.cbf.stream.oms.CreateOrderDecoder;
import com.cbf.stream.oms.MessageHeaderDecoder;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class Sequencer {
    protected final String instanceName;
    private final Transport listenOnStream;
    private final Transport sendToStream;
    private final TransportReceiver transportReceiver;
    private final EventBuilder eventBuilder = new EventBuilder();
    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final CreateOrderDecoder createOrderCmd = new CreateOrderDecoder();
    private final AcceptOrderDecoder acceptOrderCmd = new AcceptOrderDecoder();
    private int nextUniqueOrderId = 1;

    public Sequencer() {
        instanceName = Sequencer.class.getSimpleName();
        listenOnStream = new Transport(Sequencer.class.getSimpleName(), TransportAddress.commandStreamAddress());
        sendToStream = new Transport(Sequencer.class.getSimpleName(), TransportAddress.eventStreamAddress());
        transportReceiver = new TransportReceiver(listenOnStream, this::onEventStreamMessage);
    }

    public Sequencer start() {
        transportReceiver.start();
        return this;
    }

    public void stop() {
        transportReceiver.stop();
    }

    protected void onEventStreamMessage(String channel, int streamId, DirectBuffer buffer, int offset, int length, Header header) {
        headerDecoder.wrap(buffer, offset);
        final int messageId = headerDecoder.templateId();
        //System.out.printf("[%s][%s] messageId=%s%n", Thread.currentThread().getName(), instanceName, messageId);
        switch (messageId) {
            case CreateOrderDecoder.TEMPLATE_ID:
                createOrderCmd.wrapAndApplyHeader(buffer, offset, headerDecoder);
                System.out.printf("[%s][%s] received cmd=%s%n", Thread.currentThread().getName(), instanceName, createOrderCmd);
                sendToStream.send(eventBuilder.orderPending()
                                          .id(nextUniqueOrderId())
                                          .ticker(createOrderCmd.ticker())
                                          .side(createOrderCmd.side())
                                          .quantity(createOrderCmd.quantity())
                                          .price(createOrderCmd.price())
                                          .buffer());
                return;
            case AcceptOrderDecoder.TEMPLATE_ID:
                acceptOrderCmd.wrapAndApplyHeader(buffer, offset, headerDecoder);
                System.out.printf("[%s][%s] received cmd=%s%n", Thread.currentThread().getName(), instanceName, acceptOrderCmd);
                sendToStream.send(eventBuilder.orderAccepted()
                                          .id(acceptOrderCmd.id())
                                          .buffer());
                return;
        }
    }

    private long nextUniqueOrderId() {
        return nextUniqueOrderId++;
    }
}
