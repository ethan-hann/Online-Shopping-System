package com.ethanhann.ui.home;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Displays the Home GUI to the user using the specified FXML file as the layout.
 */
public class HomeView extends Stage
{
    public HomeView()
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("/home.fxml"));
            setTitle("Online Shopping System");
            setScene(new Scene(root));
            setResizable(true);
            initModality(Modality.WINDOW_MODAL);
            show();
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
