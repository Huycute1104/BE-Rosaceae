package com.example.rosaceae.dto.Request.OrderRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    private float total;
    private int voucherId;
    private int customerId;
    private String customerPhone;
    private String customerAddress;
    private String customerName;
    private List<OrderItemRequest> items;

    @Data
    @Builder
    public static class OrderItemRequest {
        private int itemId;
        private int quantity;
    }
}
