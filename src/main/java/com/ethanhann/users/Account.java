package com.ethanhann.users;
import java.util.Date;

/**
 * This class represents a single Account; Accounts are stored in the SQLite database
 *  and adhere to the properties in this class:
 * <ul>
 *     <li>id</li>
 *     <li>billing_address</li>
 *     <li>isClosed</li>
 *     <li>openDate</li>
 *     <li>closeDate</li>
 * </ul>
 * This class makes use of Generics to allow storing the date in an arbitrary date-format.
 */
public class Account<T extends Date>
{
    private String id;
    private Address billingAddress;
    private boolean isClosed;
    private T openDate;
    private T closeDate;

    public Account(String id, Address billingAddress, boolean isClosed, T openDate)
    {
        this.id = id;
        this.billingAddress = billingAddress;
        this.isClosed = isClosed;
        this.openDate = openDate;
    }

    public Account(String id, Address billingAddress, boolean isClosed, T openDate, T closeDate)
    {
        this.id = id;
        this.billingAddress = billingAddress;
        this.openDate = openDate;
        this.isClosed = isClosed;
        this.closeDate = closeDate;
    }

    public String getId()
    {
        return id;
    }

    public Address getBillingAddress()
    {
        return billingAddress;
    }

    public T getOpenDate()
    {
        return openDate;
    }

    public T getCloseDate()
    {
        return closeDate;
    }

    public boolean isClosed()
    {
        return isClosed;
    }

    public void setClosed(boolean closed)
    {
        isClosed = closed;
    }
}
