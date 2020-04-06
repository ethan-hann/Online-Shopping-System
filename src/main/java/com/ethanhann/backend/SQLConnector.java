package com.ethanhann.backend;

import com.ethanhann.users.Account;
import com.ethanhann.users.Address;
import com.ethanhann.users.Customer;
import com.ethanhann.validation.ValidationResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.ToIntFunction;

import static com.ethanhann.validation.CustomerRegistrationValidation.*;

/**
 * Singleton to ensure that only one database connection and one customer can be active
 * This class handles all connections and parsing from and to the database
 */
public class SQLConnector
{
    private static Connection connection = null; //The database connection
    private static Customer customer = null; //The current customer

    /**
     * Public method to get the database connection
     * @return connection to the SQLite database
     */
    public static Connection getConnection()
    {
        if (connection == null) {
            connection = connect();
        }
        return connection;
    }

    /**
     * Gets the current customer.
     * @return the current customer; {@code null}, if the customer has not been set.
     */
    public static Customer getCurrentCustomer()
    {
        if (customer != null) {
            return customer;
        }
        return null;
    }

    /**
     * Determines if a user has entered correct information that is present in the database and
     * the user's account is not closed.
     * @param userName the user's username (id)
     * @param password the user's password
     * @return the Customer object associated with the username and password; null otherwise
     */
    public static ValidationResult canLogin(String userName, String password)
    {
        PreparedStatement ps;
        ResultSet rs;
        String sql = "SELECT * FROM users " +
                "WHERE (login_id = ? AND password = ?);";

        try
        {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, userName);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next())
            {
                String sql2 = "SELECT * FROM customers " +
                        "WHERE (id = ?);";
                String sql3 = "SELECT * FROM accounts " +
                        "WHERE (id = ?);";
                PreparedStatement ps2 = getConnection().prepareStatement(sql2);
                PreparedStatement ps3 = getConnection().prepareStatement(sql3);

                ps2.setString(1, rs.getString("login_id"));
                ps3.setString(1, rs.getString("login_id"));

                ResultSet rs2 = ps2.executeQuery();
                ResultSet rs3 = ps3.executeQuery();

                if (rs2.next())
                {
                    if (rs3.next())
                    {
                        boolean isClosed = rs3.getBoolean("isClosed");
                        if (!isClosed)
                        {
                            setCurrentCustomer(new Customer(rs2.getString("id"),
                                    Address.of(rs2.getString("address")),
                                    Integer.toString(rs2.getInt("phone")),
                                    rs2.getString("email")));
                            return ValidationResult.SUCCESS;
                        }
                        else
                        {
                            return ValidationResult.ACCOUNT_CLOSED;
                        }
                    }
                }

                ps2.close();
                ps3.close();
                rs2.close();
                rs3.close();
            }

            ps.close();
            rs.close();

