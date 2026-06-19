package com.comdao.api.order.entities.enums;

public enum State {
    PAYING(0),
    CONFIRMED(1),
    DELIVERING(2),
    FINISHED(3),
    CANCELLED(99);

    private final int statusPriority;

    State(int statusPriority) {
        this.statusPriority = statusPriority;
    }

    public int getStatusPriority() {
        return statusPriority;
    }
}
