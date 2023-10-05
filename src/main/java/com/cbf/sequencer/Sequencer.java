package com.cbf.sequencer;

import com.cbf.base.BaseApp;
import com.cbf.base.Transport;
import com.cbf.base.TransportAddress;
import com.cbf.message.CommandDecoder;
import com.cbf.stream.oms.AcceptOrderDecoder;
import com.cbf.stream.oms.CreateNewOrderSingleDecoder;
import com.cbf.stream.oms.MessageHeaderDecoder;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class Sequencer extends BaseApp<Sequencer> {
    private final CommandDecoder commandDecoder = new CommandDecoder();
    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final CreateNewOrderSingleDecoder createNewOrderSingleDecoder = new CreateNewOrderSingleDecoder();
    private final AcceptOrderDecoder acceptOrderDecoder = new AcceptOrderDecoder();

    public Sequencer() {
        super(Sequencer.class.getSimpleName(),
              new Transport(Sequencer.class.getSimpleName(), TransportAddress.commandStreamAddress()),
              new Transport(Sequencer.class.getSimpleName(), TransportAddress.eventStreamAddress())
        );
    }

    protected void onMessage(DirectBuffer buffer, int offset, int length, Header header) {
        headerDecoder.wrap(buffer, offset);
        int messageId = headerDecoder.templateId();
        System.out.printf("[%s][%s]messageId=%s", Thread.currentThread().getName(), instanceName, messageId);
        switch (messageId) {
            case 1:
                createNewOrderSingleDecoder.wrapAndApplyHeader(buffer, offset, headerDecoder);
                System.out.printf("[%s][%s]cmd=%s", Thread.currentThread().getName(), instanceName, createNewOrderSingleDecoder);
                return;
            case 3:
                acceptOrderDecoder.wrapAndApplyHeader(buffer, offset, headerDecoder);
                System.out.printf("[%s][%s]cmd=%s", Thread.currentThread().getName(), instanceName, acceptOrderDecoder);
                return;
        }
    }
}
