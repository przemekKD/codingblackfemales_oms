package com.cbf.proxy;

import com.cbf.base.BaseApp;
import com.cbf.base.Transport;
import com.cbf.base.TransportAddress;
import com.cbf.message.CommandBuilder;
import com.cbf.stream.oms.Side;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class Proxy extends BaseApp<Proxy> {
    private Thread mainThread;
    private final CommandBuilder commandBuilder = new CommandBuilder();
    private final Transport clientConnection;
    private volatile boolean isStopped;

    public Proxy(int listenOnPort) {
        super(Proxy.class.getSimpleName());
        clientConnection = new Transport(Proxy.class.getSimpleName(), new TransportAddress(listenOnPort));
    }

    public Proxy start() {
        super.start();
        mainThread = new Thread(this::run);
        mainThread.start();
        return this;
    }

    public void stop() {
        isStopped = true;
    }

    private void run() {

        while (!isStopped) {
            clientConnection.receive(this::onFixMessage);
        }
    }

    protected void onFixMessage(String channel, int streamId, DirectBuffer buffer, int offset, int length, Header header) {
        System.out.printf("[%s][%s][%s/%s] received[%d]=%s%n", Thread.currentThread().getName(), instanceName, channel, streamId, length, buffer.getStringAscii(offset));
        if ("FIX:NewOrderSingle".equals(buffer.getStringAscii(offset))) {
//            send(commandBuilder.createNewOrderSingle()
//                         .ticker("VOD.L")
//                         .side(Side.Buy)
//                         .quantity(100)
//                         .price(7596)
//                         .buffer());
            send(commandBuilder.acceptOrder().id(101).buffer());
        }
    }

    protected void onMessage(DirectBuffer buffer, int offset, int length, Header header) {

    }
}
