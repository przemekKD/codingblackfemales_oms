package com.cbf.sequencer;

import com.cbf.base.BaseApp;
import com.cbf.base.Transport;
import com.cbf.base.TransportAddress;
import com.cbf.message.EventBuilder;
import com.cbf.stream.oms.AcceptOrderDecoder;
import com.cbf.stream.oms.CreateOrderDecoder;
import com.cbf.stream.oms.MessageHeaderDecoder;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class Sequencer extends BaseApp<Sequencer> {
    private final EventBuilder eventBuilder = new EventBuilder();
    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final CreateOrderDecoder createOrderCmd = new CreateOrderDecoder();
    private final AcceptOrderDecoder acceptOrderCmd = new AcceptOrderDecoder();
    private int nextUniqueOrderId = 1;

    public Sequencer() {
        super(Sequencer.class.getSimpleName(),
              new Transport(Sequencer.class.getSimpleName(), TransportAddress.commandStreamAddress()),
              new Transport(Sequencer.class.getSimpleName(), TransportAddress.eventStreamAddress())
        );
    }

    protected void onEventStreamMessage(DirectBuffer buffer, int offset, int length, Header header) {
        headerDecoder.wrap(buffer, offset);
        final int messageId = headerDecoder.templateId();
        System.out.printf("[%s][%s]messageId=%s", Thread.currentThread().getName(), instanceName, messageId);
        switch (messageId) {
            case CreateOrderDecoder.TEMPLATE_ID:
                createOrderCmd.wrapAndApplyHeader(buffer, offset, headerDecoder);
                System.out.printf("[%s][%s] received cmd=%s", Thread.currentThread().getName(), instanceName, createOrderCmd);
                send(eventBuilder.orderPending()
                             .id(nextUniqueOrderId())
                             .ticker(createOrderCmd.ticker())
                             .side(createOrderCmd.side())
                             .quantity(createOrderCmd.quantity())
                             .price(createOrderCmd.price())
                             .buffer());
                return;
            case AcceptOrderDecoder.TEMPLATE_ID:
                acceptOrderCmd.wrapAndApplyHeader(buffer, offset, headerDecoder);
                System.out.printf("[%s][%s] received cmd=%s", Thread.currentThread().getName(), instanceName, acceptOrderCmd);
                send(eventBuilder.acceptOrder()
                             .id(acceptOrderCmd.id())
                             .buffer());
                return;
        }
    }

    private long nextUniqueOrderId() {
        return nextUniqueOrderId++;
    }
}
