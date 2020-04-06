package com.ethanhann.ui.shopping_cart;

import com.ethanhann.backend.SQLConnector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Displays the Shopping Cart GUI to the user using the specified FXML file as the layout.
 */
public class ShoppingCartView extends Stage
{
    public ShoppingCartView()
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("/shopping_cart.fxml"));

            if (SQLConnector.getCurrentCustomer() != null)
            {
                setTitle("Shopping Cart for " + SQLConnector.getCurrentCustomer().getUserID());
            }
            else
            {
                setTitle("No current customer");
            }

            setScene(new Scene(root));
            setAlwaysOnTop(true);
            setResizable(false);
            initModality(Modality.APPLICATION_MODAL);
            show();

        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
