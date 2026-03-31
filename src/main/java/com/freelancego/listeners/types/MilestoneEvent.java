package com.freelancego.listeners.types;

import com.freelancego.enums.NotificationType;
import com.freelancego.model.Client;
import com.freelancego.model.Freelancer;
import com.freelancego.model.User;

public record MilestoneEvent(User actor, User recipient, NotificationType notificationType) {
}
