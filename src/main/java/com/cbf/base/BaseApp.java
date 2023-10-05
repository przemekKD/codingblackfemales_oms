package com.cbf.base;

import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public abstract class BaseApp<T> {
    private Thread mainThread;
    private volatile boolean isStopped;
    private final UnsafeBuffer unsafeBuffer = new UnsafeBuffer(ByteBuffer.allocate(1500));
    private final String instanceName;
    private final Transport transport;
    private final FragmentHandler messageHandler = this::doOnMessage;

    public BaseApp(String instanceName) {
        this.instanceName = instanceName;
        transport = new Transport(instanceName);
    }

    public T start() {
        mainThread = new Thread(this::run);
        mainThread.start();
        return (T) this;
    }

    public void stop() {
        isStopped = true;
    }

    public void run() {
        final String message = instanceName + ":start";
        transport.send(message);


        while(!isStopped) {
            while (transport.sub.poll(messageHandler, 1) <= 0) {
                transport.idle.idle();
            }
        }
    }

    protected void doOnMessage(DirectBuffer buffer, int offset, int length, Header header){
        System.out.printf("[%s][%s][%s/%s] received[%d]=%s%n", Thread.currentThread().getName(), instanceName, transport.sub.channel(), transport.sub.streamId(), length, buffer.getStringAscii(offset));
        onMessage(buffer, offset, length, header);
    }

    protected abstract void onMessage(DirectBuffer buffer, int offset, int length, Header header);
}
