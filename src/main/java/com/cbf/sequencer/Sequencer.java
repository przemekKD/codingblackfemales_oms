package com.cbf.sequencer;

import com.cbf.base.BaseApp;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class Sequencer extends BaseApp<Sequencer> {
    public Sequencer() {
        super(Sequencer.class.getSimpleName());
    }

    protected void onMessage(DirectBuffer buffer, int offset, int length, Header header) {
    }
}
