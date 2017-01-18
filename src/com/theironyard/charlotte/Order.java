package com.theironyard.charlotte;

import java.util.List;

/**
 * Created by emileenmarianayagam on 1/16/17.
 */
public class Order {
    private int id;
    private int userId;
    private boolean complete;
    private List<Item> items;

    public Order() {
    }

    public Order(int id, int userId, boolean complete) {
        this.id = id;
        this.userId = userId;
        this.complete = complete;
    }

    public Order(int userId, boolean complete) {
        this.userId = userId;
        this.complete = complete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }


}
