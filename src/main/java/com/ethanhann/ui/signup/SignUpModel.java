package com.ethanhann.ui.signup;

import com.ethanhann.backend.SQLConnector;
import com.ethanhann.validation.ValidationResult;

/**
 * Interfaces with the SQLConnector class to perform database operations.
 */
class SignUpModel
{
    /**
     * Checks if a user exists in the database and adds them to the database if they do not. The user
     *  is only added if the validation checks return SUCCESS. Otherwise, the ValidationResult is returned
     *  and an error message is displayed from the calling method.
     * @param userName the user's username
     * @param password the user's password
     * @param address the user's address
     * @param phoneNumber the user's phone number
     * @param email the user's email
     * @return ValidationResult representing the result of validating the user against various criteria
     */
    ValidationResult addUser(String userName, String password, String address, String phoneNumber, String email)
    {
        return SQLConnector.addUser(userName, password, address, phoneNumber, email);
    }
}
