package ar.edu.iw3.util;

import ar.edu.iw3.model.Alarm;
import ar.edu.iw3.model.Order;

public final class OrderUtils {
    // Metodo utilitario para serializador
    public static String getAlarmStatus(Order order) {
        if (order.getAlarms() == null || order.getAlarms().isEmpty()) {
            return "Sin alarmas";
        }

        boolean hasPendingReview = false;
        boolean hasConfirmedIssue = false;

        for (Alarm alarm : order.getAlarms()) {
            switch (alarm.getStatus()) {
                case PENDING_REVIEW:
                    hasPendingReview = true;
                    break;
                case CONFIRMED_ISSUE:
                    hasConfirmedIssue = true;
                    break;
                default:
                    break;
            }
        }

        if (hasConfirmedIssue) {
            return "Problema";
        }
        if (hasPendingReview) {
            return "Pendiente";
        }
        return "Sin alarmas";
    }
}
