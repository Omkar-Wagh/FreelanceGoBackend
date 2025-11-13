package com.freelancego.enums;

public enum ContractStatus {
    PENDING,       // Contract created but not yet started
    ACTIVE,        // Contract accepted and in progress
    ON_HOLD,       // Paused due to dispute or client action
    COMPLETED,     // All milestones finished and paid
    CANCELLED      // Terminated by client or freelancer
}