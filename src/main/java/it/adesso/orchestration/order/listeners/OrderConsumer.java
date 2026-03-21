package it.adesso.orchestration.order.listeners;

import com.google.gson.Gson;
import it.adesso.orchestration.order.DTO.ReceivedEventDTO;
import it.adesso.orchestration.order.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class OrderConsumer {
    private final WorkflowService workflowService;
    private final Gson gson;

    @Bean
    public Consumer<String> orderOrchestrationListener() {
        return order -> {
            ReceivedEventDTO receivedEventDTO;
            try {
                receivedEventDTO = this.gson.fromJson(order, ReceivedEventDTO.class);
            } catch (Exception e) {
                log.error("Error parsing order: {}", e.getMessage());
                return;
            }
            this.workflowService.playWorkflow(receivedEventDTO.getOrderId(), receivedEventDTO.getEvent());
            log.info("Received event for order: {} with event: {}", receivedEventDTO.getOrderId(), receivedEventDTO.getEvent());
        };
    }

    @Bean
    public Consumer<String> orderOrchestrationListenerError() {
        return order -> {
            ReceivedEventDTO receivedEventDTO;
            try {
                receivedEventDTO = this.gson.fromJson(order, ReceivedEventDTO.class);
            } catch (Exception e) {
                log.error("Error parsing order: " + e.getMessage());
                return;
            }
            this.workflowService.rollbackWorkflow(receivedEventDTO.getOrderId());
            log.info("Received error event for order: {}", receivedEventDTO.getOrderId());
        };
    }
}
