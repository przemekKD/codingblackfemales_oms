package com.cbf.gateway;

import com.cbf.base.BaseApp;
import com.cbf.message.EventDispatcher;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;

public class Gateway extends BaseApp<Gateway> {

    public Gateway() {
        super(Gateway.class.getSimpleName());
    }

    @Override
    protected void init(EventDispatcher eventDispatcher) {

    }
}
