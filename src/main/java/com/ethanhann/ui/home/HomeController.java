package com.ethanhann.ui.home;

import com.ethanhann.backend.Product;
import com.ethanhann.backend.SQLConnector;
import com.ethanhann.backend.ShoppingCart;
import com.ethanhann.ui.PopUp;
import com.ethanhann.ui.account.AccountView;
import com.ethanhann.ui.orders.OrdersView;
import com.ethanhann.ui.shopping_cart.ShoppingCartModel;
import com.ethanhann.ui.shopping_cart.ShoppingCartView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;

import java.sql.SQLException;
import java.util.Collections;

/**
 * Controls communication between the view (stage) and the database via the {@code HomeModel} class. <br>
 * Also, maintains a reference to the {@code ShoppingCartModel} class to facilitate adding to the ShoppingCart.
 */
public class HomeController
{

    private HomeModel homeModel;
    private ShoppingCartModel shoppingCartModel;

    public MenuItem productsMenuItem;
    public MenuItem sortNameAscending;
    public MenuItem sortNameDescending;
    public MenuItem sortPriceAscending;
    public MenuItem sortPriceDescending;
    public MenuItem shoppingCartMenuItem;
    public MenuItem logOutMenuItem;
    public MenuItem yourOrdersMenuItem;
    public MenuItem yourAccountMenuItem;

    public ListView<Product> productsListView;

    public Label productNameLabel;
    public Label productDescriptionLabel;
    public Label productPriceLabel;
    public Label productSupplierInformation;

    public Button addToShoppingCartButton;
    public ComboBox<Integer> quantityComboBox;

    /**
     * Initialize values and call showProducts() to display products in the ListView.
     */
    @FXML
    public void initialize()
    {
        homeModel = new HomeModel();
        shoppingCartModel = new ShoppingCartModel();

        ObservableList<Integer> choices = FXCollections.observableArrayList();
        int MAX_QUANTITY = 100;
        for (int i = 1; i <= MAX_QUANTITY; i++)
        {
            choices.add(i);
        }

        quantityComboBox.setItems(choices);
        quantityComboBox.getSelectionModel().select(0);

        showProducts();
        productsListView.getSelectionModel().select(0);

        productNameLabel.setTooltip(new Tooltip("Product name"));
        productDescriptionLabel.setTooltip(new Tooltip("Description"));
        productSupplierInformation.setTooltip(new Tooltip("Supplier"));
        productPriceLabel.setTooltip(new Tooltip("Price"));

        quantityComboBox.setTooltip(new Tooltip("Quantity"));
    }

    /**
     * Displays a list of products in the ListView. A listener is set on the
     * ListView items so that the labels can be updated dynamically based on the selected item.
     */
    public void showProducts()
    {
        ObservableList<Product> products = homeModel.getProducts();
        productsListView.setItems(products);

        //Configure how the list items are displayed
        productsListView.setCellFactory(p -> new ListCell<>(){
            @Override
            protected void updateItem(Product item, boolean empty)
            {
                super.updateItem(item, empty);

                if (empty || item == null || item.getName() == null) {
                    setText(null);
                }
                else
                {
                    setText(item.getName());
                    setFont(new Font(14));
                }
            }
        });

        //When a list item is selected, the appropriate information is displayed
        productsListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        productNameLabel.setText(newValue.getName());
                        productDescriptionLabel.setText(newValue.getDescription());
                        productPriceLabel.setText(String.format("$%.2f", newValue.getPrice()));
                        productSupplierInformation.setText(newValue.getSupplier().getName());
                    }
                });

        productsListView.getSelectionModel().select(0);
    }

    /**
     * Sort the products list view by Name (Ascending)
     */
    public void sortByNameAscending()
    {
        productsListView.getItems().sort(Product.NameComparator);
        productsListView.getSelectionModel().select(0);
    }

    /**
     * Sort the products list view by Name (Descending)
     */
    public void sortByNameDescending()
    {
        productsListView.getItems().sort(Collections.reverseOrder(Product.NameComparator));
        productsListView.getSelectionModel().select(0);
    }

    /**
     * Sort the products list view by Price (Ascending)
     */
    public void sortByPriceAscending()
    {
        productsListView.getItems().sort(Product.PriceComparator);
        productsListView.getSelectionModel().select(0);
    }

    /**
     * Sort the products list view by Price (Descending)
     */
    public void sortByPriceDescending()
    {
        productsListView.getItems().sort(Collections.reverseOrder(Product.PriceComparator));
        productsListView.getSelectionModel().select(0);
    }

    /**
     * Adds the selected item to the shopping cart along with its quantity
     */
    public void addToShoppingCart()
    {
        try
        {
            ShoppingCart s = shoppingCartModel.getShoppingCart();

            Product p = (Product) productsListView.getSelectionModel().getSelectedItem().clone();
            p.setQuantity(quantityComboBox.getValue());
            s.addItem(p);

            shoppingCartModel.updateShoppingCart(s);

        } catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
    }

    public void showShoppingCart()
    {
        new ShoppingCartView();
    }

    public void showAccountInformation()
    {
        new AccountView();
    }

    public void showOrdersInformation()
    {
        new OrdersView();
    }

    /**
     * Logs the user out (exits the application) and closes the database connection.
     */
    public void logOut()
    {
        try
        {
            PopUp.createPopUp("Goodbye!", "You have been logged out. The system will now exit.",
                    productsListView.getScene().getWindow(), "OK").showAndWait();

            productsListView.getScene().getWindow().hide();
            SQLConnector.getConnection().close();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
