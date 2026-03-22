package it.adesso.orchestration.order.controller;

import it.adesso.orchestration.order.DTO.OrderDTO;
import it.adesso.orchestration.order.DTO.OrderEntryDTO;
import it.adesso.orchestration.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTests {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderDTO orderDTO;
    private OrderEntryDTO orderEntryDTO;

    @BeforeEach
    void setUp() {
        orderEntryDTO = new OrderEntryDTO();
        orderEntryDTO.setId(1L);
        orderEntryDTO.setOrderId(1L);
        orderEntryDTO.setIdPizza(1L);
        orderEntryDTO.setIdBase(1L);
        orderEntryDTO.setAddedIngredients(new HashSet<>(Set.of(1L, 2L)));
        orderEntryDTO.setRemovedIngredients(new HashSet<>());

        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setEntries(List.of(orderEntryDTO));
    }

    @Test
    void testPlaceOrder_Success() {
        doNothing().when(orderService).placeOrder(orderDTO);

        ResponseEntity<Void> response = orderController.placeOrder(orderDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(orderService, times(1)).placeOrder(orderDTO);
    }

    @Test
    void testEditOrder_Success() {
        Long orderId = 1L;
        doNothing().when(orderService).editOrder(orderId, orderDTO);

        ResponseEntity<Void> response = orderController.editOrder(orderId, orderDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(orderService, times(1)).editOrder(orderId, orderDTO);
    }

    @Test
    void testDeleteOrder_Success() {
        Long orderId = 1L;
        doNothing().when(orderService).cancelOrder(orderId);

        ResponseEntity<Void> response = orderController.deleteOrder(orderId);

        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(orderService, times(1)).cancelOrder(orderId);
    }

    @Test
    void testPlaceOrder_Exception() {
        doThrow(new RuntimeException("Service error")).when(orderService).placeOrder(orderDTO);

        assertThrows(RuntimeException.class, () -> orderController.placeOrder(orderDTO));
        verify(orderService, times(1)).placeOrder(orderDTO);
    }

    @Test
    void testEditOrder_Exception() {
        Long orderId = 1L;
        doThrow(new RuntimeException("Service error")).when(orderService).editOrder(orderId, orderDTO);

        assertThrows(RuntimeException.class, () -> orderController.editOrder(orderId, orderDTO));
        verify(orderService, times(1)).editOrder(orderId, orderDTO);
    }

    @Test
    void testDeleteOrder_Exception() {
        Long orderId = 1L;
        doThrow(new RuntimeException("Service error")).when(orderService).cancelOrder(orderId);

        assertThrows(RuntimeException.class, () -> orderController.deleteOrder(orderId));
        verify(orderService, times(1)).cancelOrder(orderId);
    }
}

