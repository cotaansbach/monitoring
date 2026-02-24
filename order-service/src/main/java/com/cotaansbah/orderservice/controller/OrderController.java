package com.cotaansbah.orderservice.controller;

import com.cotaansbah.orderservice.dto.OrderDto;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final List<OrderDto> ORDER_DTOS = new CopyOnWriteArrayList<>();
    private static final String CREATED_ORDERS_COUNTER_NAME = "orders.create";

    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void init() {
        var orders = List.of(
                new OrderDto(UUID.randomUUID(), BigDecimal.valueOf(10023.8), LocalDateTime.of(2026, 2, 21, 8, 23)),
                new OrderDto(UUID.randomUUID(), BigDecimal.valueOf(543.9), LocalDateTime.of(2026, 1, 8, 22, 1)),
                new OrderDto(UUID.randomUUID(), BigDecimal.valueOf(19900), LocalDateTime.of(2025, 12, 3, 17, 6))
        );
        ORDER_DTOS.addAll(orders);
        meterRegistry.counter(CREATED_ORDERS_COUNTER_NAME).increment(0);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> getOrders() {
        return ORDER_DTOS;
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        if (!isOrderValid(orderDto)) {
            return ResponseEntity.badRequest().build();
        }
        orderDto.setId(UUID.randomUUID());
        orderDto.setCreateDateTimeMsk(LocalDateTime.now(ZoneId.of("Europe/Moscow")));
        ORDER_DTOS.add(orderDto);
        meterRegistry.counter(CREATED_ORDERS_COUNTER_NAME).increment();
        if (Math.random() < 0.3) {
            log.error("Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
    }


    private boolean isOrderValid(OrderDto orderDto) {
        return orderDto.getAmount() != null;
    }
}
