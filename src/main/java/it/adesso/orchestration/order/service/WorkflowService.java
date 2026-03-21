package it.adesso.orchestration.order.service;

import it.adesso.orchestration.order.DTO.OrderDTO;
import it.adesso.orchestration.order.enums.OrderReceivedEventEnum;

public interface WorkflowService {
    void playWorkflow(Long orderId, OrderReceivedEventEnum event);
    void rollbackWorkflow(Long orderId);
}
