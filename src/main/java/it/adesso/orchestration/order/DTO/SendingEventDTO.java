package it.adesso.orchestration.order.DTO;

import it.adesso.orchestration.order.enums.ActionEventEnum;
import it.adesso.orchestration.order.enums.OrderReceivedEventEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SendingEventDTO {
    private Long orderId;
    private ActionEventEnum event;
    private OrderDTO order;
    private String message;
}
