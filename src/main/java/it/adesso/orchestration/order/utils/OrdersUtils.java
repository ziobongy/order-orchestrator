package it.adesso.orchestration.order.utils;

import it.adesso.orchestration.order.enums.ActionEventEnum;
import it.adesso.orchestration.order.enums.OrderReceivedEventEnum;
import it.adesso.orchestration.order.enums.OrderStatusEnum;

public class OrdersUtils {
    public static boolean checkStatusAndEvent(OrderStatusEnum status, OrderReceivedEventEnum event) {
        return true;
    }
    public static boolean checkStatusAndAction(OrderStatusEnum status, ActionEventEnum action) {
        return true;
    }
}
