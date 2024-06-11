package com.example.rosaceae.dto.Response.OrderResponse;

import com.example.rosaceae.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private String status;
    private Order order;
}
