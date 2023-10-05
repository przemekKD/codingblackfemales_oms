package com.cbf.oms;

import com.cbf.base.BaseApp;
import com.cbf.message.CommandBuilder;
import com.cbf.stream.oms.MessageHeaderDecoder;
import com.cbf.stream.oms.OrderPendingDecoder;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class OrderManagerAlgo extends BaseApp<OrderManagerAlgo> {
    private final CommandBuilder commandBuilder = new CommandBuilder();
    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();

    public OrderManagerAlgo() {
        super(OrderManagerAlgo.class.getSimpleName());
    }

    protected void onEventStreamMessage(DirectBuffer buffer, int offset, int length, Header header) {
        headerDecoder.wrap(buffer, offset);
        final int messageId = headerDecoder.templateId();
        System.out.printf("[%s][%s] messageId=%s%n", Thread.currentThread().getName(), instanceName, messageId);
        switch (messageId) {
            case OrderPendingDecoder.TEMPLATE_ID:
                OrderPendingDecoder orderPending = new OrderPendingDecoder();
                orderPending.wrapAndApplyHeader(buffer, offset, headerDecoder);
                System.out.printf("[%s][%s] received event=%s%n", Thread.currentThread().getName(), instanceName, orderPending);
                send(commandBuilder.acceptOrder().id(orderPending.id()).buffer());
                return;
        }
    }
}
