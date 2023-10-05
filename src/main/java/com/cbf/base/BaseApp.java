package com.cbf.base;

import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public abstract class BaseApp<T> {
    private Thread mainThread;
    private volatile boolean isStopped;
    private final UnsafeBuffer unsafeBuffer = new UnsafeBuffer(ByteBuffer.allocate(1500));
    protected final String instanceName;
    private final Transport listenOnStream;
    private final Transport sendToStream;
    private final MessageListener messageHandler = this::doOnMessage;

    public BaseApp(String instanceName) {
        this(instanceName,
             new Transport(instanceName, TransportAddress.eventStreamAddress()),
             new Transport(instanceName, TransportAddress.commandStreamAddress()));
    }

    public BaseApp(String instanceName,
                   Transport listenOnStream,
                   Transport sendToStream) {
        this.instanceName = instanceName;
        this.listenOnStream = listenOnStream;
        this.sendToStream = sendToStream;
    }

    public T start() {
        mainThread = new Thread(this::run);
        mainThread.start();
        return (T) this;
    }

    public void stop() {
        isStopped = true;
    }

    private void run() {

        while (!isStopped) {
            listenOnStream.receive(messageHandler);
        }
    }

    protected void doOnMessage(String channel, int streamId, DirectBuffer buffer, int offset, int length, Header header) {
        System.out.printf("[%s][%s][%s/%s] received[%d]=%s%n", Thread.currentThread().getName(), instanceName, channel, streamId, length, buffer.getStringAscii(offset));
        onEventStreamMessage(buffer, offset, length, header);
    }

    protected abstract void onEventStreamMessage(DirectBuffer buffer, int offset, int length, Header header);

    protected void send(String message) {
        sendToStream.send(message);
    }

    protected void send(MutableDirectBuffer message) {
        sendToStream.send(message);
    }
}
