package it.adesso.orchestration.order.service.impl;

import it.adesso.orchestration.order.DTO.OrderDTO;
import it.adesso.orchestration.order.DTO.ReceivedEventDTO;
import it.adesso.orchestration.order.DTO.SendingEventDTO;
import it.adesso.orchestration.order.entities.Order;
import it.adesso.orchestration.order.enums.ActionEventEnum;
import it.adesso.orchestration.order.enums.OrderReceivedEventEnum;
import it.adesso.orchestration.order.enums.OrderStatusEnum;
import it.adesso.orchestration.order.service.JwtUtilService;
import it.adesso.orchestration.order.service.OrderService;
import it.adesso.orchestration.order.service.QueueService;
import it.adesso.orchestration.order.service.WorkflowService;
import it.adesso.orchestration.order.utils.OrdersUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final OrderService orderService;
    private final QueueService queueService;

    @Override
    public void playWorkflow(Long orderId, OrderReceivedEventEnum event) {
        Order order = this.orderService.findOrderById(orderId);
        OrderStatusEnum orderStatusEnum;
        try {
            orderStatusEnum = OrderStatusEnum.valueOf(order.getStatus());
        } catch (IllegalArgumentException e) {
            log.error("Invalid order status: " + order.getStatus());
            return;
        }
        // devo anzitutto capire se l'ordine è in uno stato corretto per ricevere questo aggiornamento
        if (!OrdersUtils.checkStatusAndEvent(orderStatusEnum, event)) {
            log.error("Invalid event {} for order status {}", event, orderStatusEnum);
            return;
        }
        // siamo in uno stato corretto procediamo
        switch (event) {
            case CREATED: {
                this.orderService.saveOrderStatus(order, OrderStatusEnum.UNDER_PAYMENT);
                // ora devo notificare il servizio di pagamento
                this.queueService.sendMessage(
                    "paymentService-out-0",
                    SendingEventDTO.builder()
                        .orderId(orderId)
                        .event(ActionEventEnum.PAY)
                        .build()
                );
                break;
            }
            case PAYED: {
                this.orderService.saveOrderStatus(order, OrderStatusEnum.PAYED);
                // ora devo notificare il servizio di ordini
                this.queueService.sendMessage(
                    "orderManagementService-out-0",
                    SendingEventDTO.builder()
                        .orderId(orderId)
                        .event(ActionEventEnum.UPDATE_PAYED)
                        .build()
                );
                break;
            }
            case DELETED: {
                this.orderService.saveOrderStatus(order, OrderStatusEnum.DELETED);
                break;
            }
            case EDITED: {
                this.orderService.saveOrderStatus(order, OrderStatusEnum.UNDER_PAYMENT);
                // ora devo notificare il servizio di pagamento
                this.queueService.sendMessage(
                    "paymentService-out-0",
                    SendingEventDTO.builder()
                        .orderId(orderId)
                        .event(ActionEventEnum.PAY)
                        .build()
                );
                break;
            }
            case CHARGED: {
                this.orderService.saveOrderStatus(order, OrderStatusEnum.CHARGED);
                break;
            }
            case COMPLETED: {
                this.orderService.saveOrderStatus(order, OrderStatusEnum.COMPLETED);
                break;
            }
        }
    }

    @Override
    public void rollbackWorkflow(Long orderId) {
        log.info("Rolling back workflow for order: {}", orderId);
    }


}
