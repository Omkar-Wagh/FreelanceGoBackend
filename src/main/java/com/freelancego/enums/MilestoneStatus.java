package com.freelancego.enums;

public enum MilestoneStatus {
    PENDING,              // Not started yet
    IN_PROGRESS,          // Freelancer working on it
    SUBMITTED,            // Freelancer submitted deliverable
    REVISION_REQUESTED,   // Client requested changes
    APPROVED,             // Client approved the work
    COMPLETED,            // Payment released and milestone closed
    CANCELLED             // Cancelled before completion
}
// related with final submission work and not with verification status