package com.freelancego.service.notification.impl;

import com.freelancego.model.Notification;
import com.freelancego.model.User;
import com.freelancego.repo.NotificationRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.notification.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<Notification> getAllNotifications(int page, int size, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User with email " + authentication.getName() + " was not found"));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );
        return notificationRepository.findByUser(user, pageable);
    }

    @Override
    @Transactional
    public void markAllNotificationsAsSeen(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User with email " + authentication.getName() + " was not found"));

        notificationRepository.markUserNotificationsAsSeenExcludingGeneral(user);
    }
}