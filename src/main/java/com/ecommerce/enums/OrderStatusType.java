package com.ecommerce.enums;

import com.ecommerce.models.order.OrderStatus;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public enum OrderStatusType {
    ORDER_PLACED,
    CANCELLED,
    ORDER_REJECTED,
    ORDER_CONFIRMED,
    ORDER_SHIPPED,
    DELIVERED,
    RETURN_REQUESTED,
    RETURN_REJECTED,
    RETURN_APPROVED,
    PICK_UP_INITIATED,
    PICK_UP_COMPLETED,
    REFUND_INITIATED,
    REFUND_COMPLETED,
    CLOSED;

    static {
        ORDER_PLACED.validTransitions.add(ORDER_CONFIRMED);
        ORDER_PLACED.validTransitions.add(ORDER_REJECTED);
        ORDER_PLACED.validTransitions.add(CANCELLED);

        CANCELLED.validTransitions.add(REFUND_INITIATED);
        CANCELLED.validTransitions.add(CLOSED);

        ORDER_REJECTED.validTransitions.add(REFUND_INITIATED);
        ORDER_REJECTED.validTransitions.add(CLOSED);

        ORDER_CONFIRMED.validTransitions.add(CANCELLED);
        ORDER_CONFIRMED.validTransitions.add(ORDER_SHIPPED);

        ORDER_SHIPPED.validTransitions.add(DELIVERED);

        DELIVERED.validTransitions.add(RETURN_REQUESTED);
        DELIVERED.validTransitions.add(CLOSED);

        RETURN_REQUESTED.validTransitions.add(RETURN_REJECTED);
        RETURN_REQUESTED.validTransitions.add(RETURN_APPROVED);

        RETURN_REJECTED.validTransitions.add(CLOSED);

        RETURN_APPROVED.validTransitions.add(PICK_UP_INITIATED);

        PICK_UP_INITIATED.validTransitions.add(PICK_UP_COMPLETED);

        PICK_UP_COMPLETED.validTransitions.add(REFUND_INITIATED);

        REFUND_INITIATED.validTransitions.add(REFUND_COMPLETED);

        REFUND_COMPLETED.validTransitions.add(CLOSED);
    }

    private final Set<OrderStatusType> validTransitions = new HashSet<>();

    public boolean canTransitionTo(OrderStatusType targetStatus) {
        return validTransitions.contains(targetStatus);
    }

}
