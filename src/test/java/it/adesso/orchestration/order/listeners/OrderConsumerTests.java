package it.adesso.orchestration.order.listeners;

import com.google.gson.Gson;
import it.adesso.orchestration.order.DTO.ReceivedEventDTO;
import it.adesso.orchestration.order.enums.OrderReceivedEventEnum;
import it.adesso.orchestration.order.service.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderConsumerTests {

    @Mock
    private WorkflowService workflowService;

    @Mock
    private Gson gson;

    @InjectMocks
    private OrderConsumer orderConsumer;

    private Consumer<String> orderOrchestrationListener;
    private Consumer<String> orderOrchestrationListenerError;

    @BeforeEach
    void setUp() {
        orderOrchestrationListener = orderConsumer.orderOrchestrationListener();
        orderOrchestrationListenerError = orderConsumer.orderOrchestrationListenerError();
    }

    @Test
    void testOrderOrchestrationListener_Success() {
        String jsonInput = "{\"orderId\": 1, \"event\": \"CREATED\"}";
        ReceivedEventDTO receivedEventDTO = ReceivedEventDTO.builder()
            .orderId(1L)
            .event(OrderReceivedEventEnum.CREATED)
            .build();

        when(gson.fromJson(jsonInput, ReceivedEventDTO.class)).thenReturn(receivedEventDTO);
        doNothing().when(workflowService).playWorkflow(anyLong(), eq(OrderReceivedEventEnum.CREATED));

        orderOrchestrationListener.accept(jsonInput);

        verify(gson, times(1)).fromJson(jsonInput, ReceivedEventDTO.class);
        verify(workflowService, times(1)).playWorkflow(1L, OrderReceivedEventEnum.CREATED);
    }

    @Test
    void testOrderOrchestrationListener_ParseError() {
        String invalidJson = "invalid json";

        when(gson.fromJson(invalidJson, ReceivedEventDTO.class)).thenThrow(new RuntimeException("Parse error"));

        orderOrchestrationListener.accept(invalidJson);

        verify(gson, times(1)).fromJson(invalidJson, ReceivedEventDTO.class);
        verify(workflowService, never()).playWorkflow(anyLong(), any());
    }

    @Test
    void testOrderOrchestrationListener_CreatedEvent() {
        String jsonInput = "{\"orderId\": 1, \"event\": \"CREATED\"}";
        ReceivedEventDTO receivedEventDTO = ReceivedEventDTO.builder()
            .orderId(1L)
            .event(OrderReceivedEventEnum.CREATED)
            .build();

        when(gson.fromJson(jsonInput, ReceivedEventDTO.class)).thenReturn(receivedEventDTO);
        doNothing().when(workflowService).playWorkflow(1L, OrderReceivedEventEnum.CREATED);

        orderOrchestrationListener.accept(jsonInput);

        ArgumentCaptor<Long> orderIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<OrderReceivedEventEnum> eventCaptor = ArgumentCaptor.forClass(OrderReceivedEventEnum.class);
        verify(workflowService).playWorkflow(orderIdCaptor.capture(), eventCaptor.capture());
        assertEquals(1L, orderIdCaptor.getValue());
        assertEquals(OrderReceivedEventEnum.CREATED, eventCaptor.getValue());
    }

    @Test
    void testOrderOrchestrationListener_PayedEvent() {
        String jsonInput = "{\"orderId\": 2, \"event\": \"PAYED\"}";
        ReceivedEventDTO receivedEventDTO = ReceivedEventDTO.builder()
            .orderId(2L)
            .event(OrderReceivedEventEnum.PAYED)
            .build();

        when(gson.fromJson(jsonInput, ReceivedEventDTO.class)).thenReturn(receivedEventDTO);
        doNothing().when(workflowService).playWorkflow(2L, OrderReceivedEventEnum.PAYED);

        orderOrchestrationListener.accept(jsonInput);

        verify(workflowService, times(1)).playWorkflow(2L, OrderReceivedEventEnum.PAYED);
    }

    @Test
    void testOrderOrchestrationListenerError_Success() {
        String jsonInput = "{\"orderId\": 1, \"event\": \"ERROR\"}";
        ReceivedEventDTO receivedEventDTO = ReceivedEventDTO.builder()
            .orderId(1L)
            .build();

        when(gson.fromJson(jsonInput, ReceivedEventDTO.class)).thenReturn(receivedEventDTO);
        doNothing().when(workflowService).rollbackWorkflow(1L);

        orderOrchestrationListenerError.accept(jsonInput);

        verify(gson, times(1)).fromJson(jsonInput, ReceivedEventDTO.class);
        verify(workflowService, times(1)).rollbackWorkflow(1L);
    }

    @Test
    void testOrderOrchestrationListenerError_ParseError() {
        String invalidJson = "invalid json";

        when(gson.fromJson(invalidJson, ReceivedEventDTO.class)).thenThrow(new RuntimeException("Parse error"));

        orderOrchestrationListenerError.accept(invalidJson);

        verify(gson, times(1)).fromJson(invalidJson, ReceivedEventDTO.class);
        verify(workflowService, never()).rollbackWorkflow(anyLong());
    }

    @Test
    void testOrderOrchestrationListener_DeletedEvent() {
        String jsonInput = "{\"orderId\": 3, \"event\": \"DELETED\"}";
        ReceivedEventDTO receivedEventDTO = ReceivedEventDTO.builder()
            .orderId(3L)
            .event(OrderReceivedEventEnum.DELETED)
            .build();

        when(gson.fromJson(jsonInput, ReceivedEventDTO.class)).thenReturn(receivedEventDTO);
        doNothing().when(workflowService).playWorkflow(3L, OrderReceivedEventEnum.DELETED);

        orderOrchestrationListener.accept(jsonInput);

        verify(workflowService, times(1)).playWorkflow(3L, OrderReceivedEventEnum.DELETED);
    }
}