            return ValidationResult.UNKNOWN_ERROR;
        } catch (SQLException e)
        {
            e.printStackTrace();
            return ValidationResult.UNKNOWN_ERROR;
        }
    }

    /**
     * Connects to the database and checks if a user exists. Adds them to the database if they do not. The user
     *  is only added if the validation checks return SUCCESS. Otherwise, the ValidationResult is returned
     *  and an error message is displayed from the calling method.
     * @param userName the user's username
     * @param password the user's password
     * @param address the user's address
     * @param phoneNumber the user's phone number
     * @param email the user's email
     * @return ValidationResult representing the result of validating the user against various criteria
     */
    public static ValidationResult addUser(String userName, String password, String address, String phoneNumber, String email)
    {
        PreparedStatement ps;
        ResultSet rs;
        int count = 0;

        try
        {
            String sql = "SELECT * FROM users WHERE login_id = ?;";
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, userName);
            rs = ps.executeQuery();

            while (rs.next()) {
                count++;
                if (count != 0) {
                    return ValidationResult.USER_EXISTS;
                }
            }

            Customer c = new Customer(userName, Address.of(address), phoneNumber, email);
            ValidationResult result = isAddressValid().and(isEmailValid()).and(isPhoneValid()).apply(c);

            if (result == ValidationResult.SUCCESS)
            {
                sql = "INSERT INTO users " +
                        "(login_id, password, userState) " +
                        "VALUES(?, ?, 'New');";

                String sql2 = "INSERT INTO customers " +
                        "(id, address, phone, email) " +
                        "VALUES(?, ?, ?, ?);";

                String sql3 = "INSERT INTO accounts " +
                        "(id, billing_address, isClosed, openDate, closeDate) " +
                        "VALUES(?, ?, ?, ?, ?);";

                String sql4 = "INSERT INTO shopping_lists " +
                        "(user_id, items) VALUES(?, ?);";

                ps = getConnection().prepareStatement(sql);
                ps.setString(1, userName);
                ps.setString(2, password);
                ps.executeUpdate();

                ps = getConnection().prepareStatement(sql2);
                ps.setString(1, userName);
                ps.setString(2, address);
                ps.setString(3, phoneNumber);
                ps.setString(4, email);
                ps.executeUpdate();

                ps = getConnection().prepareStatement(sql3);
                ps.setString(1, userName);
                ps.setString(2, address);
                ps.setBoolean(3, false);
                ps.setDate(4, Date.valueOf(LocalDate.now()));
                ps.setDate(5, null);
                ps.executeUpdate();

                ps = getConnection().prepareStatement(sql4);
                ps.setString(1, userName);
                ps.setString(2, "NONE");
                ps.executeUpdate();
            }

            ps.close();
            rs.close();
            return result;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ValidationResult.UNKNOWN_ERROR;
    }

    /**
     * Connects to the database and updates the user's email and billing address
     * @param newEmail the new email to insert into the database
     * @param newBillingAddress the new billing address to insert into the database
     */
    public static boolean updateAccount(String newEmail, String newBillingAddress)
    {
        PreparedStatement ps;
        PreparedStatement ps2;

        try
        {
            Customer c = getCurrentCustomer();

            String sql = "UPDATE accounts " +
                    "SET billing_address = ? " +
                    "WHERE id = ?;";

            String sql2 = "UPDATE customers " +
                    "SET email = ? " +
                    "WHERE id = ?;";

            ps = SQLConnector.getConnection().prepareStatement(sql);
            ps2 = SQLConnector.getConnection().prepareStatement(sql2);

            if (c != null)
            {
                ps.setString(1, newBillingAddress);
                ps.setString(2, c.getUserID());
                ps2.setString(1, newEmail);
                ps2.setString(2, c.getUserID());

                ps.executeUpdate();
                ps2.executeUpdate();
                SQLConnector.getConnection().commit();
                ps.close();
                ps2.close();
                return true;
            }

            return false;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Connects to the database and updates a user's password.
     * @param newPassword the new password
     */
    public static void updatePassword(String newPassword)
    {
        PreparedStatement ps;

        try
        {
            String sql = "UPDATE users " +
                    "SET password = ? " +
                    "WHERE login_id = ?;";

            ps = SQLConnector.getConnection().prepareStatement(sql);
            Customer c = getCurrentCustomer();

            if (c != null)
            {
                ps.setString(1, newPassword);
                ps.setString(2, c.getUserID());
            }

            ps.executeUpdate();
            SQLConnector.getConnection().commit();

            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Connects to the database and checks if the old password is equal to what is present
     * in the database
     * @param oldPassword the password to check against the database
     * @return true if passwords are equal, false if not
     */
    public static boolean checkPassword(String oldPassword)
    {
        PreparedStatement ps;
        ResultSet rs;
        String sql = "SELECT * FROM USERS " +
                "WHERE login_id == ?;";

        try
        {
            Customer c = getCurrentCustomer();
            if (c != null)
            {
                ps = getConnection().prepareStatement(sql);
                ps.setString(1, getCurrentCustomer().getUserID());
                rs = ps.executeQuery();
                if (rs.next())
                {
                    String dbPassword = rs.getString("password");
                    return oldPassword.equals(dbPassword);
                }
            }
            return false;

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Allows parsing of a user Account from a specified id. This method interfaces with
     * the database to return an Account who's id matches the argument.
     * @param id the user's id (i.e. username) in the database
     * @return a new Account matching the id; null if no Account was found.
     */
    public static Account<? super java.util.Date> parseAccount(String id)
    {
        PreparedStatement ps;
        ResultSet rs;
        String sql = "SELECT * FROM accounts " +
                "WHERE id == ?;";
        try
        {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Account<>(id, Address.of(rs.getString("billing_address")),
                        rs.getBoolean("isClosed"),
                        rs.getDate("openDate"), rs.getDate("closeDate"));
            }

            ps.close();
            rs.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Closes the current customer's account.
     */
    public static void closeAccount()
    {
        PreparedStatement ps;

        try
        {
            SQLConnector.getConnection().setAutoCommit(false);
            String sql = "UPDATE accounts " +
                    "SET isClosed = ?, closeDate = ? " +
                    "WHERE id = ?;";

            ps = SQLConnector.getConnection().prepareStatement(sql);
            ps.setBoolean(1, true);
            ps.setDate(2, Date.valueOf(LocalDate.now()));

            if (getCurrentCustomer() != null)
            {
                ps.setString(3, getCurrentCustomer().getUserID());
            }
            else
            {
                ps.setString(3, "");
            }

            ps.executeUpdate();
            SQLConnector.getConnection().commit();

            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Creates and inserts a new order based on the current customer and the shopping cart
     * @param sc the shopping cart to add to this order
     * @return true if order was created successfully; false if not.
     */
    public static boolean createOrder(ShoppingCart sc)
    {
        PreparedStatement ps;

        try
        {
            String sql = "INSERT INTO orders (ordered_date, shipped_date, ship_to, status, total, items) " +
                    "VALUES (?, ?, ?, ?, ?, ?);";

            Customer c = getCurrentCustomer();
            if (c != null)
            {
                LocalDate ordered_date = LocalDate.now();
                LocalDate shipped_date = ordered_date.plusDays(3);
                String ship_to = c.getUserID();
                OrderStatus status = OrderStatus.NEW;
                double total = sc.getTotal();

                ps = getConnection().prepareStatement(sql);
                ps.setDate(1, Date.valueOf(ordered_date));
                ps.setDate(2, Date.valueOf(shipped_date));
                ps.setString(3, ship_to);
                ps.setObject(4, status);
                ps.setDouble(5, total);
                ps.setString(6, sc.toString());

                ps.executeUpdate();

                ps.close();

                return true;
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates the specified order with the new order status.
     * @param id the id of the order to update
     * @param newStatus the new {@code OrderStatus}
     */
    private static void updateOrder(int id, OrderStatus newStatus)
    {
        PreparedStatement ps;

        try
        {
            SQLConnector.getConnection().setAutoCommit(false);
            String sql = "UPDATE orders " +
                    "SET status = ? " +
                    "WHERE number = ?;";

            ps = SQLConnector.getConnection().prepareStatement(sql);
            ps.setString(1, newStatus.name());
            ps.setInt(2, id);

            ps.executeUpdate();
            SQLConnector.getConnection().commit();

            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Removes from the database the specified order.
     * @param orderToCancel the order to cancel (remove)
     */
    public static void cancelOrder(Order<Date> orderToCancel)
    {
        PreparedStatement ps;

        try
        {
            String sql = "DELETE FROM orders " +
                    "WHERE number = ?;";
            ps = getConnection().prepareStatement(sql);
            int orderNumber = orderToCancel.getId();

            ps.setInt(1, orderNumber);
            ps.executeUpdate();

            ps.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Checks against today's date to determine the order's status
     * @param ordered the date ordered
     * @param shipped the estimated shipped date
     * @return new OrderStatus representing the order's current status
     */
    public static OrderStatus checkForStatusUpdate(Date ordered, Date shipped)
    {
        LocalDate oneDayLater = ordered.toLocalDate().plusDays(1);
        LocalDate oneDaysAfterShip = shipped.toLocalDate().plusDays(1);
        LocalDate twoDaysAfterShip = shipped.toLocalDate().plusDays(2);
        LocalDate fourDaysAfterShip = shipped.toLocalDate().plusDays(4);
        LocalDate today = LocalDate.now();

        if (today.isEqual(ordered.toLocalDate()))
        {
            return OrderStatus.NEW;
        }
        else if (today.isEqual(oneDayLater))
        {
            return OrderStatus.HOLD;
        }
        else if (today.isEqual(shipped.toLocalDate()))
        {
            return OrderStatus.SHIPPED;
        }
        else if (today.isEqual(oneDaysAfterShip))
        {
            return OrderStatus.SHIPPED;
        }
        else if (today.isEqual(twoDaysAfterShip))
        {
            return OrderStatus.DELIVERED;
        }
        else if (today.isEqual(fourDaysAfterShip) || today.isAfter(fourDaysAfterShip))
        {
            return OrderStatus.CLOSED;
        }
        else
        {
            return OrderStatus.CLOSED;
        }
    }

    /**
     * Connects and retrieves a list of products from the database
     * @return ObservableList<Product> a list of products that can be observed and dynamically changed.
     */
    public static ObservableList<Product> getProducts()
    {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT * FROM products;";
        try {
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next())
            {
                products.add(new Product(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        parseSupplier(rs.getInt("supplier_id")), 0));
            }

            stmt.close();
            rs.close();

            return products;
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets a list of orders based on the ship_to_id from the database.
     * The ship_to_id corresponds to the customer's id.
     * @param ship_to_id the customer whom this order belongs to
     * @return an ArrayList of Orders
     */
    public static ObservableList<Order<Date>> getOrders(String ship_to_id)
    {
        ObservableList<Order<Date>> orders = FXCollections.observableArrayList();
        PreparedStatement ps;
        ResultSet rs;

        try
        {
            String sql = "SELECT * FROM orders " +
                    "WHERE ship_to = ?;";
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, ship_to_id);
            rs = ps.executeQuery();

            while (rs.next())
            {
                int id = rs.getInt("number");
                Date ordered_date = rs.getDate("ordered_date");
                Date shipped_date = rs.getDate("shipped_date");
                String ship_to = rs.getString("ship_to");

                OrderStatus newStatus = checkForStatusUpdate(ordered_date, shipped_date);
                updateOrder(id, newStatus);

                OrderStatus status = OrderStatus.valueOf(rs.getString("status"));
                double total = rs.getDouble("total");
                ObservableList<Product> items = parseShoppingList(rs.getString("items"));

                orders.add(new Order<>(id, ordered_date, shipped_date, ship_to, status, total, items));
            }

            ps.close();
            rs.close();

            return orders;

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * Connects and retrieves a ShoppingCart from the database; the shopping cart retrieved is
     * dependent upon the current customer.
     * @return a ShoppingCart
     */
    public static ShoppingCart getShoppingCart()
    {
        String sql = "SELECT items FROM shopping_lists " +
                "WHERE user_id = ?";
        PreparedStatement ps;
        ResultSet rs;

        try
        {
            ps = SQLConnector.getConnection().prepareStatement(sql);

            if (getCurrentCustomer() != null) {
                ps.setString(1, getCurrentCustomer().getUserID());
            }
            else
            {
                ps.setString(1, "");
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                String itemString = rs.getString("items");
                if (itemString.equals("NONE") || itemString.equals(""))
                {
                    return new ShoppingCart();
                }
                else
                {
                    return new ShoppingCart(parseShoppingList(rs.getString("items")));
                }
            }

            ps.close();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return new ShoppingCart();
        }
        return new ShoppingCart();
    }

    /**
     * Connects to the database and updates the shopping cart for the current customer with the new value
     * @param sc the new shopping cart
     */
    public static void updateShoppingCart(ShoppingCart sc)
    {
        PreparedStatement ps;

        try
        {
            SQLConnector.getConnection().setAutoCommit(false);
            String sql = "UPDATE shopping_lists " +
                    "SET items = ? " +
                    "WHERE user_id = ?;";

            ps = SQLConnector.getConnection().prepareStatement(sql);
            ps.setString(1, sc.toString());

            if (getCurrentCustomer() != null)
            {
                ps.setString(2, getCurrentCustomer().getUserID());
            }
            else
            {
                ps.setString(2, "");
            }

            ps.executeUpdate();
            SQLConnector.getConnection().commit();

            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Connects to the database and updates the shopping cart for the current customer with the new value
     * @param items string representation of the shopping cart
     */
    public static void updateShoppingCart(String items)
    {
        PreparedStatement ps;

        try
        {
            SQLConnector.getConnection().setAutoCommit(false);
            String sql = "UPDATE shopping_lists " +
                    "SET items = ? " +
                    "WHERE user_id = ?;";

            ps = SQLConnector.getConnection().prepareStatement(sql);
            ps.setString(1, items);

            if (getCurrentCustomer() != null)
            {
                ps.setString(2, getCurrentCustomer().getUserID());
            }
            else
            {
                ps.setString(2, "");
            }

            ps.executeUpdate();
            SQLConnector.getConnection().commit();

            ps.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Sets the current customer object.
     * @param c the customer to make current
     */
    private static void setCurrentCustomer(Customer c)
    {
        if (c != null)
        {
            customer = c;
        }
    }

    /**
     * Allows parsing of a Product from a specified product id. This method interfaces with
     * the database to return a Product who's id matches the argument.
     * @param id the product id in the database
     * @param quantity the quantity of the product
     * @return a new Product matching the id; null if no Product was found.
     */
    private static Product parseProduct(int id, int quantity)
    {
        PreparedStatement ps;
        ResultSet rs;
        String sql = "SELECT * FROM products " +
                "WHERE id = ?;";
        try
        {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Product(id,
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        parseSupplier(rs.getInt("supplier_id")),
                        quantity);
            }

            ps.close();
            rs.close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Allows parsing of a Supplier from a specified supplier_id. This method interfaces with
     * the database to return a Supplier who's id matches the argument.
     * @param id the supplier id in the database
     * @return a new Supplier matching the id; null if no Supplier was found.
     */
    private static Supplier parseSupplier(int id)
    {
        PreparedStatement ps;
        ResultSet rs;
        String sql = "SELECT * FROM suppliers " +
                "WHERE id = ?;";
        try
        {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Supplier(rs.getInt("id"), rs.getString("name"));
            }

            ps.close();
            rs.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Parse a string of items into an ObservableArrayList of Products
     * @param items a string representing the users shopping cart
     * @return ObservableArrayList
     */
    private static ObservableList<Product> parseShoppingList(String items)
    {
        ObservableList<Product> list = FXCollections.observableArrayList();
        ToIntFunction<String> stringToInt = Integer::parseInt;

        //Split the input string on ; character and map each string to an integer (parseInt)
        int[] tokens = Arrays.stream(items.split(";")).mapToInt(stringToInt).toArray();

        HashMap<Integer, Integer> quantities = findQuantities(tokens);

        for (Integer i : quantities.keySet())
        {
            list.add(parseProduct(i, quantities.get(i)));
        }

        return list;
    }

    /**
     * Counts the number of occurrences of each product id in the shopping cart
     * @param tokens the string tokens
     * @return HashMap containing (ProductId, Quantity) pairs
     */
    private static HashMap<Integer, Integer> findQuantities(int[] tokens)
    {
        HashMap<Integer, Integer> quantityMap = new HashMap<>();

        for (int i : tokens)
        {
            if (!quantityMap.containsKey(i))
            {
                quantityMap.put(i, 1);
            }
            else
            {
                quantityMap.put(i, quantityMap.get(i) + 1);
            }
        }
        return quantityMap;
    }

    /**
     * Connects to an embedded SQLite database which holds various information
     * such as users, products, suppliers, etc.... The database is stored in the
     * resources folder in the Classpath.
     * This is a private method that can only be called from this Singleton class.
     * @return Connection a SQLConnection port to interface with the database
     */
    private static Connection connect()
    {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = DriverManager.getConnection("jdbc:sqlite:src/main/resources/oss.sqlite");
            c.setAutoCommit(false);
            return c;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
