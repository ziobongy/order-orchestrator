package it.adesso.orchestration.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {
    DELETED("DELETED"),
    COMPLETED("COMPLETED"),
    CHARGED("CHARGED"),
    PAYED("PAYED"),
    UNDER_PAYMENT("UNDER_PAYMENT"),
    ACQUIRED("ACQUIRED"),
    UNDER_EDITING("UNDER_EDITING");

    private final String name;
}
