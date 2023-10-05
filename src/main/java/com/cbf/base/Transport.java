package com.cbf.base;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class Transport {
    private static final int STREAM_ID = 1;
    private static final String CHANNEL_ID = "aeron:ipc";
    public final IdleStrategy idle;
    private final UnsafeBuffer unsafeBuffer;
    private final Publication pub;
    public final Subscription sub;
    private final String instanceName;
    static Aeron aeron;

    public Transport(String instanceName) {
        this.instanceName = instanceName;
        idle = new BusySpinIdleStrategy();
        unsafeBuffer = new UnsafeBuffer(ByteBuffer.allocate(1500));

        String aeronDirectoryName = Context.instance().getDriver().aeronDirectoryName();
        System.out.printf("[%s][%s] aeronDirectoryName=[%s]%n", Thread.currentThread().getName(), instanceName, aeronDirectoryName);
        Aeron.Context aeronCtx = new Aeron.Context().aeronDirectoryName(aeronDirectoryName);
        if(aeron==null) {
            aeron = Aeron.connect(aeronCtx);
        }


        sub = aeron.addSubscription(CHANNEL_ID, STREAM_ID);
        pub = aeron.addPublication(CHANNEL_ID, STREAM_ID);

        while (!pub.isConnected()) {
            idle.idle();
        }
    }

    public void send(String message) {
        unsafeBuffer.capacity();
        unsafeBuffer.putStringAscii(0, message);
        System.out.printf("[%s][%s][%s/%s] sending:%s%n", Thread.currentThread().getName(), instanceName, pub.channel(), pub.streamId(), message);
        send(unsafeBuffer);
    }

    public void send(UnsafeBuffer unsafeBuffer) {
        while (pub.offer(unsafeBuffer) < 0) {
            idle.idle();
        }
    }

//        FragmentHandler handler = (buffer, offset, length, header) -> System.out.println("received:" + buffer.getStringAscii(offset));
//        while (sub.poll(handler, 1) <= 0) {
//            idle.idle();
//        }
}
