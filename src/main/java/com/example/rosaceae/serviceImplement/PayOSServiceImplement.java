package com.example.rosaceae.serviceImplement;

import com.example.rosaceae.dto.PayOS.CreatePaymentLinkRequestBody;
import com.example.rosaceae.dto.PayOS.PayOSCancel;
import com.example.rosaceae.dto.PayOS.PayOSSuccess;
import com.example.rosaceae.dto.Request.OrderRequest.CreateOrderRequest;
import com.example.rosaceae.dto.Response.OrderResponse.OrderResponse;
import com.example.rosaceae.enums.Fee;
import com.example.rosaceae.enums.OrderStatus;
import com.example.rosaceae.model.*;
import com.example.rosaceae.repository.*;
import com.example.rosaceae.service.PayOSService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lib.payos.PayOS;
import com.lib.payos.type.ItemData;
import com.lib.payos.type.PaymentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayOSServiceImplement implements PayOSService {

    @Autowired
    private PayOS payOS;
    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private VoucherRepo voucherRepo;
    @Autowired
    private OrderDetailRepo orderDetailRepo;
    @Autowired
    private CartRepo cartRepository;
    @Autowired
    private ItemRepo itemRepo;
    @Autowired
    private ShopPayRepo shopPayRepo;

    @Override
    public ResponseEntity<ObjectNode> createOrderQR(CreatePaymentLinkRequestBody body) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            final String description = body.getDescription();
            final String returnUrl = body.getReturnUrl();
            final String cancelUrl = body.getCancelUrl();
            final int orderID;

            var user = userRepo.findUserByUsersID(body.getCustomerId()).orElse(null);
            if (user == null) {
                response.put("error", -1);
                response.put("message", "User Not Found");
                response.set("data", null);
                return ResponseEntity.status(404).body(response);
            }

            if (body.getItems() == null || body.getItems().isEmpty()) {
                response.put("error", -1);
                response.put("message", "No items provided");
                response.set("data", null);
                return ResponseEntity.status(400).body(response);
            }

            // Calculate total
            float total = body.getTotal();
            if (total <= 0) {
                response.put("error", -1);
                response.put("message", "Total must be a positive value");
                response.set("data", null);
                return ResponseEntity.status(400).body(response);
            }

            // Apply voucher if present
            var voucher = voucherRepo.findVoucherByVoucherId(body.getVoucherId()).orElse(null);
            if (voucher != null) {
                total -= (total * voucher.getValue()) / 100;
            }

            // Create and save order
            Order order = Order.builder()
                    .orderDate(new Date())
                    .total(total)
                    .orderStatus(OrderStatus.PENDING)
                    .customer(user)
                    .customerAddress(body.getCustomerAddress())
                    .customerName(body.getCustomerName())
                    .customerPhone(body.getCustomerPhone())
                    .voucher(voucher)
                    .build();
            orderRepo.save(order);
            orderID = order.getOrderId();
            LocalDateTime orderDate = order.getOrderDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // Lấy tháng và năm
            int month = orderDate.getMonthValue();
            int year = orderDate.getYear();


            // Handle order details
            float fee = Fee.SHOP_FEE.getFee() / 100;
            List<OrderDetail> orderDetails = new ArrayList<>();

            for (CreateOrderRequest.OrderItemRequest itemRequest : body.getItems()) {
                var item = itemRepo.findById(itemRequest.getItemId()).orElse(null);
                if (item == null) {
                    response.put("error", -1);
                    response.put("message", "Item with ID " + itemRequest.getItemId() + " not found");
                    response.set("data", null);
                    return ResponseEntity.status(404).body(response);
                }

                if (item.getQuantity() < itemRequest.getQuantity()) {
                    response.put("error", -1);
                    response.put("message", "Insufficient quantity for item with ID " + itemRequest.getItemId());
                    response.set("data", null);
                    return ResponseEntity.status(400).body(response);
                }

                float discount = (float) item.getDiscount() / 100;
                float itemDiscount = item.getItemPrice() * discount;
                float itemTotal = (item.getItemPrice() - itemDiscount) * itemRequest.getQuantity();
                OrderDetail orderDetail = OrderDetail.builder()
                        .item(item)
                        .quantity(itemRequest.getQuantity())
                        .price(itemTotal)
                        .priceForShop(itemTotal - (itemTotal * fee))
                        .order(order)
                        .build();
                orderDetails.add(orderDetail);
                System.out.println(orderDetail.getPriceForShop());
                int currentBuyCount = (item.getQuantityCount() == null) ? 0 : item.getQuantityCount();
                item.setQuantityCount(currentBuyCount + itemRequest.getQuantity());
                item.setQuantity(item.getQuantity() - itemRequest.getQuantity());
                itemRepo.save(item);

                User shop = orderDetail.getItem().getUser();
                shop.setUserWallet(shop.getUserWallet() + orderDetail.getPriceForShop());
                userRepo.save(shop);
                var shopPay = shopPayRepo.findByMonthAndYearAndUser(month, year,shop).orElse(null);
                if (shopPay != null) {
                    shopPay.setMoney(shopPay.getMoney() + orderDetail.getPriceForShop());
                    shopPayRepo.save(shopPay);
                } else {
                    ShopPay newPay = ShopPay.builder()
                            .month(month)
                            .year(year)
                            .money(orderDetail.getPriceForShop())
                            .status(false)
                            .user(orderDetail.getItem().getUser())
                            .build();
                    shopPayRepo.save(newPay);
                }

            }

            // Save order details
            orderDetailRepo.saveAll(orderDetails);

            // Generate order code
            String currentTimeString = String.valueOf(new Date().getTime());
            int orderCode = Integer.parseInt(currentTimeString.substring(currentTimeString.length() - 6));

            List<ItemData> itemList = orderDetails.stream().map(orderDetail -> new ItemData(
                    orderDetail.getItem().getItemName(),
                    orderDetail.getQuantity(),
                    (int) orderDetail.getPrice()
            )).collect(Collectors.toList());

            PaymentData paymentData = new PaymentData(orderCode, (int) order.getTotal(), description, itemList, cancelUrl, returnUrl);

            JsonNode data = payOS.createPaymentLink(paymentData);

            response.put("error", 0);
            response.put("message", "success");
            response.set("data", data);

            order.setOrderCode(orderCode);
            orderRepo.save(order);
            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", "Internal Server Error");
            response.set("data", null);
            return ResponseEntity.status(500).body(response);
        }
    }


    @Override
    public ResponseEntity<PayOSSuccess> Success(int orderCode) {
        var order = orderRepo.findByOrderCode(orderCode).orElse(null);
        if (order == null) {
            return ResponseEntity.status(404).body(new PayOSSuccess("Order Not Found", "https://rosaceae.id.vn/order"));
        } else {
            order.setOrderStatus(OrderStatus.DELIVERED);
            orderRepo.save(order);
            return ResponseEntity.ok(new PayOSSuccess("Order is successfully delivered", "https://rosaceae.id.vn/order"));
        }
    }

    @Override
    public ResponseEntity<PayOSCancel> Cancel(int orderCode) {
        var order = orderRepo.findByOrderCode(orderCode).orElse(null);
        if (order == null) {
            return ResponseEntity.status(404).body(new PayOSCancel("Order Not Found", "https://rosaceae.id.vn/"));
        } else {
            if (order.getOrderStatus().equals(OrderStatus.CANCELLED)) {
                return ResponseEntity.ok(new PayOSCancel("Order Cancelled", "https://rosaceae.id.vn/"));
            } else {
                order.setOrderStatus(OrderStatus.CANCELLED);
                orderRepo.save(order);
                List<OrderDetail> list = order.getOrderDetails().stream().toList();
                for (OrderDetail orderDetail : list) {

                    int quantity = orderDetail.getQuantity();
                    float priceForShop = orderDetail.getPriceForShop();
                    var shop = orderDetail.getItem().getUser();
                    var item = orderDetail.getItem();

                    LocalDateTime orderDate = orderDetail.getOrder().getOrderDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                    // Lấy tháng và năm
                    int month = orderDate.getMonthValue();
                    int year = orderDate.getYear();

                    var shopPay = shopPayRepo.findByMonthAndYearAndUser(month, year,shop).orElse(null);
                    assert shopPay != null;
                    shopPay.setMoney(shopPay.getMoney() - priceForShop);
                    item.setQuantity(item.getQuantity() + quantity);
                    item.setQuantityCount(item.getQuantityCount() - quantity);
                    shop.setUserWallet(shop.getUserWallet() - priceForShop);
                    shopPayRepo.save(shopPay);
                    userRepo.save(shop);
                    itemRepo.save(item);
                }
                return ResponseEntity.ok(new PayOSCancel("Order Cancelled", "https://rosaceae.id.vn/"));
            }

        }
    }
}
