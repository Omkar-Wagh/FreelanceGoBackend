package com.freelancego.listeners.classes;

import com.freelancego.enums.NotificationType;
import com.freelancego.listeners.types.*;
import com.freelancego.model.Notification;
import com.freelancego.repo.NotificationRepository;
import com.freelancego.repo.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class GlobalEventListener {
    final private NotificationRepository notificationRepository;
    final private UserRepository userRepository;

    public GlobalEventListener(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @EventListener
    public void onMilestoneEvent(MilestoneEvent event) {
        Notification notification = new Notification(
                event.recipient(),
                event.actor(),
                event.notificationType()
        );

        notificationRepository.save(notification);
    }

    @EventListener
    public void onPaymentEvent(PaymentEvent event) {
        Notification notification = new Notification(
                event.recipient(),
                event.actor(),
                event.notificationType()
        );

        notificationRepository.save(notification);
    }

    /*
    // This is what the actual method should be like
    @Async
    @EventListener
    public void onJobCreated(JobCreatedEvent event) {
        List<String> skills = Arrays.stream(event.job().getRequiredSkills().split(",")).map(String::trim).toList();

        List<User> relevantFreelancers = userRepository.findFreelancersBySkills(skills);

        List<Notification> notifications = relevantFreelancers.stream()
                .map(user -> new Notification(user, event.client().getUser(), NotificationType.JOB_CREATED)).toList();

        notificationRepository.saveAll(notifications);
    }
     */

    @EventListener
    public void onJobEvent(JobEvent event) {
        // * Using a broadcast type notification constructor(General type)
        Notification notification = new Notification(
                event.actor(),
                event.notificationType(),
                true
        );

        notificationRepository.save(notification);
    }

    @EventListener
    public void onBidEvent(BidEvent bidEvent) {
        Notification notification = new Notification(
                bidEvent.recipient(),
                bidEvent.actor(),
                bidEvent.notificationType()
        );

        notificationRepository.save(notification);
    }

    @EventListener
    public void onBidRejected(BidRejectedEvent event) {
        Notification notification = new Notification(
                event.actor(),
                event.notificationType(),
                true
        );
    }
}