package com.ethanhann.ui.signup;

import com.ethanhann.ui.PopUp;
import com.ethanhann.users.Address;
import com.ethanhann.validation.State;
import com.ethanhann.validation.ValidationResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Controls communication between the view (stage) and the database via the {@code SignUpModel} class.
 */
public class SignUpController
{

    private final SignUpModel signUpModel = new SignUpModel();

    public TextField streetText;
    public TextField cityText;
    public ComboBox<String> stateChoiceBox;
    public TextField zipCodeText;
    public TextField userNameText;
    public PasswordField passwordText;
    public PasswordField passwordConfirmText;
    public TextField emailText;
    public TextField phoneNumberText;
    public Label errorLabel;
    public Label errorLabel2;

    /**
     * Converts a State Enum to a human readable string.
     */
    private static final Function<State, String> toHumanReadableString = State::getName;

    @FXML
    public void initialize()
    {
        ObservableList<String> choices = FXCollections.observableArrayList();

        String[] stateStrings = Arrays.stream(State.values()).map(toHumanReadableString).toArray(String[]::new);

        choices.addAll(stateStrings);

        stateChoiceBox.setItems(choices);
        stateChoiceBox.getSelectionModel().select(0);
    }

    /**
     * Performs a sign-up action when the Ok button is clicked.
     * First checks if password fields are equal. Then checks various validation
     * using validateAndAddUser(...) which returns a ValidationResult enum.
     * If the result is ValidationResult.SUCCESS the user is added to the database and the view
     * is closed.
     */
    public void signUp()
    {
        String username = userNameText.getText();
        String password = passwordText.getText();
        String passwordConfirm = passwordConfirmText.getText();
        String street = streetText.getText();
        String city = cityText.getText();
        State state = State.valueOfName(stateChoiceBox.getSelectionModel().getSelectedItem());
        String zipCode = zipCodeText.getText();
        String email = emailText.getText();
        String phoneNumber = phoneNumberText.getText();

        if (!password.equals(passwordConfirm)) {
            errorLabel.setText("Passwords do not match. Please try again.");
        }
        else
        {
            ValidationResult result = validateAndAddUser(username, password, new Address(street, city, state, zipCode), phoneNumber, email);
            if (result == ValidationResult.SUCCESS)
            {
                userNameText.getScene().getWindow().hide();
                PopUp.createPopUp("Account Created",
                        "You may now login with your username and password.",
                        userNameText.getScene().getWindow(), "OK").showAndWait();
            }
        }
    }

    /**
     * Validates and adds the user to the database.
     * @param username the user's username
     * @param password the user's password
     * @param address the user's address
     * @param phoneNumber the user's phone number
     * @param email the user's email
     */
    private ValidationResult validateAndAddUser(String username, String password, Address address, String phoneNumber, String email)
    {
        ValidationResult result = signUpModel.addUser(username, password, address.toString(), phoneNumber, email);
        switch (result)
        {
            case INVALID_ADDRESS:
                errorLabel.setText("Invalid address was entered. Please check your entry.");
                errorLabel2.setText("Zip code should be in either 00000 or 00000-0000 form.");
                break;
            case INVALID_PHONE:
                errorLabel.setText("Invalid phone number was entered. Please check your entry.");
                errorLabel2.setText("A phone number should have 10 digits in it and no dashes.");
                break;
            case INVALID_EMAIL:
                errorLabel.setText("Invalid email was entered. Please check your entry.");
                errorLabel2.setText("An e-mail should contain exactly one '@' symbol.");
                break;
            case USER_EXISTS:
                errorLabel.setText("A user with that username already exists. Please try again.");
                errorLabel2.setText("");
                break;
            case UNKNOWN_ERROR:
                errorLabel.setText("An unknown error has occurred. Please try again later.");
                errorLabel2.setText("");
                break;
            case SUCCESS:
                errorLabel.setText("");
                errorLabel2.setText("");
                break;
            default:
                System.err.println("An unknown error occurred.");
        }
        return result;
    }
}
