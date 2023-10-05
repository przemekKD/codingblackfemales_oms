package com.cbf.oms;

import com.cbf.base.BaseApp;
import com.cbf.message.CommandBuilder;
import com.cbf.message.EventDispatcher;
import com.cbf.stream.oms.MessageHeaderDecoder;
import com.cbf.stream.oms.OrderPendingDecoder;

public class OrderManagerAlgo extends BaseApp<OrderManagerAlgo> {
    private final CommandBuilder commandBuilder = new CommandBuilder();
    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();

    public OrderManagerAlgo() {
        super(OrderManagerAlgo.class.getSimpleName());
    }

    @Override
    protected void init(EventDispatcher eventDispatcher) {
        eventDispatcher.subscribe(OrderPendingDecoder.TEMPLATE_ID, this::onOrderPending);
    }

    private void onOrderPending(OrderPendingDecoder orderPending) {
        send(commandBuilder.acceptOrder().id(orderPending.id()).buffer());
    }
}
