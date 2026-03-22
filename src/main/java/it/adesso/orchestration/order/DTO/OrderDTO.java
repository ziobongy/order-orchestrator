package it.adesso.orchestration.order.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private Long id;
    private List<OrderEntryDTO> entries;
}
