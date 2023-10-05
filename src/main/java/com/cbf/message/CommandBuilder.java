package com.cbf.message;

import com.cbf.stream.oms.AcceptOrderEncoder;
import com.cbf.stream.oms.CreateOrderEncoder;
import com.cbf.stream.oms.MessageHeaderEncoder;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class CommandBuilder {
    private final UnsafeBuffer sendBuffer = new UnsafeBuffer(ByteBuffer.allocate(1500));
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final CreateOrderEncoder createOrder = new CreateOrderEncoder();
    private final AcceptOrderEncoder acceptOrder = new AcceptOrderEncoder();

    public CreateOrderEncoder createOrder() {
        return createOrder.wrapAndApplyHeader(new UnsafeBuffer(ByteBuffer.allocate(1500)), 0, headerEncoder);
    }

    public AcceptOrderEncoder acceptOrder() {
        return acceptOrder.wrapAndApplyHeader(new UnsafeBuffer(ByteBuffer.allocate(1500)), 0, headerEncoder);
    }
}
