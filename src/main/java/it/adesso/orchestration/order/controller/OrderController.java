package it.adesso.orchestration.order.controller;


import it.adesso.orchestration.order.DTO.OrderDTO;
import it.adesso.orchestration.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> placeOrder(
        @RequestBody OrderDTO orderCreationDTO
    ) {
        this.orderService.placeOrder(
            orderCreationDTO
        );
        return ResponseEntity.accepted().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> editOrder(
        @PathVariable Long id,
        @RequestBody OrderDTO orderCreationDTO
    ) {
        this.orderService.editOrder(
            id,
            orderCreationDTO
        );
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteOrder(
        @PathVariable Long id
    ) {
        this.orderService.cancelOrder(id);
        return ResponseEntity.accepted().build();
    }
}
