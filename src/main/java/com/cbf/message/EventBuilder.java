package com.cbf.message;

import com.cbf.stream.oms.MessageHeaderEncoder;
import com.cbf.stream.oms.OrderAcceptedEncoder;
import com.cbf.stream.oms.OrderPendingEncoder;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class EventBuilder {
    private final UnsafeBuffer sendBuffer = new UnsafeBuffer(ByteBuffer.allocate(1500));
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final OrderPendingEncoder orderPending = new OrderPendingEncoder();
    private final OrderAcceptedEncoder orderAccepted = new OrderAcceptedEncoder();

    public OrderPendingEncoder orderPending(){
        return orderPending.wrapAndApplyHeader(sendBuffer, 0, headerEncoder);
    }

    public OrderAcceptedEncoder orderAccepted(){
        return orderAccepted.wrapAndApplyHeader(sendBuffer, 0, headerEncoder);
    }
}
