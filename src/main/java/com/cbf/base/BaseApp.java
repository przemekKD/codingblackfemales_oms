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
    private final Transport eventStream;
    private final Transport commandStream;
    private final MessageListener messageHandler = this::doOnMessage;

    public BaseApp(String instanceName) {
        this(instanceName,
             new Transport(instanceName, TransportAddress.eventStreamAddress()),
             new Transport(instanceName, TransportAddress.commandStreamAddress()));
    }

    public BaseApp(String instanceName,
                   Transport eventStream,
                   Transport commandStream) {
        this.instanceName = instanceName;
        this.eventStream = eventStream;
        this.commandStream = commandStream;
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
            eventStream.receive(messageHandler);
        }
    }

    protected void doOnMessage(String channel, int streamId, DirectBuffer buffer, int offset, int length, Header header) {
        System.out.printf("[%s][%s][%s/%s] received[%d]=%s%n", Thread.currentThread().getName(), instanceName, channel, streamId, length, buffer.getStringAscii(offset));
        onMessage(buffer, offset, length, header);
    }

    protected abstract void onMessage(DirectBuffer buffer, int offset, int length, Header header);

    protected void send(String message) {
        commandStream.send(message);
    }

    protected void send(MutableDirectBuffer message) {
        commandStream.send(message);
    }
}
