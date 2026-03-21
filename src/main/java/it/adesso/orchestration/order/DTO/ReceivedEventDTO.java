package it.adesso.orchestration.order.DTO;

import it.adesso.orchestration.order.enums.OrderReceivedEventEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ReceivedEventDTO {
    private Long orderId;
    private OrderReceivedEventEnum event;
}
