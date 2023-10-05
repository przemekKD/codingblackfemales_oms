package com.cbf.testutil;

import com.cbf.base.Transport;
import com.cbf.base.TransportAddress;
import com.cbf.base.TransportReceiver;
import com.cbf.message.EventDispatcher;
import org.agrona.MutableDirectBuffer;
import org.assertj.core.api.Assertions;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TestEventAgent {
    private String instanceName;
    private Transport writeStream;
    private Transport readStream;
    private final TransportReceiver transportReceiver;
    private final EventDispatcher eventDispatcher;
    private final Set<Object> allEvents = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public TestEventAgent(String instanceName) {
        this.instanceName = instanceName;
        writeStream = new Transport(TestAgent.class.getSimpleName(), TransportAddress.commandStreamAddress());
        readStream = new Transport(TestAgent.class.getSimpleName(), TransportAddress.eventStreamAddress());
        eventDispatcher = new EventDispatcher(instanceName);
        eventDispatcher.subscribeAll(this::onAnyEvent);
        transportReceiver = new TransportReceiver(readStream, eventDispatcher.getMessageHandler());
        transportReceiver.start();
    }

    public void stop() {
        transportReceiver.stop();
    }

    public <E> void injectMessage(E event) {
        try {
            Method eventBufferMethod = event.getClass().getMethod("buffer");
            Object buffer = eventBufferMethod.invoke(event);
            MutableDirectBuffer mutableDirectBuffer = (MutableDirectBuffer) buffer;
            writeStream.send(mutableDirectBuffer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void onAnyEvent(Object event){
        allEvents.add(event.toString());
    }

    public <E> void assertEvent(E expectedEvent) {
        String expectedEventString = expectedEvent.toString();
        // await message
        int cnt = 0;
        while (!allEvents.contains(expectedEventString) && ++cnt < 100) {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Assertions.assertThat(allEvents).contains(expectedEventString);
    }
}
