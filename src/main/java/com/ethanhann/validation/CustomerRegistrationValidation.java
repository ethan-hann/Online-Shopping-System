package com.ethanhann.validation;

import com.ethanhann.users.Customer;

import java.util.function.Function;
import java.util.function.IntPredicate;

/**
 * Interface which uses the Combinator pattern to perform validity checks on data.
 * This Interface checks for a valid Customer.
 */
public interface CustomerRegistrationValidation extends Function<Customer, ValidationResult>
{
    /**
     * Checks the user's email to ensure it contains only one '@' symbol.
     * @return CustomerRegistrationValidation
     */
    static CustomerRegistrationValidation isEmailValid()
    {
        IntPredicate containsAtSymbol = ch -> ch == '@';
        return customer -> customer.getEmail().codePoints().filter(containsAtSymbol).count() == 1L ?
                ValidationResult.SUCCESS :
                ValidationResult.INVALID_EMAIL;
    }

    /**
     * Checks the user's phone number to ensure it contains only 10 digits.
     * @return CustomerRegistrationValidation
     */
    static CustomerRegistrationValidation isPhoneValid()
    {
        return customer -> customer.getPhoneNumber().matches("\\d{10}") ?
                ValidationResult.SUCCESS :
                ValidationResult.INVALID_PHONE;
    }

    /**
     * Checks the user's address using the address's {@code isValid()} method.
     * @return CustomerRegistrationValidation
     */
    static CustomerRegistrationValidation isAddressValid()
    {
        return customer -> customer.getAddress().isValid() ?
                ValidationResult.SUCCESS :
                ValidationResult.INVALID_ADDRESS;
    }

    /**
     * Allows chaining of Validation tests. Will only continue chaining if the previous
     * validation result returned SUCCESS. Otherwise, it will stop and return the last failed
     * validation.
     * @param other another CustomerRegistrationValidation
     * @return CustomerRegistrationValidation
     */
    default CustomerRegistrationValidation and(CustomerRegistrationValidation other)
    {
        return customer -> {
            ValidationResult result = this.apply(customer);
            return result.equals(ValidationResult.SUCCESS) ? other.apply(customer) : result;
        };
    }
}
