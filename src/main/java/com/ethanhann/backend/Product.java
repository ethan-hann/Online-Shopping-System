package com.ethanhann.backend;

import java.util.Comparator;

/**
 * This class represents a single Product item; Products are stored in the SQLite database
 *  and adhere to the properties in this class:
 * <ul>
 *     <li>id</li>
 *     <li>name</li>
 *     <li>description</li>
 *     <li>price</li>
 *     <li>supplier</li>
 * </ul>
 *  This class implements cloneable so that an exact copy of the product can be added to the
 *  user's shopping cart. <br> <br>
 *  {@code quantity} is not stored in the database; it is calculated and stored in this Product object.
 */
public class Product implements Cloneable, Comparable<Product>
{
    private final int id;
    private String name;
    private String description;
    private double price;
    private Supplier supplier;
    private int quantity;

    Product(int id, String name, String description, double price, Supplier supplier, int quantity)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.supplier = supplier;
        this.quantity = quantity;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public double getPrice()
    {
        return price;
    }

    public Supplier getSupplier()
    {
        return supplier;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public double getTotal()
    {
        return price * quantity;
    }

    /**
     * Comparator function to sort by product name (Ascending)
     */
    public static final Comparator<Product> NameComparator = Comparator.comparing(Product::getName);

    /**
     * Comparator function to sort by product price
     */
    public static final Comparator<Product> PriceComparator = Comparator.comparingDouble(Product::getPrice);

    /**
     * Compares two objects and if they are of instance {@code Product}, compares the id's
     * @param obj another object to compare
     * @return true if {@code this == obj} or if {@code this.id == obj.id}; false otherwise
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof Product))
        {
            return false;
        }

        Product p = (Product) obj;

        return p.getId() == id;
    }

    /**
     * Clones this object including the non-primitive object {@code Supplier}.
     * @return a copy of this object
     * @throws CloneNotSupportedException if the clone could not be completed.
     */
    @Override
    public Object clone() throws CloneNotSupportedException
    {
        Product cProduct = (Product) super.clone();

        //have to call supplier.clone() since its not a primitive
        cProduct.supplier = (Supplier) supplier.clone();
        return cProduct;
    }

    /**
     * Compares two products by their quantity
     * @param other the other product
     * @return -integer if this is less than other, 0 if they are equal, and +integer if this is greater than other
     */
    @Override
    public int compareTo(Product other)
    {
        return (this.quantity - other.getQuantity());
    }

    @Override
    public String toString()
    {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", supplier=" + supplier +
                ", quantity=" + quantity +
                '}';
    }
}
