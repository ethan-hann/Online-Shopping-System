package com.ethanhann.backend;

/**
 * This class represents a single Supplier; Suppliers are stored in the SQLite database
 *  and adhere to the properties in this class:
 * <ul>
 *     <li>id</li>
 *     <li>name</li>
 * </ul>
 *  This class implements cloneable so that an exact copy of the Supplier can be cloned
 *   with the Product.
 */
public class Supplier implements Cloneable
{
    private final int id;
    private String name;

    Supplier(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}
