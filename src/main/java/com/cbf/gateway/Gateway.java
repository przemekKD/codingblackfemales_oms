package com.cbf.gateway;

import com.cbf.base.BaseApp;
import com.cbf.base.Transport;
import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import io.aeron.driver.MediaDriver;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class Gateway extends BaseApp<Gateway> {

    public Gateway() {
        super(Gateway.class.getSimpleName());
    }

    protected void onMessage(DirectBuffer buffer, int offset, int length, Header header) {

    }
}
