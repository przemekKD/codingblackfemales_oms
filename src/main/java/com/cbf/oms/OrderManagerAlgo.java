package com.cbf.oms;

import com.cbf.base.BaseApp;
import com.cbf.message.CommandBuilder;
import com.cbf.message.EventDispatcher;
import com.cbf.stream.oms.OrderAcceptedDecoder;
import com.cbf.stream.oms.OrderCancelRequestedDecoder;
import com.cbf.stream.oms.OrderPendingDecoder;
import org.agrona.collections.Long2ObjectHashMap;

public class OrderManagerAlgo extends BaseApp<OrderManagerAlgo> {
    private final CommandBuilder commandBuilder = new CommandBuilder();
    private final Long2ObjectHashMap<Order> orderIdToOrder = new Long2ObjectHashMap<>();

    public OrderManagerAlgo() {
        super(OrderManagerAlgo.class.getSimpleName());
    }

    @Override
    protected void init(EventDispatcher eventDispatcher) {
        eventDispatcher.subscribe(OrderPendingDecoder.TEMPLATE_ID, this::onOrderPending);
        eventDispatcher.subscribe(OrderAcceptedDecoder.TEMPLATE_ID, this::onOrderAccepted);
        eventDispatcher.subscribe(OrderCancelRequestedDecoder.TEMPLATE_ID, this::onOrderCancelRequested);
    }

    private void onOrderPending(OrderPendingDecoder orderPending) {
        orderIdToOrder.put(orderPending.id(),
                           new Order()
                                   .setId(orderPending.id())
                                   .setOrderStatus(OrderStatus.Pending)
                                   .setTicker(orderPending.ticker())
                                   .setSide(orderPending.side())
                                   .setQuantity(orderPending.quantity())
                                   .setPrice(orderPending.price()));
        send(commandBuilder.acceptOrder().id(orderPending.id()).buffer());
    }

    private void onOrderAccepted(OrderAcceptedDecoder orderAccepted) {
        Order order = orderIdToOrder.get(orderAccepted.id());
        order.setOrderStatus(OrderStatus.Accepted);
    }

    private void onOrderCancelRequested(OrderCancelRequestedDecoder orderCancelRequested) {
        Order order = orderIdToOrder.get(orderCancelRequested.id());
        if (order == null || order.getOrderStatus() == OrderStatus.Cancelled) {
            send(commandBuilder.rejectOrderCancel().id(orderCancelRequested.id()).buffer());
        } else {
            send(commandBuilder.acceptOrderCancel().id(orderCancelRequested.id()).buffer());
        }
    }
}
