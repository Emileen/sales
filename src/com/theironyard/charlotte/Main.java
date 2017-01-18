

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
        stmt.execute("create table if not exists orders (id identity, user_id int, complete boolean)");
        stmt.execute("create table if not exists items  (id identity, name varchar, quantity int, price double, order_id int)");
    }

    private static List<Order> getOrdersForUser(Integer userId) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement("select * from orders where user_id = ?");

        // so on and so forth.
        return new ArrayList<>();
    }
/*    private static List<Item> getOrdersForUser(Connection conn, Integer userId) throws SQLException {
        ArrayList<Item> items = new ArrayList<>();
        PreparedStatement stmt = getConnection().prepareStatement("select * from orders where user_id = ?");
        stmt.setInt(1,userId);
        ResultSet result = stmt.executeQuery();

        while (result.next()){
            String name = result.getString("name");
            int quantity = result.getInt("quantity");
            double price = result.getDouble("price");
            int orderId = result.getInt("orderId");
            items.add(new Item(name,quantity,price,orderId));
        }

        return items;
    }*/

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

    private static Order getLatestCurrentOrder(Integer userId) throws SQLException {
        Order order = null;
        if (userId != null) {
            PreparedStatement stmt = getConnection().prepareStatement("select top 1 * from orders where user_id = ? and complete = false");
            stmt.setInt(1,userId);
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                order = new Order(results.getInt("id"),
                        results.getInt("user_id"),
                        false);
            }
        }
        return order;
    }

    private static int insertOrder(int userId) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement("insert into orders values (NULL, ?,?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, userId);
        stmt.setBoolean(2,false);
        stmt.executeUpdate();
        ResultSet keys =  stmt.getGeneratedKeys();
        keys.next();
        return keys.getInt(1);
    }

    public static void insertItem(Connection conn, Item item) throws SQLException {

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO items VALUES (NULL, ?, ?, ?, ?)");
        stmt.setString(1, item.getName());
        stmt.setInt(2,item.getQuantity());
        stmt.setDouble(3,item.getPrice());
        stmt.setInt(4,item.getId());
        stmt.execute();
    }


    public static void main(String[] args) throws SQLException {
        //checks for users
        Server.createWebServer().start();

        Spark.get("/", (request, response) -> {
            HashMap model = new HashMap();
            Session session = request.session();

            User current = getUserById(session.attribute("user"));

            //if the user is not null then send to home page
            if (current != null) {
                // pass user into model
                model.put("user", current);
                return new ModelAndView(model, "home.html");
            }
            else{

                //if the user is null then send them to the login in page
                return new ModelAndView(model, "login.html");

            }
        }, new MustacheTemplateEngine());

        Spark.post("/login", (request, response) -> {

            String email = request.queryParams("email");
            /*String name = request.queryParams("name");*/

            // look up the user by email address
            Integer userId = getUserIdByEmail(email);

            // if the user exists, save the id in session.
            if (userId != null) {
                Session session = request.session();
                session.attribute("user", userId);
            }
            response.redirect("/");
            return "";
        });

        initializeDatabase();

        //registers the user is there is no user
        Spark.get("/registration",
                (request, response) -> {
                    HashMap m = new HashMap();
                    return new ModelAndView(m, "registration.html");
                }, new MustacheTemplateEngine());

        Spark.post("/registration",
                (request, response) -> {
                    User.insertUser(getConnection(),request.queryParams("name"),
                            request.queryParams("email"));
                    response.redirect("/");
                    return "";

        });

        //trys to enter the order that the person has into the table
        Spark.post("/add-item", (request, response) -> {
            Session session = request.session();

            //see if the user has a valid order
            User currentUser = getUserById(request.session().attribute("user"));

            //if there is a seesion
            if (currentUser != null) {
                // see if there is a current order
                Order currentOrder = getLatestCurrentOrder(currentUser.getId());

                if (currentOrder == null) {
                    // if not, make a new one
                    currentOrder = new Order(currentUser.getId(),true);
                    int orderId = insertOrder(currentUser.getId());


                    // get item from post data
                    Item postedItem = new Item(request.queryParams("name"),
                            Integer.valueOf(request.queryParams("quantity")),
                            Double.valueOf(request.queryParams("price")),
                                    orderId);

                    // add item to order
                    insertItem(getConnection(), postedItem);

                }
            }

            // redirect
            response.redirect("/");
            return "";
        });

        Spark.get("/item",(request, response) -> {
            HashMap m = new HashMap();

            return new ModelAndView(m,"order.html");
        });

        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                })
        );



    }

}



