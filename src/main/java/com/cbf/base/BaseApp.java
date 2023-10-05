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
    private final MessageListener messageHandler = this::doOnMessage;

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


        while (!isStopped) {
            transport.receive(messageHandler);
        }
    }

    protected void doOnMessage(String channel, int streamId, DirectBuffer buffer, int offset, int length, Header header) {
        System.out.printf("[%s][%s][%s/%s] received[%d]=%s%n", Thread.currentThread().getName(), instanceName, channel, streamId, length, buffer.getStringAscii(offset));
        onMessage(buffer, offset, length, header);
    }

    protected abstract void onMessage(DirectBuffer buffer, int offset, int length, Header header);

    protected void send(String message) {
        transport.send(message);
    }
}
