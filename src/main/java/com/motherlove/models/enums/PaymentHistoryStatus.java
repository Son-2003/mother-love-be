package com.motherlove.models.enums;

public enum PaymentHistoryStatus {
    FAILED(0),
    SUCCESS(1);

    private final int value;

    PaymentHistoryStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
