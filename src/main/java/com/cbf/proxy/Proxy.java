package com.cbf.proxy;

import com.cbf.base.BaseApp;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class Proxy extends BaseApp<Proxy> {

    public Proxy() {
        super(Proxy.class.getSimpleName());
    }

    protected void onMessage(DirectBuffer buffer, int offset, int length, Header header) {
        if("FIX:NewOrderSingle".equals(buffer.getStringAscii(offset))){
            send("FIX:ack");
        }
    }
}
