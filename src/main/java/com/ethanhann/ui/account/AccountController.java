package com.ethanhann.ui.account;

import com.ethanhann.backend.SQLConnector;
import com.ethanhann.ui.PopUp;
import com.ethanhann.users.Account;
import com.ethanhann.users.Address;
import com.ethanhann.users.Customer;
import com.ethanhann.validation.State;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Controls communication between the view (stage) and the database via the {@code AccountModel} class.
 */
public class AccountController
{
    private AccountModel accountModel;

    public GridPane gridPane;
    public Button updateAccountButton;
    public Button closeAccountButton;
    public Label accountClosedLabel;
    public Label accountOpenedLabel;
    public TextArea billingAddressTextArea;
    public Button changePasswordButton;
    public Label userIDLabel;
    public TextField emailTextField;

    @FXML
    public void initialize()
    {
        accountModel = new AccountModel();
        getInformation();
    }

    private void getInformation()
    {
        Customer c = accountModel.getCurrentCustomer();
        if (c != null)
        {
            Account currentAccount = accountModel.parseAccount(c.getUserID());

            if (currentAccount != null)
            {
                userIDLabel.setText(currentAccount.getId());
                emailTextField.setText(c.getEmail());

                StringBuilder builder = new StringBuilder();
                Address a = currentAccount.getBillingAddress();
                builder.append(a.getStreet())
                       .append("\n")
                       .append(a.getCity())
                       .append("\n")
                       .append(a.getState().getAbbreviation())
                       .append("\n")
                       .append(a.getPostCode());
                billingAddressTextArea.setText(builder.toString());

                accountOpenedLabel.setText(currentAccount.getOpenDate().toString());

                if (currentAccount.getCloseDate() != null)
                {
                    accountClosedLabel.setText(currentAccount.getCloseDate().toString());
                }
                else
                {
                    accountClosedLabel.setText("Account still open.");
                }
            }
        }
    }

    public void changePassword()
    {
        Stage passwordStage = new Stage();
        passwordStage.setTitle("Password Update");

        GridPane pane = createPasswordChangeForm();
        addControlsToForm(pane);
        Scene scene = new Scene(pane, 350, 250);
        passwordStage.setScene(scene);
        passwordStage.setResizable(false);
        passwordStage.initModality(Modality.APPLICATION_MODAL);
        passwordStage.initOwner(gridPane.getScene().getWindow());
        passwordStage.show();
    }

    private GridPane createPasswordChangeForm()
    {
        GridPane pane = new GridPane();

        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(40, 40, 40, 40));
        pane.setHgap(10);
        pane.setVgap(10);

        ColumnConstraints columnOneConstraints = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.RIGHT);

        ColumnConstraints columnTwoConstrains = new ColumnConstraints(200,200, Double.MAX_VALUE);
        columnTwoConstrains.setHgrow(Priority.ALWAYS);

        pane.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);

        return pane;
    }

    private void addControlsToForm(GridPane pane)
    {
        Label errorLabel = new Label();
        errorLabel.setTextFill(Paint.valueOf("red"));
        errorLabel.setUnderline(true);
        pane.add(errorLabel, 0, 0, 2, 1);

        Label oldPasswordLabel = new Label("Old Password: ");
        pane.add(oldPasswordLabel, 0, 1);

        PasswordField oldPasswordTextField = new PasswordField();
        oldPasswordTextField.setPrefHeight(40);
        pane.add(oldPasswordTextField, 1, 1);

        Label newPasswordLabel = new Label("New Password: ");
        pane.add(newPasswordLabel, 0, 2);

        PasswordField newPasswordTextField = new PasswordField();
        newPasswordTextField.setPrefHeight(40);
        pane.add(newPasswordTextField, 1, 2);

        Button okButton = new Button("Change");
        okButton.setPrefHeight(40);
        pane.add(okButton, 0, 4, 2, 1);
        GridPane.setHalignment(okButton, HPos.CENTER);
        GridPane.setMargin(okButton, new Insets(20, 0, 20, 0));

        //TODO: More robust password verification using Combinator pattern
        okButton.setOnAction(e -> {
            String oldPassword = oldPasswordTextField.getText();
            String newPassword = newPasswordTextField.getText();

            if (oldPassword.equals(newPassword))
            {
                errorLabel.setText("Old password cannot be the same as the new password.");
            }
            else
            {
                if (accountModel.checkPassword(oldPassword))
                {
                    errorLabel.setText("");
                    accountModel.updatePassword(newPassword);
                    PopUp.createPopUp("Password updated", "You may now use the new password on next login.",
                            pane.getScene().getWindow(), "OK").showAndWait();
                }
                else
                {
                    errorLabel.setText("Old password is not correct!");
                }
            }
        });
    }

    public void updateAccount()
    {
        //TODO: More robust validation using Combinator pattern
        String newEmail = emailTextField.getText();
        String[] newBillingAddressTokens = billingAddressTextArea.getText().split("\n");
        StringBuilder builder = new StringBuilder();
        builder.append(newBillingAddressTokens[0]).append(", ")
                .append(newBillingAddressTokens[1]).append(", ");

        if (newBillingAddressTokens[2].length() == 2) {
            builder.append(State.valueOfAbbreviation(newBillingAddressTokens[2]).getAbbreviation()).append(" ");
        }
        else
        {
            builder.append(State.valueOfName(newBillingAddressTokens[2]).getAbbreviation()).append(" ");
        }

        builder.append(newBillingAddressTokens[3]);


        if (accountModel.updateAccount(newEmail, builder.toString()))
        {
            PopUp.createPopUp("Account updated", "Account has been updated.",
                    gridPane.getScene().getWindow(), "OK").showAndWait();
        }
    }

    public void closeAccount()
    {
        accountModel.closeAccount();

        try {
            PopUp.createPopUp("Goodbye!", "Your account has been closed. The system will now exit.",
                    gridPane.getScene().getWindow(), "OK").showAndWait();

            accountClosedLabel.setText("Account has been closed.");

            SQLConnector.getConnection().close();
            Platform.exit();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
