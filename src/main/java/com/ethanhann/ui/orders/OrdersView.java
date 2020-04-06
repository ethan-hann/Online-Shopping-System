package com.ethanhann.ui.orders;

import com.ethanhann.backend.SQLConnector;
import com.ethanhann.users.Customer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Displays the Orders GUI to the user using the specified FXML file as the layout.
 */
public class OrdersView extends Stage
{
    public OrdersView()
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("/orders.fxml"));
            Customer c = SQLConnector.getCurrentCustomer();
            if (c != null)
            {
                setTitle("Orders for " + c.getUserID());
            }

            setScene(new Scene(root));
            setAlwaysOnTop(true);
            setResizable(true);
            initModality(Modality.APPLICATION_MODAL);
            show();
        } catch (IOException | NullPointerException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
