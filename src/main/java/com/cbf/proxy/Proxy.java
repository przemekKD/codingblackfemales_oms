package com.cbf.proxy;

import com.cbf.base.BaseApp;
import com.cbf.base.Transport;
import com.cbf.base.TransportAddress;
import com.cbf.base.TransportReceiver;
import com.cbf.message.CommandBuilder;
import com.cbf.stream.oms.AcceptOrderDecoder;
import com.cbf.stream.oms.MessageHeaderDecoder;
import com.cbf.stream.oms.OrderAcceptedDecoder;
import com.cbf.stream.oms.Side;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class Proxy extends BaseApp<Proxy> {
    private final CommandBuilder commandBuilder = new CommandBuilder();
    private final Transport clientConnection;
    private final TransportReceiver clientConnectionReceiver;
    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();

    public Proxy(int listenOnPort) {
        super(Proxy.class.getSimpleName());
        clientConnection = new Transport(Proxy.class.getSimpleName(), new TransportAddress(listenOnPort));
        clientConnectionReceiver = new TransportReceiver(clientConnection, this::onFixMessage);
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
        System.out.printf("[%s][%s][%s/%s] received[%d]=%s%n", Thread.currentThread().getName(), instanceName, channel, streamId, length, buffer.getStringAscii(offset));
        if ("FIX:NewOrderSingle".equals(buffer.getStringAscii(offset))) {
            send(commandBuilder.createOrder()
                         .ticker("VOD.L")
                         .side(Side.Buy)
                         .quantity(100)
                         .price(7596)
                         .buffer());
        }
    }

    protected void onEventStreamMessage(DirectBuffer buffer, int offset, int length, Header header) {
        headerDecoder.wrap(buffer, offset);
        final int messageId = headerDecoder.templateId();
        System.out.printf("[%s][%s] messageId=%s%n", Thread.currentThread().getName(), instanceName, messageId);
        switch (messageId) {
            case OrderAcceptedDecoder.TEMPLATE_ID:
                OrderAcceptedDecoder orderAccepted = new OrderAcceptedDecoder();
                orderAccepted.wrapAndApplyHeader(buffer, offset, headerDecoder);
                System.out.printf("[%s][%s] received event=%s%n", Thread.currentThread().getName(), instanceName, orderAccepted);
                clientConnection.send("FIX:MsgType=ExecutionReport|OrdStatus=New|OrderID=" + orderAccepted.id());
                return;
        }
    }
}
