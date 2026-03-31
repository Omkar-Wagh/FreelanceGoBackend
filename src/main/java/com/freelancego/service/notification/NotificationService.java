package com.freelancego.service.notification;

import com.freelancego.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

public interface NotificationService {
    Page<Notification> getAllNotifications(int page, int size, Authentication authentication);

    void markAllNotificationsAsSeen(Authentication authentication);
}
