package com.niyaz000.auth_service.enums;

import lombok.Getter;

@Getter
public enum CountryCode {

    IN("+91");

    private final String countryCode;

    CountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
