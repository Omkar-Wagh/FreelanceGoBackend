package com.freelancego.enums;

public enum PayoutAccountStatus {
    NOT_CREATED,          // Freelancer has not added payout details
    PENDING_VERIFICATION, // Razorpay verifying bank details
    ACTIVE,               // Payout allowed
    FAILED                // Verification failed / payout blocked
}
