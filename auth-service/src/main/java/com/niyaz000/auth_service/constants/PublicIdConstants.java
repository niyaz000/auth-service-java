package com.niyaz000.auth_service.constants;

import java.security.SecureRandom;

public final class PublicIdConstants {

    public static final char[] DEFAULT_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            .toCharArray();

    public static final int DEFAULT_SIZE = 13;

    public static final SecureRandom DEFAULT_NUMBER_GENERATOR = new SecureRandom();
}
