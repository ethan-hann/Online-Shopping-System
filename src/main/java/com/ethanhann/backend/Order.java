package com.ethanhann.backend;

import javafx.collections.ObservableList;

import java.util.Date;

/**
 * This class represents a single Order; Orders are stored in the SQLite database
 *  and adhere to the properties in this class:
 * <ul>
 *     <li>id</li>
 *     <li>ordered_date</li>
 *     <li>shipped_date</li>
 *     <li>ship_to</li>
 *     <li>status</li>
 *     <li>total</li>
 * </ul>
 *  This class implements the use of Generics so that the Dates can be in any format
 *  as long as the DateType extends Date.
 */
public class Order<T extends Date>
{
    private final int id;
    private T orderedDate;
    private T shippedDate;
    private String ship_to;
    private OrderStatus status;
    private double total;
    private final ObservableList<Product> items;

    public Order(int id, T orderedDate, T shippedDate, String ship_to, OrderStatus status, double total, final ObservableList<Product> items)
    {
        this.id = id;
        this.orderedDate = orderedDate;
        this.shippedDate = shippedDate;
        this.ship_to = ship_to;
        this.status = status;
        this.total = total;
        this.items = items;
    }

    public int getId()
    {
        return id;
    }

    public T getOrderedDate()
    {
        return orderedDate;
    }

    public T getShippedDate()
    {
        return shippedDate;
    }

    public double getTotal()
    {
        return total;
    }

    public final ObservableList<Product> getItems()
    {
        return items;
    }

    @Override
    public String toString()
    {
        return "Order{" +
                "id=" + id +
                ", orderedDate=" + orderedDate +
                ", shippedDate=" + shippedDate +
                ", ship_to='" + ship_to + '\'' +
                ", status=" + status +
                ", total=" + total +
                ", items='" + items + '\'' +
                '}';
    }
}
