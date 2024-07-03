package com.motherlove.models.enums;

public enum ProductStatus {
    INACTIVE(1),
    ACTIVE(2),
    PRE_ORDER(3),
    NEAR_OUT_OF_STOCKS(4),
    OUT_OF_STOCKS(5);

    private final int value;

    ProductStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
