package com.cbf.base;

public class TransportReceiver {
    private final Transport connection;
    private final MessageListener messageListener;
    private Thread mainThread;
    private volatile boolean isStopped;

    public TransportReceiver(Transport connection, MessageListener messageListener) {
        this.connection = connection;
        this.messageListener = messageListener;
    }

    public void start() {
        mainThread = new Thread(this::run);
        mainThread.start();
    }

    public void stop() {
        isStopped = true;
    }

    private void run() {
        while (!isStopped) {
            connection.receive(messageListener);
        }
    }
}
