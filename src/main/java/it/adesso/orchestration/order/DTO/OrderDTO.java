package it.adesso.orchestration.order.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class OrderDTO {
    private Long id;
    private Long idPizza;
    private Long idBase;
    @NotNull
    private Set<Long> addedIngredients;
    @NotNull
    private Set<Long> removedIngredients;
}
