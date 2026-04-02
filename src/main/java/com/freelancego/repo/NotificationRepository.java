package com.freelancego.repo;

import com.freelancego.model.Notification;
import com.freelancego.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    @Query("""
        SELECT n FROM Notification n
        WHERE n.user = :user
           OR n.isGeneral = true
        ORDER BY n.createdAt DESC
        """)
    Page<Notification> findUserAndGeneralNotifications(
            @Param("user") User user,
            Pageable pageable
    );

    /* Make this method's return type as int,
     if number of affected rows are needed */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE Notification n
        SET n.seen = true
        WHERE n.user = :user
          AND n.seen = false
          AND n.isGeneral = false
        """)
    void markUserNotificationsAsSeenExcludingGeneral(@Param("user") User user);
}
