package com.cbf.testutil;

import com.cbf.base.Transport;
import com.cbf.base.TransportAddress;
import com.cbf.base.TransportReceiver;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.assertj.core.api.Assertions;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class TestAgent {
    private String instanceName;
    private Transport writeStream;
    private Transport readStream;
    private final TransportReceiver transportReceiver;
    private final Set<String> messages = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public TestAgent(String instanceName, int connectionPort) {
        this(instanceName + ":" + connectionPort, TransportAddress.clientConnectionChanel(connectionPort), TransportAddress.serverConnectionChanel(connectionPort));
    }

    public TestAgent(String instanceName,
                     TransportAddress writeStreamAddress,
                     TransportAddress readStreamAddress) {
        this.instanceName = instanceName;
        writeStream = new Transport(TestAgent.class.getSimpleName(), writeStreamAddress);
        readStream = new Transport(TestAgent.class.getSimpleName(), readStreamAddress);
        transportReceiver = new TransportReceiver(readStream, this::onMessage);
        transportReceiver.start();
    }

    public void stop() {
        transportReceiver.stop();
    }

    public void injectMessage(String message) {
        writeStream.send(message);
    }

    private void onMessage(String channel, int streamId, DirectBuffer buffer, int offset, int length, Header header) {
        final int contentLength = buffer.getInt(offset);
        if(contentLength>0) {
            System.out.printf("[%s][%s][%s/%s] received[%d]=%s%n", Thread.currentThread().getName(), instanceName, channel, streamId, length, buffer.getStringAscii(offset));
            messages.add(buffer.getStringAscii(offset));
        }
    }

    public void assertReceivedMessage(String expectedMessage) {
        // await message
        int cnt = 0;
        while (!messages.contains(expectedMessage) && ++cnt < 100) {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Assertions.assertThat(messages).contains(expectedMessage);
        messages.remove(expectedMessage);
    }
}
