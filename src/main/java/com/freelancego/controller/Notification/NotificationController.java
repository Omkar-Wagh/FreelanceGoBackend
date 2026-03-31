package com.freelancego.controller.Notification;

import com.freelancego.model.Notification;
import com.freelancego.service.notification.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * The NotificationController class provides RESTful endpoints for retrieving and marking notifications.
 *
 * The following endpoints are provided:
 *   - GET /notifications: Retrieves all notifications for the authenticated user with pagination.
 *   - PATCH /notifications/seen: Marks all notifications as seen for the authenticated user.
 *
 * @author Koustubh Karande
 * @see NotificationService
 * @see Notification
 */
@RestController
@RequestMapping(path = "/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Retrieves all notifications for the authenticated user with pagination.
     *
     * @param page the page number of the notifications to retrieve
     * @param size the number of notifications per page
     * @param authentication the authentication context
     * @return a {@link ResponseEntity} containing a {@link Page} of {@link Notification} objects
     */
    @GetMapping
    public ResponseEntity<Page<Notification>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        Page<Notification> notifications = notificationService.getAllNotifications(page, size, authentication);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Marks all notifications as seen for the authenticated user.
     *
     * @param authentication the authentication context
     * @return a {@link ResponseEntity} with a 200 OK status
     */
    @PatchMapping(path = "/seen")
    public ResponseEntity<Void> markNotificationsAsSeen(Authentication authentication) {
        notificationService.markAllNotificationsAsSeen(authentication);
        return ResponseEntity.noContent().build();
    }
}