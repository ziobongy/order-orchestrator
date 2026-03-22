package it.adesso.orchestration.order.utils;

import it.adesso.orchestration.order.enums.ActionEventEnum;
import it.adesso.orchestration.order.enums.OrderReceivedEventEnum;
import it.adesso.orchestration.order.enums.OrderStatusEnum;

public class OrdersUtils {
    public static boolean checkStatusAndEvent(OrderStatusEnum status, OrderReceivedEventEnum event) {
        return switch (status) {
            case DELETED, COMPLETED -> false;
            case CHARGED -> event.equals(OrderReceivedEventEnum.COMPLETED);
            case PAYED -> event.equals(OrderReceivedEventEnum.CHARGED) || event.equals(OrderReceivedEventEnum.DELETED);
            case UNDER_PAYMENT -> event.equals(OrderReceivedEventEnum.PAYED) ||
                    event.equals(OrderReceivedEventEnum.EDITED) ||
                    event.equals(OrderReceivedEventEnum.DELETED);
            case ACQUIRED -> event.equals(OrderReceivedEventEnum.CREATED) ||
                    event.equals(OrderReceivedEventEnum.EDITED) ||
                    event.equals(OrderReceivedEventEnum.DELETED);
            case UNDER_EDITING -> event.equals(OrderReceivedEventEnum.EDITED) ||
                    event.equals(OrderReceivedEventEnum.DELETED);
        };
    }
    public static boolean checkStatusAndAction(OrderStatusEnum status, ActionEventEnum action) {
        return switch (status) {
            case DELETED, COMPLETED, CHARGED -> false;
            case PAYED -> action.equals(ActionEventEnum.UPDATE) || action.equals(ActionEventEnum.DELETE);
            case UNDER_PAYMENT -> action.equals(ActionEventEnum.UPDATE) || action.equals(ActionEventEnum.DELETE);
            case ACQUIRED -> action.equals(ActionEventEnum.UPDATE) ||
                    action.equals(ActionEventEnum.DELETE);
            case UNDER_EDITING -> action.equals(ActionEventEnum.UPDATE) || action.equals(ActionEventEnum.DELETE);
        };
    }
}
