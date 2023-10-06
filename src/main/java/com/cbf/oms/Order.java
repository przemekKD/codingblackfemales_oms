package com.cbf.oms;

import com.cbf.stream.oms.Side;

public class Order {
    private long id;
    private OrderStatus orderStatus;
    private String ticker;
    private Side side;
    private long quantity;
    private long price;

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public long getId() {
        return id;
    }

    public Order setId(long id) {
        this.id = id;
        return this;
    }

    public String getTicker() {
        return ticker;
    }

    public Order setTicker(String ticker) {
        this.ticker = ticker;
        return this;
    }

    public Side getSide() {
        return side;
    }

    public Order setSide(Side side) {
        this.side = side;
        return this;
    }

    public long getQuantity() {
        return quantity;
    }

    public Order setQuantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    public long getPrice() {
        return price;
    }

    public Order setPrice(long price) {
        this.price = price;
        return this;
    }
}
