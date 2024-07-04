package com.motherlove.models.enums;

public enum ProductStatus {
    INACTIVE(0),
    ACTIVE(1),
    PRE_ORDER(2),
    NEAR_OUT_OF_STOCKS(3);

    private final int value;

    ProductStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
