package it.adesso.orchestration.order.service.impl;

import it.adesso.orchestration.order.DTO.SendingEventDTO;
import it.adesso.orchestration.order.enums.ActionEventEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceImplTests {

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private QueueServiceImpl queueService;

    private SendingEventDTO sendingEventDTO;

    @BeforeEach
    void setUp() {
        sendingEventDTO = SendingEventDTO.builder()
            .orderId(1L)
            .event(ActionEventEnum.CREATE)
            .message("Test message")
            .build();
    }

    @Test
    void testSendMessage_Success() {
        String bindingName = "orderManagementService-out-0";
        when(streamBridge.send(bindingName, sendingEventDTO)).thenReturn(true);

        queueService.sendMessage(bindingName, sendingEventDTO);

        verify(streamBridge, times(1)).send(bindingName, sendingEventDTO);
    }

    @Test
    void testSendMessage_PaymentService() {
        String bindingName = "paymentService-out-0";
        when(streamBridge.send(bindingName, sendingEventDTO)).thenReturn(true);

        queueService.sendMessage(bindingName, sendingEventDTO);

        verify(streamBridge, times(1)).send(bindingName, sendingEventDTO);
    }

    @Test
    void testSendMessage_Failure() {
        String bindingName = "orderManagementService-out-0";
        when(streamBridge.send(anyString(), any(SendingEventDTO.class)))
            .thenThrow(new RuntimeException("Queue error"));

        assertThrows(RuntimeException.class, () -> queueService.sendMessage(bindingName, sendingEventDTO));
        verify(streamBridge, times(1)).send(bindingName, sendingEventDTO);
    }

    @Test
    void testSendMessage_WithDifferentEventTypes() {
        String bindingName = "orderManagementService-out-0";
        sendingEventDTO.setEvent(ActionEventEnum.UPDATE);
        when(streamBridge.send(bindingName, sendingEventDTO)).thenReturn(true);

        queueService.sendMessage(bindingName, sendingEventDTO);

        verify(streamBridge, times(1)).send(bindingName, sendingEventDTO);
    }

    @Test
    void testSendMessage_WithRefundEvent() {
        String bindingName = "paymentService-out-0";
        sendingEventDTO.setEvent(ActionEventEnum.REFUND);
        when(streamBridge.send(bindingName, sendingEventDTO)).thenReturn(true);

        queueService.sendMessage(bindingName, sendingEventDTO);

        verify(streamBridge, times(1)).send(bindingName, sendingEventDTO);
    }

    @Test
    void testSendMessage_MultipleMessages() {
        String bindingName = "orderManagementService-out-0";
        when(streamBridge.send(anyString(), any(SendingEventDTO.class))).thenReturn(true);

        SendingEventDTO event1 = SendingEventDTO.builder().orderId(1L).event(ActionEventEnum.CREATE).build();
        SendingEventDTO event2 = SendingEventDTO.builder().orderId(2L).event(ActionEventEnum.UPDATE).build();

        queueService.sendMessage(bindingName, event1);
        queueService.sendMessage(bindingName, event2);

        verify(streamBridge, times(2)).send(anyString(), any(SendingEventDTO.class));
    }

    @Test
    void testSendMessage_False() {
        String bindingName = "orderManagementService-out-0";
        when(streamBridge.send(bindingName, sendingEventDTO)).thenReturn(false);

        queueService.sendMessage(bindingName, sendingEventDTO);

        verify(streamBridge, times(1)).send(bindingName, sendingEventDTO);
    }
}

