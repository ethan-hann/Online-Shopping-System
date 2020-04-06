package com.ethanhann.ui.account;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Displays the Account GUI to the user using the specified FXML file as the layout.
 */
public class AccountView extends Stage
{
    public AccountView()
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("/account.fxml"));

            setTitle("Account Details");

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
