package com.ethanhann.ui.shopping_cart;

import com.ethanhann.backend.SQLConnector;
import com.ethanhann.backend.ShoppingCart;

/**
 * Interfaces with the SQLConnector class to perform database operations on a user's Shopping Cart.
 */
public class ShoppingCartModel
{
    /**
     * Retrieves a ShoppingCart from the database; the shopping cart retrieved is
     * dependent upon the current customer.
     * @return a ShoppingCart
     */
    public ShoppingCart getShoppingCart()
    {
        return SQLConnector.getShoppingCart();
    }

    /**
     * Updates the shopping cart for the current customer with the new value
     * @param sc the new shopping cart
     */
    public void updateShoppingCart(ShoppingCart sc)
    {
        SQLConnector.updateShoppingCart(sc);
    }

    /**
     * Update the shopping cart for the current customer with the new value
     * @param items a string representation of the shopping cart
     */
    public void updateShoppingCart(String items)
    {
        SQLConnector.updateShoppingCart(items);
    }

    /**
     * Creates a order using the current shopping cart
     * @return true if order was created successfully; false if not
     */
    public boolean createOrder()
    {
        return SQLConnector.createOrder(getShoppingCart());
    }
}
