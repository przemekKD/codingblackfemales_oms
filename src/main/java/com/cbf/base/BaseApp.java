package com.cbf.base;

import com.cbf.message.EventDispatcher;
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
    private final EventDispatcher eventDispatcher;
    private final MessageListener messageHandler;

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
        this.eventDispatcher = new EventDispatcher(instanceName);
        this.messageHandler = eventDispatcher.getMessageHandler();
        init(eventDispatcher);
    }

    protected abstract void init(EventDispatcher eventDispatcher);

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

    protected void send(String message) {
        sendToStream.send(message);
    }

    protected void send(MutableDirectBuffer message) {
        sendToStream.send(message);
    }
}
