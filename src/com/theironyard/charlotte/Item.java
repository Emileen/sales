package com.theironyard.charlotte;

/**
 * Created by emileenmarianayagam on 1/16/17.
 */
public class Item {
    private int id;
    private String name;
    private int quantity;
    private double price;
    private int orderId;

    public Item() {
    }

    public Item(int id, String name, int quantity, double price, int orderId) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.orderId = orderId;
    }

    public Item(String name, int quantity, double price, int orderId) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.orderId = orderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
