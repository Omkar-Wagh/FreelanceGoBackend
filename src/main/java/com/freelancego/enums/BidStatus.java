package com.freelancego.enums;

public enum BidStatus {
    PENDING,   // submitted, no action yet
    ACCEPTED,  // chosen -> contract created
    REJECTED,  // explicitly declined or another bid accepted
    EXPIRED    // job closed without action
}


