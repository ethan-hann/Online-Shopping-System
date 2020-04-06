package com.ethanhann.backend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ShoppingCart
{
    private ObservableList<Product> items;

    ShoppingCart(ObservableList<Product> items)
    {
        this.items = items;
    }

    public ShoppingCart()
    {
        items = FXCollections.observableArrayList();
    }

    /**
     * Add the specified item to the shopping cart.
     * If the item already exists, just adds 1 to the quantity
     * @param p the product to add
     */
    public void addItem(Product p)
    {
        if (items.contains(p))
        {
            int currentQuantity = items.get(items.indexOf(p)).getQuantity();
            items.get(items.indexOf(p)).setQuantity(currentQuantity + 1);
        }
        else
        {
            items.add(p);
        }
    }

    /**
     * Gets the total dollar amount of this shopping cart.
     * @return double - the real sum total
     */
    public double getTotal()
    {
        double total = 0.0;
        for (Product p : items) {
            total += p.getTotal();
        }

        return total;
    }

    /**
     * Get the observable products list which represents this shopping cart.
     * @return ObservableList of Products
     */
    public ObservableList<Product> getItems()
    {
        return items;
    }

    /**
     * Create a string that represents the list items and their quantity.
     * The product_id will be repeated for as many times as the quantity of the product.
     * The final string will look like this:
     * "product_id; product_id; ..." <br>
     * {@code ;} is used as a delimeter and facilitates parsing this string back into a {@code ShoppingCart}
     * @return string representation of this shopping cart
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        for (Product current : items)
        {
            for (int i = 0; i < current.getQuantity(); i++)
            {
                builder.append(current.getId()).append(";");
            }
        }

        return builder.toString();
    }
}
