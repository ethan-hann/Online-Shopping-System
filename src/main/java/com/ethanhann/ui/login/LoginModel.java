package com.ethanhann.ui.login;

import com.ethanhann.backend.SQLConnector;
import com.ethanhann.validation.ValidationResult;

/**
 * Interfaces with the SQLConnector class to perform database operations.
 */
class LoginModel
{
    /**
     * Determines if a user has entered correct information that is present in the database.
     * Connects to the database through SQLConnector to check the database.
     * @param userName the user's username (id)
     * @param password the user's password
     * @return the Customer object associated with the username and password; null otherwise
     */
    ValidationResult canLogin(String userName, String password)
    {
        return SQLConnector.canLogin(userName, password);
    }
}
