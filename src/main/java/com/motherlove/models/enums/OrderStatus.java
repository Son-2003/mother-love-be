package com.motherlove.models.enums;

public enum OrderStatus {
    PRE_ORDER(0),
    PENDING(1),
    CONFIRMED(2),
    CANCELLED(3),
    COMPLETED(4);
    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
