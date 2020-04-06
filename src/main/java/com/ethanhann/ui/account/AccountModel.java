package com.ethanhann.ui.account;

import com.ethanhann.backend.SQLConnector;
import com.ethanhann.users.Account;
import com.ethanhann.users.Customer;

/**
 * Interfaces with the SQLConnector class to perform database operations on a user's Account.
 */
class AccountModel
{
    Customer getCurrentCustomer()
    {
        return SQLConnector.getCurrentCustomer();
    }

    Account parseAccount(String userId)
    {
        return SQLConnector.parseAccount(userId);
    }

    boolean checkPassword(String password)
    {
        return SQLConnector.checkPassword(password);
    }

    void updatePassword(String newPassword)
    {
        SQLConnector.updatePassword(newPassword);
    }

    boolean updateAccount(String newEmail, String newAddress)
    {
        return SQLConnector.updateAccount(newEmail, newAddress);
    }

    void closeAccount()
    {
        SQLConnector.closeAccount();
    }
}
