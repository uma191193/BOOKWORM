package com.bookworm.dto.order;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Inline delivery address submitted at checkout.
 * Stored as a snapshot on the order — no separate address entity is required.
 */
public record AddressRequest(

        @NotBlank String firstName,
        @NotBlank String lastName,

        @NotBlank @Email String email,

        @NotBlank @Pattern(regexp = "^[0-9+\\-\\s]{7,20}$", message = "Invalid phone number")
        String phone,

        @NotBlank String addressLine1,
        String       addressLine2,

        @NotBlank String city,

        @NotBlank @Pattern(regexp = "^[0-9]{4,10}$", message = "Invalid PIN / ZIP code")
        String pin,

        @NotBlank String state,

        @NotBlank String country
) {}
