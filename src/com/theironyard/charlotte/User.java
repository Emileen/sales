package com.theironyard.charlotte;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by emileenmarianayagam on 1/16/17.
 */
public class User {
    private int id;
    private String name;
    private String email;
    private List<Order> orders;

    public User() {
    }

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public static void insertUser(Connection conn, String name, String email) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement ("insert into users values (NULL, ?, ?)");
        stmt.setString(1,name);
        stmt.setString(2,email);
        stmt.execute();
    }

}
