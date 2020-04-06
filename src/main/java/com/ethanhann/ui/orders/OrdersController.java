package com.ethanhann.ui.orders;

import com.ethanhann.backend.Order;
import com.ethanhann.backend.OrderStatus;
import com.ethanhann.backend.Product;
import com.ethanhann.ui.PopUp;
import com.ethanhann.users.Customer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.sql.Date;

/**
 * Controls communication between the view (stage) and the database via the {@code OrdersModel} class.
 */
public class OrdersController
{
    private OrdersModel ordersModel;

    public ListView<Order<Date>> ordersListView;
    public Label orderNumberText;
    public Label orderedDateText;
    public Label shippedDateText;
    public Label orderStatusText;
    public Label orderTotalText;
    public Button cancelOrderButton;
    public GridPane productsGridPane;

    @FXML
    public void initialize()
    {
        ordersModel = new OrdersModel();

        showOrders();
        showProducts();
    }

    /**
     * Show all the orders under the user's id
     */
    private void showOrders()
    {
        Customer c = ordersModel.getCurrentCustomer();
        if (c != null)
        {
            ObservableList<Order<Date>> orders = ordersModel.getOrders(c.getUserID());
            ordersListView.setItems(orders);
        }

        ordersListView.setCellFactory(p -> new ListCell<>(){
            @Override
            protected void updateItem(Order<Date> item, boolean empty)
            {
                super.updateItem(item, empty);

                if (empty || item == null)
                {
                    setText(null);
                }
                else
                {
                    setText(String.format("%d", item.getId()));
                    setFont(new Font(12));
                }
            }
        });

        ordersListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null)
                    {
                        cancelOrderButton.setDisable(false);
                        orderNumberText.setText(String.format("%d", newValue.getId()));
                        orderedDateText.setText(newValue.getOrderedDate().toString());
                        shippedDateText.setText(newValue.getShippedDate().toString());

                        //Disable cancel order button if the items are already shipped.
                        OrderStatus status = ordersModel.checkForStatusUpdate(newValue.getOrderedDate(), newValue.getShippedDate());
                        if (status == OrderStatus.SHIPPED)
                        {
                            cancelOrderButton.setDisable(true);
                        }

                        if (status == OrderStatus.CLOSED)
                        {
                            cancelOrderButton.setDisable(true);
                            ordersModel.cancelOrder(newValue);
                        }

                        orderStatusText.setText(status.name());
                        orderTotalText.setText(String.format("$%.2f", newValue.getTotal()));

                        showProducts();
                    }
                    else
                    {
                        orderNumberText.setText("");
                        orderedDateText.setText("");
                        shippedDateText.setText("");
                        orderStatusText.setText("");
                        orderTotalText.setText("");
                        cancelOrderButton.setDisable(true);
                    }
                });
        ordersListView.getSelectionModel().select(0);
    }

    /**
     * Displays products in a GridPane based on how many products are in the Order.
     * In order to dynamically show the products, we have to add a new Label to the GridPane <br>
     * for each new row and column.
     */
    private void showProducts()
    {
        if (!ordersListView.getItems().isEmpty())
        {
            Order<Date> selectedOrder = ordersListView.getSelectionModel().getSelectedItem();

            //Remove everything after the header row to prevent Labels
            // from overlapping every time this method is called.
            productsGridPane.getChildren().remove(3, productsGridPane.getChildren().size());

            int rowIndex = 1; //Always start at row 1; row 0 is the header row and contains static information.
            for (int i = 0; i < selectedOrder.getItems().size(); i++)
            {
                int colIndex = 0; //Reset the column index each iteration.
                Product p = selectedOrder.getItems().get(i);
                productsGridPane.add(new Label(p.getName()), colIndex, rowIndex);
                productsGridPane.add(new Label(String.format("%d", p.getQuantity())), colIndex + 1, rowIndex);
                productsGridPane.add(new Label(String.format("$%.2f", p.getTotal())), colIndex + 2, rowIndex);
                rowIndex++;
            }
        }
        else
        {
            productsGridPane.getChildren().remove(3, productsGridPane.getChildren().size());
        }
    }

    /**
     * Cancel (remove) the selected order.
     */
    public void cancelOrder()
    {
        if (!ordersListView.getItems().isEmpty())
        {
            PopUp.createPopUp("Cancel Order?", "Are you sure you want to cancel this order?",
                    ordersListView.getScene().getWindow(), "OK_CANCEL").showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response ->
                    {
                        ordersModel.cancelOrder(ordersListView.getSelectionModel().getSelectedItem());
                        showOrders();
                        showProducts();
                    });
        }
    }
}
