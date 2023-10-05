package com.cbf.proxy;

import com.cbf.base.BaseApp;
import com.cbf.base.Transport;
import com.cbf.base.TransportAddress;
import com.cbf.base.TransportReceiver;
import com.cbf.message.CommandBuilder;
import com.cbf.stream.oms.Side;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class Proxy extends BaseApp<Proxy> {
    private final CommandBuilder commandBuilder = new CommandBuilder();
    private final Transport clientConnection;
    private final TransportReceiver transportReceiver;

    public Proxy(int listenOnPort) {
        super(Proxy.class.getSimpleName());
        clientConnection = new Transport(Proxy.class.getSimpleName(), new TransportAddress(listenOnPort));
        transportReceiver = new TransportReceiver(clientConnection, this::onFixMessage);
    }

    public Proxy start() {
        super.start();
        transportReceiver.start();
        return this;
    }

    public void stop() {
        super.stop();
        transportReceiver.stop();
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

    }
}
