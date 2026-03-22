package it.adesso.orchestration.order.service.impl;

import it.adesso.orchestration.order.DTO.OrderDTO;
import it.adesso.orchestration.order.DTO.SendingEventDTO;
import it.adesso.orchestration.order.entities.Order;
import it.adesso.orchestration.order.enums.ActionEventEnum;
import it.adesso.orchestration.order.enums.OrderStatusEnum;
import it.adesso.orchestration.order.mappers.OrderMapping;
import it.adesso.orchestration.order.repositories.OrderRepository;
import it.adesso.orchestration.order.service.OrderService;
import it.adesso.orchestration.order.service.QueueService;
import it.adesso.orchestration.order.service.WorkflowService;
import it.adesso.orchestration.order.utils.OrdersUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private static final String ORDERS_BINDING_NAME = "orderManagementService-out-0";
    private static final String ORDERS_ERROR_BINDING_NAME = "orderManagementError-out-0";

    private final OrderMapping orderMapping;
    private final OrderRepository orderRepository;
    private final QueueService queueService;

    @Override
    public void placeOrder(OrderDTO orderCreationDTO) {
        Order orderEntity = orderMapping.toEntity(orderCreationDTO);
        orderEntity.setStatus(OrderStatusEnum.ACQUIRED.getName());
        this.orderRepository.save(orderEntity);
        this.queueService.sendMessage(
            ORDERS_BINDING_NAME,
            SendingEventDTO.builder()
                    .orderId(orderEntity.getId())
                    .event(ActionEventEnum.CREATE)
                    .order(orderCreationDTO)
                    .build()
        );
    }

    @Override
    public void editOrder(Long id, OrderDTO orderDTO) {
        Order orderEntity = this.orderRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Order not found")
        );
        if (!OrdersUtils.checkStatusAndAction(OrderStatusEnum.valueOf(orderEntity.getStatus()), ActionEventEnum.UPDATE)) {
            this.queueService.sendMessage(
                ORDERS_ERROR_BINDING_NAME,
                SendingEventDTO.builder()
                    .orderId(id)
                    .message("Order cannot be edited in its current status: status " + orderEntity.getStatus() + " - action " + ActionEventEnum.UPDATE.name())
                    .build()
            );
            throw new RuntimeException("Order cannot be edited in its current status");
        }
        orderEntity.setStatus(OrderStatusEnum.UNDER_EDITING.getName());
        this.orderRepository.save(orderEntity);
        this.queueService.sendMessage(
            ORDERS_BINDING_NAME,
            SendingEventDTO.builder()
                .orderId(id)
                .event(ActionEventEnum.UPDATE)
                .order(orderDTO)
                .build()
        );
    }

    @Override
    public void cancelOrder(Long id) {
        Order orderEntity = this.orderRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Order not found")
        );
        if (!OrdersUtils.checkStatusAndAction(OrderStatusEnum.valueOf(orderEntity.getStatus()), ActionEventEnum.DELETE)) {
            this.queueService.sendMessage(
                ORDERS_ERROR_BINDING_NAME,
                SendingEventDTO.builder()
                    .orderId(id)
                    .message("Order cannot be edited in its current status: status " + orderEntity.getStatus() + " - action " + ActionEventEnum.DELETE.name())
                    .build()
            );
            throw new RuntimeException("Order cannot be edited in its current status");
        }
        this.saveOrderStatus(orderEntity, OrderStatusEnum.DELETED);
        this.queueService.sendMessage(
            ORDERS_BINDING_NAME,
            SendingEventDTO.builder()
                .orderId(id)
                .event(ActionEventEnum.DELETE)
                .build()
        );
        this.queueService.sendMessage(
            "paymentService-out-0",
            SendingEventDTO.builder()
                .orderId(id)
                .event(ActionEventEnum.REFUND)
                .build()
        );
    }

    @Override
    public Order findOrderById(Long orderId) {
        return this.orderRepository.findById(orderId).orElseThrow(
            () -> new RuntimeException("Order not found")
        );
    }

    @Override
    public Order saveOrderStatus(Order order, OrderStatusEnum status) {
        order.setStatus(status.getName());
        return this.orderRepository.save(order);
    }

}
