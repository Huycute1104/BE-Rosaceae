package com.example.rosaceae.dto.Data;

import com.example.rosaceae.model.Order;
import com.example.rosaceae.model.OrderDetail;

import java.util.Set;
import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderDTO toOrderDTO(Order order) {
        Set<OrderDetailDTO> orderDetailDTOs = order.getOrderDetails().stream()
                .map(OrderMapper::toOrderDetailDTO)
                .collect(Collectors.toSet());

        return OrderDTO.builder()
                .orderId(order.getOrderId())
                .orderDate(order.getOrderDate())
                .total(order.getTotal())
                .orderStatus(order.getOrderStatus().name())
                .orderDetails(orderDetailDTOs)
                .CustomerName(order.getCustomer().getAccountName())
                .build();
    }

    public static OrderDetailDTO toOrderDetailDTO(OrderDetail orderDetail) {
        return OrderDetailDTO.builder()
                .orderDetailId(orderDetail.getOrderDetailId())
                .quantity(orderDetail.getQuantity())
                .price(orderDetail.getPrice())
                .itemId(orderDetail.getItem().getItemId())
                .itemName(orderDetail.getItem().getItemName())
                .CustomerName(orderDetail.getOrder().getCustomer().getAccountName())
                .build();
    }
}
