package it.adesso.orchestration.order.service;

import it.adesso.orchestration.order.DTO.OrderDTO;
import it.adesso.orchestration.order.entities.Order;
import it.adesso.orchestration.order.enums.OrderStatusEnum;

public interface OrderService {
    void placeOrder(OrderDTO orderDTO);
    void editOrder(Long id, OrderDTO orderDTO);
    void cancelOrder(Long orderId);
    Order findOrderById(Long orderId);
    Order saveOrderStatus(Order order, OrderStatusEnum status);
}
