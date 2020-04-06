package com.ethanhann.ui.login;

import com.ethanhann.backend.SQLConnector;
import com.ethanhann.ui.signup.SignUpView;
import com.ethanhann.ui.home.HomeView;
import com.ethanhann.validation.ValidationResult;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controls communication between the view (stage) and the database via the {@code LoginModel} class.
 */
public class LoginController
{
    private static final LoginModel loginModel = new LoginModel();

    public Label errorLabel;
    public TextField usernameInputText;
    public PasswordField passwordInputText;
    public Button loginButton;
    public Button signUpButton;

    /**
     * Performs a login action when the Login button is clicked.
     * First checks if the user can login (i.e. valid username and password and account is not closed).
     * Then, closes the LoginView and creates a new HomeView where the user can
     * interact with the system.
     */
    public void login()
    {
        ValidationResult result = loginModel.canLogin(usernameInputText.getText(), passwordInputText.getText());
        switch (result)
        {
            case SUCCESS:
            {
                if (SQLConnector.getCurrentCustomer() != null)
                {
                    usernameInputText.getScene().getWindow().hide();
                    new HomeView();
                    System.out.println("Valid user: " + SQLConnector.getCurrentCustomer().toString());
                }
                break;
            }
            case ACCOUNT_CLOSED:
            {
                errorLabel.setText("Your account has been closed. Please register for a new account.");
                break;
            }
            default:
            {
                errorLabel.setText("Invalid email or password. Please try again.");
            }
        }
    }

    /**
     * Performs a sign-up action when the Sign Up button is clicked.
     * Creates a new SignUpView that allows the user to enter information to
     * create an account.
     */
    public void signUp()
    {
        new SignUpView();
    }
}
