package com.cbf.gateway;

import com.cbf.base.BaseApp;
import com.cbf.message.EventDispatcher;

public class Gateway extends BaseApp<Gateway> {

    public Gateway() {
        super(Gateway.class.getSimpleName());
    }

    @Override
    protected void init(EventDispatcher eventDispatcher) {

    }
}
