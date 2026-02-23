package com.cotaansbah.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class OrderDto {
    private UUID id;
    private BigDecimal amount;
    private LocalDateTime createDateTimeMsk;
}
