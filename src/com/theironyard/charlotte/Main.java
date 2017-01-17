

package com.theironyard.charlotte;

import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:./main");
    }

    private static void initializeDatabase() throws SQLException {
        Statement stmt = getConnection().createStatement();
        stmt.execute("create table if not exists users  (id identity, name varchar, email varchar)");
        stmt.execute("create table if not exists orders (id identity, user_id int)");
        stmt.execute("create table if not exists items  (id identity, name varchar, quantity int, price double, order_id int)");
    }

    private static List<Order> getOrdersForUser(Integer userId) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement("select * from orders where user_id = ?");

        // so on and so forth.
        return new ArrayList<>();
    }

    private static User getUserById(Integer id) throws SQLException {
        User user = null;

        if (id != null) {
            PreparedStatement stmt = getConnection().prepareStatement("select * from users where id = ?");

            stmt.setInt(1, id);
            ResultSet results = stmt.executeQuery();

            if (results.next()) {
                user = new User(id, results.getString("name"), results.getString("email"));

                user.setOrders(getOrdersForUser(id));
            }
        }

        return user;
    }

    private static Integer getUserIdByEmail(String email) throws SQLException {
        Integer userId = null;

        if (email != null) {
            PreparedStatement stmt = getConnection().prepareStatement("select * from users where email = ?");
            stmt.setString(1, email);

            ResultSet results = stmt.executeQuery();

            if (results.next()) {
                userId = results.getInt("id");
            }
        }

        return userId;
    }

    public static void main(String[] args) throws SQLException {

        Server.createWebServer().start();
        HashMap model = new HashMap();

        Spark.get("/", (request, response) -> {
            //HashMap model = new HashMap();
            Session session = request.session();

            User current = getUserById(session.attribute("user"));

            if (current != null) {
                // pass user into model
                model.put("user", current);

                return new ModelAndView(model, "home.html");
            } else {
                return new ModelAndView(model, "login.html");

            }
        }, new MustacheTemplateEngine());

        Spark.post("/login", (request, response) -> {

            String email = request.queryParams("email");
            String name = request.queryParams("name");

            // look up the user by email address
            Integer userId = getUserIdByEmail(email);

            // if the user exists, save the id in session.
            if (userId != null) {
                Session session = request.session();
                session.attribute("user", userId);
            } else {
                User.insertUser(getConnection(), name, email);
            }
            response.redirect("/");
            return "";
        });

       /* Spark.get("/login",
                (request, response) -> {
            User.insertUser(getConnection(),request.queryParams("name"),request.queryParams("email"));
            return new ModelAndView(model,"")
                });*/

        initializeDatabase();

        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );

        Spark.get("/registration",
                (request, response) -> {
                    HashMap m = new HashMap();
                    String email = request.queryParams("email");
                    String name = request.queryParams("name");
                    User.insertUser(getConnection(), name, email);

                    return new ModelAndView(m, "registration.html");

                }, new MustacheTemplateEngine());

    }

}



