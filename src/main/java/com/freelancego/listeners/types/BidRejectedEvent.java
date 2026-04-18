package com.freelancego.listeners.types;

import com.freelancego.enums.NotificationType;
import com.freelancego.model.Bid;
import com.freelancego.model.User;

import java.util.List;

public record BidRejectedEvent(List<Bid> bidList, User actor, NotificationType notificationType) {
}