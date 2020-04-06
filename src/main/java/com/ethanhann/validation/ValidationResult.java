package com.ethanhann.validation;

/**
 * Enum representing a result that could happen based on validity checks
 * using CustomerRegistrationValidation
 */
public enum ValidationResult
{
    SUCCESS,
    INVALID_PHONE,
    INVALID_EMAIL,
    INVALID_ADDRESS,
    USER_EXISTS,
    ACCOUNT_CLOSED,
    UNKNOWN_ERROR
}
