package com.hospital.authservice.factory;

import com.hospital.authservice.dto.RegisterRequest;

/**
 * Creator in Factory Method Pattern.
 * Declares the factory method 'createUserProfile' that returns a UserProfile Product.
 */
public abstract class UserFactory {

    /**
     * The Factory Method.
     * Concrete sub-classes override this method to produce specialized UserProfile products.
     */
    public abstract UserProfile createUserProfile(RegisterRequest request);
}
