package com.cbf.message;

import com.cbf.stream.oms.*;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class EventBuilder {
    private final UnsafeBuffer sendBuffer = new UnsafeBuffer(ByteBuffer.allocate(1500));
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final OrderPendingEncoder orderPending = new OrderPendingEncoder();
    private final OrderAcceptedEncoder orderAccepted = new OrderAcceptedEncoder();
    private final OrderCancelRequestedEncoder orderCancelRequested = new OrderCancelRequestedEncoder();
    private final OrderCancelAcceptedEncoder orderCancelAccepted = new OrderCancelAcceptedEncoder();
    private final OrderCancelRejectedEncoder orderCancelRejected = new OrderCancelRejectedEncoder();

    public OrderPendingEncoder orderPending() {
        return orderPending.wrapAndApplyHeader(sendBuffer, 0, headerEncoder);
    }

    public OrderAcceptedEncoder orderAccepted() {
        return orderAccepted.wrapAndApplyHeader(sendBuffer, 0, headerEncoder);
    }

    public OrderCancelRequestedEncoder orderCancelRequested() {
        return orderCancelRequested.wrapAndApplyHeader(sendBuffer, 0, headerEncoder);
    }

    public OrderCancelAcceptedEncoder orderCancelAccepted() {
        return orderCancelAccepted.wrapAndApplyHeader(sendBuffer, 0, headerEncoder);
    }

    public OrderCancelRejectedEncoder orderCancelRejected() {
        return orderCancelRejected.wrapAndApplyHeader(sendBuffer, 0, headerEncoder);
    }
}
