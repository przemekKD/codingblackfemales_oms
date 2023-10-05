package com.cbf.message;

import com.cbf.base.MessageListener;
import com.cbf.stream.oms.MessageHeaderDecoder;
import com.cbf.stream.oms.OrderAcceptedDecoder;
import com.cbf.stream.oms.OrderPendingDecoder;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.agrona.collections.Int2ObjectHashMap;

public class EventDispatcher {
    private final String instanceName;
    private final MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
    private final OrderPendingDecoder orderPending = new OrderPendingDecoder();
    private final OrderAcceptedDecoder orderAccepted = new OrderAcceptedDecoder();
    private final Int2ObjectHashMap<EventListener<?>> messageIdToSubscriber = new Int2ObjectHashMap<>();
    private final MessageListener messageHandler = this::onEventStreamMessage;
    private EventListener allEventListener;

    public EventDispatcher(String instanceName) {
        this.instanceName = instanceName;
    }

    public <T> void subscribe(int messageId, EventListener<T> eventListener) {
        messageIdToSubscriber.put(messageId, eventListener);
    }

    public void subscribeAll(EventListener<?> allEventListener) {
        this.allEventListener = allEventListener;
    }

    public MessageListener getMessageHandler() {
        return messageHandler;
    }

    protected void onEventStreamMessage(String channel, int streamId, DirectBuffer buffer, int offset, int length, Header header) {
        headerDecoder.wrap(buffer, offset);
        final int messageId = headerDecoder.templateId();
        switch (messageId) {
            case OrderPendingDecoder.TEMPLATE_ID:
                orderPending.wrapAndApplyHeader(buffer, offset, headerDecoder);
                dispatch(channel, streamId, messageId, orderPending);
                return;
            case OrderAcceptedDecoder.TEMPLATE_ID:
                orderAccepted.wrapAndApplyHeader(buffer, offset, headerDecoder);
                dispatch(channel, streamId, messageId, orderAccepted);
                return;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> void dispatch(String channel, int streamId, int messageId, T event) {
        EventListener<T> eventListener = (EventListener) messageIdToSubscriber.get(messageId);
        if (eventListener != null) {
            System.out.printf("[%s][%s][%s/%s] received event=%s%n", Thread.currentThread().getName(), channel, streamId, instanceName, event);
            eventListener.onEvent(event);
        }

        if (allEventListener != null) {
            allEventListener.onEvent(event);
        }
    }
}
