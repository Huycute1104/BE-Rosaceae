package com.example.rosaceae.controller;

import com.example.rosaceae.dto.Data.OrderDTO;
import com.example.rosaceae.dto.Data.OrderDetailDTO;
import com.example.rosaceae.dto.Request.OrderDetailRequest.OrderDetailRequest;
import com.example.rosaceae.dto.Request.OrderRequest.CreateOrderRequest;
import com.example.rosaceae.dto.Response.OrderDetailResponse.OrderDetailResponse;
import com.example.rosaceae.dto.Response.OrderResponse.*;
import com.example.rosaceae.enums.OrderStatus;
import com.example.rosaceae.service.OrderDetailService;
import com.example.rosaceae.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService  orderDetailService;

    @PostMapping("")
//    @PreAuthorize("hasAuthority('customer:create')")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request ){
        return ResponseEntity.ok(orderService.createOrderWithDetails(request));
    }

    @PostMapping("/orderdetail")
//    @PreAuthorize("hasAuthority('customer:create')")
    public ResponseEntity<OrderDetailResponse> createOrderDetail(@RequestBody OrderDetailRequest request ){
        return ResponseEntity.ok(orderDetailService.createOrderDetail(request));
    }

    @GetMapping("/customer/{id}")
    public Page<OrderDTO> getOrdersForCustomer(
            @PathVariable int id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return orderService.getOrderForCustomer(id, pageable);
    }
    @GetMapping("/shop/{userId}")
    public Page<OrderDetailDTO> getOrderDetailsByItemUserId(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return orderService.getOrderDetailsByItemUserId(userId, pageable);
    }
    @GetMapping("/details")
    public Page<OrderDetailDTO> getAllOrderDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return orderService.getAllOrderDetails(pageable);
    }

    @GetMapping("/orderCount/{shopId}")
    public long countOrdersByUserId(@PathVariable int shopId) {
        return orderService.countOrdersByUserId(shopId);
    }
    @GetMapping("/countByOrderStatus")
    public long countOrdersByOrderStatus(@RequestParam OrderStatus orderStatus) {
        return orderService.countOrdersByOrderStatus(orderStatus);
    }
    @GetMapping("/countOrderByStatusForShop")
    public long countOrdersByOrderStatusAndShopOwnerId(@RequestParam OrderStatus orderStatus, @RequestParam int shopOwnerId) {
        return orderService.countOrdersByOrderStatusAndShopOwnerId(orderStatus, shopOwnerId);
    }
//    @GetMapping("/{userId}/total-price-for-shop")
//    public TotalPriceForShopResponse getTotalPriceForShopByUserId(@PathVariable int userId) {
//        return orderService.getTotalPriceForShopByUserId(userId);
//    }
@GetMapping("/total-price-for-shop")
public TotalPriceForShopResponse getTotalPriceForShopByUserId(
        @RequestParam int userId,
        @RequestParam int month,
        @RequestParam int year) {
    return orderService.getTotalPriceForShopByUserId(userId, month, year);
}
    @GetMapping("/total-price-for-shop2")
    public TotalPriceForShopResponse getTotalPriceForShopByUserId2(
            @RequestParam int userId,
            @RequestParam int month,
            @RequestParam int year) {
        return orderService.getTotalPriceForShopByUserId2(userId, month, year);
    }
    @GetMapping("/total-price-for-admin")
    public TotalPriceForAdminResponse getTotalPriceForAdmin(
            @RequestParam int month,
            @RequestParam int year) {
        return orderService.getTotalPriceForAdmin(month, year);
    }
    @GetMapping("/order-count-by-day")
    public ResponseEntity<List<DailyOrderCountResponse>> getOrderCountByShopAndMonthAndYear(
            @RequestParam int userId,
            @RequestParam int month,
            @RequestParam int year) {
        List<DailyOrderCountResponse> orderCounts = orderService.getOrderCountByShopAndMonthAndYear(userId, month, year);
        return ResponseEntity.ok(orderCounts);
    }
    @GetMapping("/order-count-by-day-for-Admin")
    public ResponseEntity<List<DailyOrderCountResponse>> getOrderCountByMonthAndYearForAdmin(
            @RequestParam int month,
            @RequestParam int year) {
        List<DailyOrderCountResponse> orderCounts = orderService.getOrderCountByMonthAndYearForAdmin(month, year);
        return ResponseEntity.ok(orderCounts);
    }
    @GetMapping("/daily-price-for-shop")
    public ResponseEntity<List<DailyPriceForShopResponse>> getDailyPriceForShop(
            @RequestParam int userId,
            @RequestParam int month,
            @RequestParam int year) {
        List<DailyPriceForShopResponse> dailyPrices = orderService.getDailyPriceForShopByUserId(userId, month, year);
        return ResponseEntity.ok(dailyPrices);
    }
    @GetMapping("/daily-price-for-admin")
    public ResponseEntity<List<DailyPriceForAdminResponse>> getDailyPriceForAdmin(
            @RequestParam int month,
            @RequestParam int year) {
        List<DailyPriceForAdminResponse> dailyPrices = orderService.getDailyPriceForAdmin(month, year);
        return ResponseEntity.ok(dailyPrices);
    }
    @GetMapping("/completed-order-count-by-day-forAdmin")
    public ResponseEntity<List<DailyOrderCountResponse>> getCompletedOrderCountByDayWithItemType(
            @RequestParam int month,
            @RequestParam int year) {
        List<DailyOrderCountResponse> orderCounts = orderService.getCompletedOrderCountByDayWithItemType(month, year);
        return ResponseEntity.ok(orderCounts);
    }
    @GetMapping("/completed-order-count-by-day-forShop")
    public ResponseEntity<List<DailyOrderCountResponse>> getCompletedOrderCountByDayForShop(
            @RequestParam int userId,
            @RequestParam int month,
            @RequestParam int year) {
        List<DailyOrderCountResponse> orderCounts = orderService.getCompletedOrderCountByDayForShop(userId, month, year);
        return ResponseEntity.ok(orderCounts);
    }
    @GetMapping("/total-price-by-day-onlyService-forAdmin")
    public ResponseEntity<List<DailyPriceForAdminResponse>> getTotalPriceByDayWithItemType(
            @RequestParam int month,
            @RequestParam int year) {
        List<DailyPriceForAdminResponse> totalPrices = orderService.getTotalPriceByDayWithItemType(month, year);
        return ResponseEntity.ok(totalPrices);
    }
    @GetMapping("/total-price-by-day-onlyService-forShop")
    public ResponseEntity<List<DailyPriceForShopResponse>> getTotalPriceByDayForShop(
            @RequestParam int userId,
            @RequestParam int month,
            @RequestParam int year) {
        List<DailyPriceForShopResponse> totalPrices = orderService.getTotalPriceByDayForShop(userId, month, year);
        return ResponseEntity.ok(totalPrices);
    }

}
