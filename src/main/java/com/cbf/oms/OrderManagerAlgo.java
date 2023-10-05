package com.cbf.oms;

import com.cbf.base.BaseApp;
import com.cbf.sequencer.Sequencer;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class OrderManagerAlgo extends BaseApp<OrderManagerAlgo> {
    public OrderManagerAlgo() {
        super(Sequencer.class.getSimpleName());
    }

    protected void onMessage(DirectBuffer buffer, int offset, int length, Header header) {
    }
}
