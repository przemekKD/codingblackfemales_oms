package com.cbf.base;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class Transport {
    private final String instanceName;
    private final TransportAddress address;
    private final Aeron aeron;
    private final UnsafeBuffer unsafeBuffer;
    private final Publication pub;
    private final IdleStrategy idle;
    private final Subscription sub;
    private final FragmentHandler messageHandler = this::doOnMessage;
    private MessageListener messageListener;

    public Transport(String instanceName, TransportAddress address) {
        this.instanceName = instanceName;
        this.address = address;
        idle = new BusySpinIdleStrategy();
        unsafeBuffer = new UnsafeBuffer(ByteBuffer.allocate(1500));

        String aeronDirectoryName = Context.instance().getDriver().aeronDirectoryName();
        System.out.printf("[%s][%s] aeronDirectoryName=[%s]%n", Thread.currentThread().getName(), instanceName, aeronDirectoryName);
        Aeron.Context aeronCtx = new Aeron.Context().aeronDirectoryName(aeronDirectoryName);
        aeron = Aeron.connect(aeronCtx);


        sub = aeron.addSubscription(address.getChannelId(), address.getStreamId());
        pub = aeron.addPublication(address.getChannelId(), address.getStreamId());

        while (!pub.isConnected()) {
            idle.idle();
        }
    }

    public void receive(final MessageListener messageListener) {
        this.messageListener = messageListener;
        try {
            while (sub.poll(messageHandler, 1) <= 0) {
                idle.idle();
            }
        }finally {
            this.messageListener = null;
        }
    }

    protected void doOnMessage(DirectBuffer buffer, int offset, int length, Header header) {
        this.messageListener.onMessage(sub.channel(), sub.streamId(), buffer, offset, length, header);
    }

    public void send(String message) {
        unsafeBuffer.putStringAscii(0, message);
        //System.out.printf("[%s][%s][%s/%s] sending:%s%n", Thread.currentThread().getName(), instanceName, pub.channel(), pub.streamId(), message);
        send(unsafeBuffer);
    }

    public void send(MutableDirectBuffer unsafeBuffer) {
        long result;
        while ((result = pub.offer(unsafeBuffer)) < 0) {
            idle.idle();
        }
        //System.out.printf("[%s][%s][%s/%s] send success result=%s", Thread.currentThread().getName(), instanceName, address.getChannelId(), address.getStreamId(), result);
    }
}
