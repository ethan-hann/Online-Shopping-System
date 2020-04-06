package com.ethanhann.users;

/**
 * This class represents a single Customer; Customers are stored in the SQLite database
 *  and adhere to the properties in this class:
 * <ul>
 *     <li>id</li>
 *     <li>address</li>
 *     <li>phone</li>
 *     <li>email</li>
 * </ul>
 */
public class Customer
{
    private final String userID;
    private Address address;
    private String phoneNumber;
    private String email;

    public Customer(String userID, Address address, String phoneNumber, String email)
    {
        this.userID = userID;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getUserID()
    {
        return userID;
    }

    public Address getAddress()
    {
        return address;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public String getEmail()
    {
        return email;
    }

    @Override
    public String toString()
    {
        return this.userID.concat("\t").concat(this.email);
    }
}
