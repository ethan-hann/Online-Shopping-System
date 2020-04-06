package com.ethanhann.ui.shopping_cart;

import com.ethanhann.backend.Product;
import com.ethanhann.backend.ShoppingCart;
import com.ethanhann.ui.PopUp;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;

import java.util.Collections;

/**
 * Controls communication between the view (stage) and the database via the {@code ShoppingCartModel} class.
 */
public class ShoppingCartController
{
    private ShoppingCartModel shoppingCartModel;

    public ListView<Product> productsListView;
    public Label priceLabel;
    public Label quantityLabel;
    public Label totalLabel;


    @FXML
    public void initialize()
    {
        shoppingCartModel = new ShoppingCartModel();
        showShoppingCartItems();
    }

    private void showShoppingCartItems()
    {
        productsListView.setItems(shoppingCartModel.getShoppingCart().getItems());

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
                    setFont(new Font(13));
                }
            }
        });

        //When a list item is selected, the appropriate information is displayed
        productsListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null && newValue.getQuantity() > 0)
                    {
                        priceLabel.setText(String.format("Price: $%.2f", newValue.getPrice()));
                        quantityLabel.setText(String.format("Quantity: %d", newValue.getQuantity()));
                        totalLabel.setText(String.format("Total: $%.2f", shoppingCartModel.getShoppingCart().getTotal()));
                    }
                    else
                    {
                        priceLabel.setText("Price: $");
                        quantityLabel.setText("Quantity:");
                        totalLabel.setText("Total: $");
                    }
                });

        productsListView.getSelectionModel().select(0);
    }

    /**
     * Sorts products in shopping cart by price (Ascending)
     */
    public void sortByPriceAscending()
    {
        productsListView.getItems().sort(Product.PriceComparator);
        productsListView.getSelectionModel().select(0);
    }

    /**
     * Sorts products in shopping cart by quantity
     */
    public void sortByQuantityAscending()
    {
        productsListView.getItems().sort(Product::compareTo);
        productsListView.getSelectionModel().select(0);
    }

    /**
     * Sorts products in shopping cart by price (Descending)
     */
    public void sortByPriceDescending()
    {
        productsListView.getItems().sort(Collections.reverseOrder(Product.PriceComparator));
        productsListView.getSelectionModel().select(0);
    }

    /**
     * Sorts products in shopping cart by quantity (Descending)
     */
    public void sortByQuantityDescending()
    {
        productsListView.getItems().sort(Collections.reverseOrder(Product::compareTo));
        productsListView.getSelectionModel().select(0);
    }

    /**
     * Remove a single item from the shopping cart. <br>
     * Removes the item from the shopping cart, decreases its quantity by 1, and if its
     * new quantity is greater than 0, the product is re-added to the shopping cart.
     */
    public void removeItem()
    {
        Product selectedProduct = productsListView.getSelectionModel().getSelectedItem();
        ShoppingCart sc = shoppingCartModel.getShoppingCart();

        for (int i = sc.getItems().size() - 1; i >= 0; i--)
        {
            Product p = sc.getItems().get(i);
            if (p.equals(selectedProduct))
            {
                sc.getItems().remove(p);
                p.setQuantity(p.getQuantity() - 1);
                if (p.getQuantity() > 0)
                {
                    sc.getItems().add(p);
                }
            }
        }

        shoppingCartModel.updateShoppingCart(sc);
        productsListView.setItems(shoppingCartModel.getShoppingCart().getItems());
        productsListView.refresh();

        productsListView.getSelectionModel().select(0);
    }

    /**
     * Remove all of the selected item from the shopping cart.
     */
    public void removeAll()
    {
        Product selectedProduct = productsListView.getSelectionModel().getSelectedItem();
        ShoppingCart sc = shoppingCartModel.getShoppingCart();

        for (int i = sc.getItems().size() - 1; i >= 0; i--)
        {
            Product p = sc.getItems().get(i);
            if (p.equals(selectedProduct))
            {
                sc.getItems().remove(p);
            }
        }

        shoppingCartModel.updateShoppingCart(sc);
        productsListView.setItems(shoppingCartModel.getShoppingCart().getItems());
        productsListView.refresh();

        productsListView.getSelectionModel().select(0);
    }

    /**
     * Removes all products from shopping cart. Only happens if the user acknowledges via the OK button.
     */
    public void clearCart()
    {
        PopUp.createPopUp("Remove all items?", "Are you sure you want to remove all items from cart?",
                productsListView.getScene().getWindow(), "OK_CANCEL").showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response ->
                {
                    shoppingCartModel.updateShoppingCart("");
                    productsListView.setItems(shoppingCartModel.getShoppingCart().getItems());
                    productsListView.refresh();
                    System.out.println("Clearing...");
                });
    }

    /**
     * Performs a check out operation on the user's shopping cart.
     */
    public void checkOut()
    {
        if (shoppingCartModel.getShoppingCart().getItems().isEmpty())
        {
            PopUp.createPopUp("Oops",
                    "You have no items in your shopping cart! Add some items first.",
                    productsListView.getScene().getWindow(), "OK").showAndWait();
        }
        else
        {
            boolean orderSuccessful = shoppingCartModel.createOrder();

            if (orderSuccessful)
            {
                PopUp.createPopUp("Thank You!",
                        "Order has been successfully placed and will be shipped within 3 days.",
                        productsListView.getScene().getWindow(), "OK").showAndWait();
            }
            else
            {
                PopUp.createPopUp("Oops",
                        "There was a problem placing your order. Please try again later.",
                        productsListView.getScene().getWindow(), "OK").showAndWait();
            }
        }
    }
}
