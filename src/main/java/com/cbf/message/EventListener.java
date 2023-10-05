package com.cbf.message;

public interface EventListener<E> {
    void onEvent(E event);
}
