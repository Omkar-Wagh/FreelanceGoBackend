package com.freelancego.listeners.types;

import com.freelancego.enums.NotificationType;
import com.freelancego.model.User;

public record BidEvent(User actor, User recipient, NotificationType notificationType) {
}