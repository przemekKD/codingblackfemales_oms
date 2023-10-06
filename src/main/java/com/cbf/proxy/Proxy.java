package com.cbf.proxy;

import com.cbf.base.BaseApp;
import com.cbf.base.Transport;
import com.cbf.base.TransportAddress;
import com.cbf.base.TransportReceiver;
import com.cbf.fix.FixMessage;
import com.cbf.fix.FixTag;
import com.cbf.message.CommandBuilder;
import com.cbf.message.EventDispatcher;
import com.cbf.stream.oms.*;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class Proxy extends BaseApp<Proxy> {
    private final CommandBuilder commandBuilder = new CommandBuilder();
    private final FixMessage fixMessage = new FixMessage();
    private final Transport writeClientConnectionChannel;
    private final Transport readClientConnectionChannel;
    private final TransportReceiver clientConnectionReceiver;

    public Proxy(int listenOnPort) {
        super(Proxy.class.getSimpleName());
        writeClientConnectionChannel = new Transport(Proxy.class.getSimpleName(), TransportAddress.serverConnectionChanel(listenOnPort));
        readClientConnectionChannel = new Transport(Proxy.class.getSimpleName(), TransportAddress.clientConnectionChanel(listenOnPort));
        clientConnectionReceiver = new TransportReceiver(readClientConnectionChannel, this::onFixMessage);
    }

    @Override
    protected void init(EventDispatcher eventDispatcher) {
        eventDispatcher.subscribe(OrderAcceptedDecoder.TEMPLATE_ID, this::onOrderAccepted);
        eventDispatcher.subscribe(OrderCancelRequestedDecoder.TEMPLATE_ID, this::onOrderCancelRequested);
        eventDispatcher.subscribe(OrderCancelAcceptedDecoder.TEMPLATE_ID, this::onOrderCancelAccepted);
        eventDispatcher.subscribe(OrderCancelRejectedDecoder.TEMPLATE_ID, this::onOrderCancelRejected);
    }

    public Proxy start() {
        super.start();
        clientConnectionReceiver.start();
        return this;
    }

    public void stop() {
        super.stop();
        clientConnectionReceiver.stop();
    }

    protected void onFixMessage(String channel, int streamId, DirectBuffer buffer, int offset, int length, Header header) {
        final int contentLength = buffer.getInt(offset);
        if (contentLength > 0) {
            String message = buffer.getStringAscii(offset);
            System.out.printf("[%s][%s][%s/%s] received[%d][%d]=%s%n", Thread.currentThread().getName(), instanceName, channel, streamId, header.type(), length, message);
            if (message.startsWith("FIX:")) {
                fixMessage.parse(message);
                if ("NewOrderSingle".equals(fixMessage.get(FixTag.MsgType))) {
                    send(commandBuilder.createOrder()
                                 .ticker(fixMessage.get(FixTag.Symbol))
                                 .side(Side.valueOf(fixMessage.get(FixTag.Side)))
                                 .quantity(fixMessage.getInt(FixTag.OrderQty))
                                 .price(fixMessage.getDecimalAsLong(FixTag.Price))
                                 .buffer());
                } else if ("OrderCancelRequest".equals(fixMessage.get(FixTag.MsgType))) {
                    send(commandBuilder.requestCancelOrder()
                                 .id(fixMessage.getInt(FixTag.OrderID))
                                 .buffer());
                }
            }
        }
    }

    private void onOrderAccepted(OrderAcceptedDecoder orderAccepted) {
        writeClientConnectionChannel.send("FIX:MsgType=ExecutionReport|OrdStatus=New|OrderID=" + orderAccepted.id());
    }

    private void onOrderCancelRequested(OrderCancelRequestedDecoder orderCancelRequested) {
        writeClientConnectionChannel.send("FIX:MsgType=ExecutionReport|ExecType=PendingCancel|OrderID=" + orderCancelRequested.id());
    }

    private void onOrderCancelAccepted(OrderCancelAcceptedDecoder orderCancelAccepted) {
        writeClientConnectionChannel.send("FIX:MsgType=ExecutionReport|ExecType=Canceled|OrderID=" + orderCancelAccepted.id());
    }

    private void onOrderCancelRejected(OrderCancelRejectedDecoder orderCancelRejected) {
        writeClientConnectionChannel.send("FIX:MsgType=OrderCancelReject|OrderID=" + orderCancelRejected.id());
    }
}
