package it.adesso.orchestration.order.service.impl;

import it.adesso.orchestration.order.DTO.SendingEventDTO;
import it.adesso.orchestration.order.entities.Order;
import it.adesso.orchestration.order.enums.ActionEventEnum;
import it.adesso.orchestration.order.enums.OrderReceivedEventEnum;
import it.adesso.orchestration.order.enums.OrderStatusEnum;
import it.adesso.orchestration.order.service.OrderService;
import it.adesso.orchestration.order.service.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceImplTests {

    @Mock
    private OrderService orderService;

    @Mock
    private QueueService queueService;

    @InjectMocks
    private WorkflowServiceImpl workflowService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatusEnum.ACQUIRED.getName());
        Map<String, Object> orderModel = new HashMap<>();
        orderModel.put("id", 1L);
        order.setOrder(orderModel);
    }

    @Test
    void testPlayWorkflow_Created_Success() {
        Long orderId = 1L;
        order.setStatus(OrderStatusEnum.ACQUIRED.getName());
        when(orderService.findOrderById(orderId)).thenReturn(order);
        when(orderService.saveOrderStatus(any(Order.class), eq(OrderStatusEnum.UNDER_PAYMENT))).thenReturn(order);
        doNothing().when(queueService).sendMessage(anyString(), any(SendingEventDTO.class));

        workflowService.playWorkflow(orderId, OrderReceivedEventEnum.CREATED);

        verify(orderService, times(1)).findOrderById(orderId);
        verify(orderService, times(1)).saveOrderStatus(order, OrderStatusEnum.UNDER_PAYMENT);
        verify(queueService, times(1)).sendMessage(eq("paymentService-out-0"), any(SendingEventDTO.class));
    }

    @Test
    void testPlayWorkflow_Payed_Success() {
        Long orderId = 1L;
        order.setStatus(OrderStatusEnum.UNDER_PAYMENT.getName());
        when(orderService.findOrderById(orderId)).thenReturn(order);
        when(orderService.saveOrderStatus(any(Order.class), eq(OrderStatusEnum.PAYED))).thenReturn(order);
        doNothing().when(queueService).sendMessage(anyString(), any(SendingEventDTO.class));

        workflowService.playWorkflow(orderId, OrderReceivedEventEnum.PAYED);

        verify(orderService, times(1)).findOrderById(orderId);
        verify(orderService, times(1)).saveOrderStatus(order, OrderStatusEnum.PAYED);
        verify(queueService, times(1)).sendMessage(eq("orderManagementService-out-0"), any(SendingEventDTO.class));
    }

    @Test
    void testPlayWorkflow_Deleted_Success() {
        Long orderId = 1L;
        order.setStatus(OrderStatusEnum.ACQUIRED.getName());
        when(orderService.findOrderById(orderId)).thenReturn(order);
        when(orderService.saveOrderStatus(any(Order.class), eq(OrderStatusEnum.DELETED))).thenReturn(order);

        workflowService.playWorkflow(orderId, OrderReceivedEventEnum.DELETED);

        verify(orderService, times(1)).findOrderById(orderId);
        verify(orderService, times(1)).saveOrderStatus(order, OrderStatusEnum.DELETED);
    }

    @Test
    void testPlayWorkflow_Edited_Success() {
        Long orderId = 1L;
        order.setStatus(OrderStatusEnum.UNDER_EDITING.getName());
        when(orderService.findOrderById(orderId)).thenReturn(order);
        when(orderService.saveOrderStatus(any(Order.class), eq(OrderStatusEnum.UNDER_PAYMENT))).thenReturn(order);
        doNothing().when(queueService).sendMessage(anyString(), any(SendingEventDTO.class));

        workflowService.playWorkflow(orderId, OrderReceivedEventEnum.EDITED);

        verify(orderService, times(1)).findOrderById(orderId);
        verify(orderService, times(1)).saveOrderStatus(order, OrderStatusEnum.UNDER_PAYMENT);
        verify(queueService, times(1)).sendMessage(eq("paymentService-out-0"), any(SendingEventDTO.class));
    }

    @Test
    void testPlayWorkflow_Charged_Success() {
        Long orderId = 1L;
        order.setStatus(OrderStatusEnum.PAYED.getName());
        when(orderService.findOrderById(orderId)).thenReturn(order);
        when(orderService.saveOrderStatus(any(Order.class), eq(OrderStatusEnum.CHARGED))).thenReturn(order);

        workflowService.playWorkflow(orderId, OrderReceivedEventEnum.CHARGED);

        verify(orderService, times(1)).findOrderById(orderId);
        verify(orderService, times(1)).saveOrderStatus(order, OrderStatusEnum.CHARGED);
    }

    @Test
    void testPlayWorkflow_Completed_Success() {
        Long orderId = 1L;
        order.setStatus(OrderStatusEnum.CHARGED.getName());
        when(orderService.findOrderById(orderId)).thenReturn(order);
        when(orderService.saveOrderStatus(any(Order.class), eq(OrderStatusEnum.COMPLETED))).thenReturn(order);

        workflowService.playWorkflow(orderId, OrderReceivedEventEnum.COMPLETED);

        verify(orderService, times(1)).findOrderById(orderId);
        verify(orderService, times(1)).saveOrderStatus(order, OrderStatusEnum.COMPLETED);
    }

    @Test
    void testPlayWorkflow_InvalidStatus() {
        Long orderId = 1L;
        order.setStatus("INVALID_STATUS");
        when(orderService.findOrderById(orderId)).thenReturn(order);

        workflowService.playWorkflow(orderId, OrderReceivedEventEnum.CREATED);

        verify(orderService, times(1)).findOrderById(orderId);
        verify(orderService, never()).saveOrderStatus(any(), any());
    }

    @Test
    void testRollbackWorkflow_Success() {
        Long orderId = 1L;

        assertDoesNotThrow(() -> workflowService.rollbackWorkflow(orderId));
    }

    @Test
    void testPlayWorkflow_CreatedQueueMessage() {
        Long orderId = 1L;
        order.setStatus(OrderStatusEnum.ACQUIRED.getName());
        when(orderService.findOrderById(orderId)).thenReturn(order);
        when(orderService.saveOrderStatus(any(Order.class), eq(OrderStatusEnum.UNDER_PAYMENT))).thenReturn(order);
        doNothing().when(queueService).sendMessage(anyString(), any(SendingEventDTO.class));

        workflowService.playWorkflow(orderId, OrderReceivedEventEnum.CREATED);

        ArgumentCaptor<SendingEventDTO> eventCaptor = ArgumentCaptor.forClass(SendingEventDTO.class);
        verify(queueService).sendMessage(eq("paymentService-out-0"), eventCaptor.capture());
        SendingEventDTO capturedEvent = eventCaptor.getValue();
        assertEquals(ActionEventEnum.PAY, capturedEvent.getEvent());
        assertEquals(orderId, capturedEvent.getOrderId());
    }
}

