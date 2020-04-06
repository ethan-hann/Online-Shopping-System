package com.ethanhann.ui.signup;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Displays the Sign Up GUI to the user using the specified FXML file as the layout.
 */
public class SignUpView extends Stage
{
    public SignUpView()
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource("/signUp.fxml"));
            setTitle("Create a new account");
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
