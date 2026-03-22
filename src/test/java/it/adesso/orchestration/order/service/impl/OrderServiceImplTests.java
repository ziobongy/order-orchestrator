package it.adesso.orchestration.order.service.impl;

import it.adesso.orchestration.order.DTO.OrderDTO;
import it.adesso.orchestration.order.DTO.OrderEntryDTO;
import it.adesso.orchestration.order.DTO.SendingEventDTO;
import it.adesso.orchestration.order.entities.Order;
import it.adesso.orchestration.order.enums.ActionEventEnum;
import it.adesso.orchestration.order.enums.OrderStatusEnum;
import it.adesso.orchestration.order.mappers.OrderMapping;
import it.adesso.orchestration.order.repositories.OrderRepository;
import it.adesso.orchestration.order.service.QueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTests {

    @Mock
    private OrderMapping orderMapping;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private QueueService queueService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderDTO orderDTO;
    private Order order;

    @BeforeEach
    void setUp() {
        OrderEntryDTO orderEntryDTO = new OrderEntryDTO();
        orderEntryDTO.setId(1L);
        orderEntryDTO.setOrderId(1L);
        orderEntryDTO.setIdPizza(1L);
        orderEntryDTO.setIdBase(1L);
        orderEntryDTO.setAddedIngredients(Set.of(1L, 2L));
        orderEntryDTO.setRemovedIngredients(Set.of());

        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setEntries(List.of(orderEntryDTO));

        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatusEnum.ACQUIRED.getName());
        Map<String, Object> orderModel = new HashMap<>();
        orderModel.put("id", 1L);
        order.setOrder(orderModel);
    }

    @Test
    void testPlaceOrder_Success() {
        when(orderMapping.toEntity(orderDTO)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(queueService).sendMessage(anyString(), any(SendingEventDTO.class));

        orderService.placeOrder(orderDTO);

        verify(orderMapping, times(1)).toEntity(orderDTO);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(queueService, times(1)).sendMessage(eq("orderManagementService-out-0"), any(SendingEventDTO.class));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(OrderStatusEnum.ACQUIRED.getName(), orderCaptor.getValue().getStatus());
    }

    @Test
    void testEditOrder_Success() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(queueService).sendMessage(anyString(), any(SendingEventDTO.class));

        orderService.editOrder(orderId, orderDTO);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(queueService, times(1)).sendMessage(eq("orderManagementService-out-0"), any(SendingEventDTO.class));
    }

    @Test
    void testEditOrder_NotFound() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.editOrder(orderId, orderDTO));
        verify(orderRepository, times(1)).findById(orderId);
        verify(queueService, never()).sendMessage(anyString(), any());
    }

    @Test
    void testCancelOrder_Success() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(queueService).sendMessage(anyString(), any(SendingEventDTO.class));

        orderService.cancelOrder(orderId);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(queueService, times(2)).sendMessage(anyString(), any(SendingEventDTO.class));
    }

    @Test
    void testCancelOrder_NotFound() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.cancelOrder(orderId));
        verify(orderRepository, times(1)).findById(orderId);
        verify(queueService, never()).sendMessage(anyString(), any());
    }

    @Test
    void testFindOrderById_Success() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.findOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testFindOrderById_NotFound() {
        Long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.findOrderById(orderId));
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testSaveOrderStatus_Success() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.saveOrderStatus(order, OrderStatusEnum.PAYED);

        assertNotNull(result);
        assertEquals(OrderStatusEnum.PAYED.getName(), order.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testPlaceOrder_VerifyQueueMessage() {
        when(orderMapping.toEntity(orderDTO)).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        doNothing().when(queueService).sendMessage(anyString(), any(SendingEventDTO.class));

        orderService.placeOrder(orderDTO);

        ArgumentCaptor<SendingEventDTO> eventCaptor = ArgumentCaptor.forClass(SendingEventDTO.class);
        verify(queueService).sendMessage(eq("orderManagementService-out-0"), eventCaptor.capture());
        SendingEventDTO capturedEvent = eventCaptor.getValue();
        assertEquals(ActionEventEnum.CREATE, capturedEvent.getEvent());
    }
}

