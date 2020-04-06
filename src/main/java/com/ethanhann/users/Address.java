package com.ethanhann.users;

import com.ethanhann.validation.State;

import java.util.regex.Pattern;

/**
 * Class to represent an Address made up of 4 parts:
 * <ul>
 *     <li>Street</li>
 *     <li>City</li>
 *     <li>State</li>
 *     <li>Zip Code</li>
 * </ul>
 * The address {@code toString} method returns a string in the following format: <br>
 * {@code Street City, State, PostCode}. <br>
 * {@code Address.of()} method is capable of parsing an address in this format.
 */
public class Address
{
    private static String street = "";
    private static String city = "";
    private static State state = State.UNKNOWN;
    private static String postCode = "";

    public Address(String street, String city, State state, String postCode)
    {
        Address.street = street;
        Address.city = city;
        Address.state = state;
        Address.postCode = postCode;
    }

    public String getStreet()
    {
        return street;
    }

    public String getCity()
    {
        return city;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public State getState()
    {
        return state;
    }

    /**
     * Parses an Address object from a string representing an address.
     * @param address the address to parse in String form
     * @return Address object
     */
    public static Address of(String address)
    {
        //First half of string is street and city
        String[] tokens = address.split(",");
        String street = tokens[0];
        String city = tokens[1].trim();

        //Second half of string is state and zip code
        String[] stateAndZip = tokens[2].split(" ");
        State state = stateAndZip[1].length() == 2 ?
                State.valueOfAbbreviation(stateAndZip[1]) :
                State.valueOfName(stateAndZip[1]);

        String zip = stateAndZip[2];

        return new Address(street, city, state, zip);
    }

    /**
     * Simple validity check to make sure an address is valid.
     * Checks if the street and city are empty, if the state is unknown, and if the zip code
     * is in one of two formats: either {@code 00000} or {@code 00000-0000}
     * @return true if address is valid; false if not.
     */
    public boolean isValid()
    {
        return !street.equals("") && !city.equals("") && !state.equals(State.UNKNOWN) &&
                Pattern.matches("^\\d{5}(?:[-\\s]\\d{4})?$", postCode);
    }

    @Override
    public String toString()
    {
        return String.format("%s, %s, %s %s", street, city, state.getAbbreviation(), postCode);
    }
}
