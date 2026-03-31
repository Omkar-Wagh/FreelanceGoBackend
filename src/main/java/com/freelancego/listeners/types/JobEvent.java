package com.freelancego.listeners.types;

import com.freelancego.enums.NotificationType;
import com.freelancego.model.Job;
import com.freelancego.model.User;

public record JobEvent(User actor, Job job, NotificationType notificationType) {
}
