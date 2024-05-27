package com.motherlove.models.entities;

public enum UserType {
    ADMIN("ADMIN"), CUSTOMER("CUSTOMER"), STAFF("STAFF");

    private final String type;

    UserType(String string) {
        type = string;
    }

    @Override
    public String toString() {
        return type;
    }
}
