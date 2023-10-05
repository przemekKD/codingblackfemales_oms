package com.cbf.message;

import com.cbf.stream.oms.AcceptOrderEncoder;
import com.cbf.stream.oms.CreateNewOrderSingleEncoder;
import com.cbf.stream.oms.MessageHeaderEncoder;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class CommandBuilder {
    private final UnsafeBuffer sendBuffer = new UnsafeBuffer(ByteBuffer.allocate(1500));
    private final MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
    private final CreateNewOrderSingleEncoder createNewOrderSingle = new CreateNewOrderSingleEncoder();
    private final AcceptOrderEncoder acceptOrder = new AcceptOrderEncoder();

    public CreateNewOrderSingleEncoder createNewOrderSingle(){
        return createNewOrderSingle.wrapAndApplyHeader(sendBuffer, 0, headerEncoder);
    }

    public AcceptOrderEncoder acceptOrder(){
        return acceptOrder.wrapAndApplyHeader(sendBuffer, 0, headerEncoder);
    }
}
