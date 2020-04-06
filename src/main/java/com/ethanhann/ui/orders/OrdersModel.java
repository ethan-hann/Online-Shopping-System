package com.ethanhann.ui.orders;

import com.ethanhann.backend.Order;
import com.ethanhann.backend.OrderStatus;
import com.ethanhann.backend.SQLConnector;
import com.ethanhann.users.Customer;
import javafx.collections.ObservableList;

import java.sql.Date;

/**
 * Interfaces with the SQLConnector class to perform database operations on a user's Orders.
 */
class OrdersModel
{
    Customer getCurrentCustomer()
    {
        return SQLConnector.getCurrentCustomer();
    }

    ObservableList<Order<Date>> getOrders(String userId)
    {
        return SQLConnector.getOrders(userId);
    }

    OrderStatus checkForStatusUpdate(Date orderedDate, Date shipped_date)
    {
        return SQLConnector.checkForStatusUpdate(orderedDate, shipped_date);
    }

    void cancelOrder(Order<Date> order)
    {
        SQLConnector.cancelOrder(order);
    }
}
