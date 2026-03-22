package it.adesso.orchestration.order.utils;

import it.adesso.orchestration.order.enums.ActionEventEnum;
import it.adesso.orchestration.order.enums.OrderReceivedEventEnum;
import it.adesso.orchestration.order.enums.OrderStatusEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrdersUtilsTests {

    @Test
    void testCheckStatusAndEvent_ReturnsTrue() {
        boolean result = OrdersUtils.checkStatusAndEvent(OrderStatusEnum.ACQUIRED, OrderReceivedEventEnum.CREATED);
        assertTrue(result);
    }

    @Test
    void testCheckStatusAndEvent_AcquiredStatus() {
        boolean result = OrdersUtils.checkStatusAndEvent(OrderStatusEnum.ACQUIRED, OrderReceivedEventEnum.CREATED);
        assertTrue(result);
    }

    @Test
    void testCheckStatusAndEvent_UnderPaymentStatus() {
        boolean result = OrdersUtils.checkStatusAndEvent(OrderStatusEnum.UNDER_PAYMENT, OrderReceivedEventEnum.PAYED);
        assertTrue(result);
    }

    @Test
    void testCheckStatusAndEvent_PayedStatus() {
        boolean result = OrdersUtils.checkStatusAndEvent(OrderStatusEnum.PAYED, OrderReceivedEventEnum.CHARGED);
        assertTrue(result);
    }

    @Test
    void testCheckStatusAndEvent_DeletedStatus() {
        boolean result = OrdersUtils.checkStatusAndEvent(OrderStatusEnum.DELETED, OrderReceivedEventEnum.DELETED);
        assertTrue(result);
    }

    @Test
    void testCheckStatusAndAction_Create() {
        boolean result = OrdersUtils.checkStatusAndAction(OrderStatusEnum.ACQUIRED, ActionEventEnum.CREATE);
        assertTrue(result);
    }

    @Test
    void testCheckStatusAndAction_Update() {
        boolean result = OrdersUtils.checkStatusAndAction(OrderStatusEnum.ACQUIRED, ActionEventEnum.UPDATE);
        assertTrue(result);
    }

    @Test
    void testCheckStatusAndAction_Delete() {
        boolean result = OrdersUtils.checkStatusAndAction(OrderStatusEnum.ACQUIRED, ActionEventEnum.DELETE);
        assertTrue(result);
    }

    @Test
    void testCheckStatusAndAction_Pay() {
        boolean result = OrdersUtils.checkStatusAndAction(OrderStatusEnum.UNDER_PAYMENT, ActionEventEnum.PAY);
        assertTrue(result);
    }

    @Test
    void testCheckStatusAndAction_Refund() {
        boolean result = OrdersUtils.checkStatusAndAction(OrderStatusEnum.PAYED, ActionEventEnum.REFUND);
        assertTrue(result);
    }

    @Test
    void testCheckStatusAndEvent_ChargedStatus() {
        boolean result = OrdersUtils.checkStatusAndEvent(OrderStatusEnum.CHARGED, OrderReceivedEventEnum.COMPLETED);
        assertTrue(result);
    }

    @Test
    void testCheckStatusAndAction_UpdatePayed() {
        boolean result = OrdersUtils.checkStatusAndAction(OrderStatusEnum.PAYED, ActionEventEnum.UPDATE_PAYED);
        assertTrue(result);
    }
}

