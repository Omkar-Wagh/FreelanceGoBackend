package com.freelancego.enums;

public enum PaymentStatus {
    NOT_PAID,        // Default state before any payment
    ESCROW_HELD,     // Client deposited funds but not released
    RELEASED,        // Payment released to freelancer
    REFUNDED,        // Payment refunded to client
    FAILED           // Transaction failed
}