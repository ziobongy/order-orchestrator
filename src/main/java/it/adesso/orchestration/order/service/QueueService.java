package it.adesso.orchestration.order.service;

import it.adesso.orchestration.order.DTO.SendingEventDTO;

public interface QueueService {
    void sendMessage(String bindingName, SendingEventDTO sendingEventDTO);
}
