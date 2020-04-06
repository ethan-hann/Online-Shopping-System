package com.ethanhann.ui.home;

import com.ethanhann.backend.Product;
import com.ethanhann.backend.SQLConnector;
import javafx.collections.ObservableList;

/**
 * Interfaces with the SQLConnector class to perform database operations.
 */
class HomeModel
{
    /**
     * Retrieves an ObservableList of Products from the database.
     * @return ObservableList<Product> a list of products that can be observed and dynamically changed.
     */
    ObservableList<Product> getProducts()
    {
        return SQLConnector.getProducts();
    }
}
