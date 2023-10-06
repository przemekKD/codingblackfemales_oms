package com.cbf.message;

import com.cbf.stream.oms.*;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class CommandBuilder {
    private final UnsafeBuffer sendBuffer = new UnsafeBuffer(ByteBuffer.allocate(1500));
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final CreateOrderEncoder createOrder = new CreateOrderEncoder();
    private final AcceptOrderEncoder acceptOrder = new AcceptOrderEncoder();
    private final RequestCancelOrderEncoder requestCancelOrder = new RequestCancelOrderEncoder();
    private final AcceptOrderCancelEncoder acceptOrderCancel = new AcceptOrderCancelEncoder();
    private final RejectOrderCancelEncoder rejectOrderCancel = new RejectOrderCancelEncoder();

    public CreateOrderEncoder createOrder() {
        return createOrder.wrapAndApplyHeader(sendBuffer, 0, headerEncoder);
    }

    public AcceptOrderEncoder acceptOrder() {
        return acceptOrder.wrapAndApplyHeader(sendBuffer, 0, headerEncoder);
    }

    public RequestCancelOrderEncoder requestCancelOrder() {
        return requestCancelOrder.wrapAndApplyHeader(sendBuffer, 0, headerEncoder);
    }

    public AcceptOrderCancelEncoder acceptOrderCancel() {
        return acceptOrderCancel.wrapAndApplyHeader(sendBuffer, 0, headerEncoder);
    }

    public RejectOrderCancelEncoder rejectOrderCancel() {
        return rejectOrderCancel.wrapAndApplyHeader(sendBuffer, 0, headerEncoder);
    }
}
