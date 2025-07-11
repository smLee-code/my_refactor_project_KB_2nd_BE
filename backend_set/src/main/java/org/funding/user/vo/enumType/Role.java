package org.funding.user.vo.enumType;

public enum Role {

    ROLE_NORMAL, ROLE_FINANCE, ROLE_ADMIN;

    public static Role fromString(String value) {
        for (Role role : Role.values()) {
            if (role.name().equals(value)) {
                return role;
            }
        }

        throw new IllegalArgumentException("Unknown role: " + value);
    }
}
