package com.motherlove.models.enums;

public enum VoucherStatus {
    INACTIVE(0),
    ACTIVE(1),
    EXPIRE(2);
    private final int value;

    VoucherStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
